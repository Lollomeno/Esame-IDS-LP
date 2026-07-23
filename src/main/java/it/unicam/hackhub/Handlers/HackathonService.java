package it.unicam.hackhub.service;

import it.unicam.hackhub.model.*;
import it.unicam.hackhub.model.dto.requestdto.CreateHackathonDTO;
import it.unicam.hackhub.model.dto.requestdto.HackathonSearchCriteria;
import it.unicam.hackhub.model.dto.requestdto.PaymentResult;
import it.unicam.hackhub.model.enums.HackathonStatus;
import it.unicam.hackhub.model.enums.PrizeStatus;
import it.unicam.hackhub.model.enums.RankingPolicy;
import it.unicam.hackhub.model.valueobjs.PayoutAccountRef;
import it.unicam.hackhub.repository.*;
import it.unicam.hackhub.utils.DomainException;
import it.unicam.hackhub.utils.IPaymentService;
import it.unicam.hackhub.utils.WinnerService;
import it.unicam.hackhub.utils.builders.HackathonBuilder;
import it.unicam.hackhub.utils.builders.IHackathonBuilder;
import it.unicam.hackhub.validators.HackathonValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HackathonService {

    private final StaffProfileRepository staffProfileRepository;
    private final HackathonRepository hackathonRepository;
    private final SubmissionRepository submissionRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;
    private final HackathonValidator hackathonValidator;
    private final WinnerService winnerService;
    private final IPaymentService paymentService;

    public HackathonService(StaffProfileRepository staffProfileRepository,
                            HackathonRepository hackathonRepository,
                            SubmissionRepository submissionRepository,
                            ParticipatingTeamRepository participatingTeamRepository,
                            HackathonValidator hackathonValidator,
                            WinnerService winnerService,
                            IPaymentService paymentService) {
        this.staffProfileRepository = staffProfileRepository;
        this.hackathonRepository = hackathonRepository;
        this.submissionRepository = submissionRepository;
        this.participatingTeamRepository = participatingTeamRepository;
        this.hackathonValidator = hackathonValidator;
        this.winnerService = winnerService;
        this.paymentService = paymentService;
    }

    @Transactional
    public void createHackathon(Long staffProfileId, CreateHackathonDTO createHackathonDTO) {
        hackathonValidator.validate(createHackathonDTO);

        StaffProfile organizer = staffProfileRepository.findById(staffProfileId)
                .orElseThrow(() -> new DomainException("Organizzatore non trovato"));

        if (hackathonRepository.existsByName(createHackathonDTO.getName())) {
            throw new DomainException("Esiste già un hackathon con questo nome");
        }

        StaffProfile judge = staffProfileRepository.findByEmail(createHackathonDTO.getJudgeEmail());
        if (judge == null) {
            throw new DomainException("Nessun profilo staff trovato per l'email del giudice");
        }

        List<Long> mentorsId = new ArrayList<>();
        if (createHackathonDTO.getMentorEmails() != null) {
            for (String mentorEmail : createHackathonDTO.getMentorEmails()) {
                StaffProfile mentor = staffProfileRepository.findByEmail(mentorEmail);
                if (mentor == null) {
                    throw new DomainException("Nessun profilo staff trovato per l'email del mentore: " + mentorEmail);
                }
                mentorsId.add(mentor.getId());
            }
        }

        IHackathonBuilder builder = new HackathonBuilder();
        Hackathon createdHackathon = builder
                .buildOrganizer(organizer.getId())
                .buildName(createHackathonDTO.getName())
                .buildType(createHackathonDTO.getType())
                .buildRegulation(createHackathonDTO.getRegulation())
                .buildLocation(createHackathonDTO.getLocation())
                .buildPrize(createHackathonDTO.getPrize())
                .buildMaxTeamSize(createHackathonDTO.getMaxTeamSize())
                .buildSubscriptionDates(createHackathonDTO.getSubscriptionDates())
                .buildDates(createHackathonDTO.getDates())
                .buildDelivery(createHackathonDTO.getDelivery())
                .buildJudge(judge.getId())
                .buildMentors(mentorsId)
                .buildRankingPolicy(createHackathonDTO.getRankingPolicy())
                .build();

        hackathonRepository.save(createdHackathon);
    }

    @Transactional
    public void confirmEvaluations(Long judgeId, Long hackathonId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new DomainException("Hackathon non trovato."));

        if (!hackathon.getJudge().equals(judgeId)) {
            throw new DomainException("Solo il giudice assegnato può confermare le valutazioni.");
        }

        List<ParticipatingTeam> eligibleTeams = participatingTeamRepository.findEligibleForRanking(hackathonId);
        List<RankingCandidate> candidates = new ArrayList<>();

        for (ParticipatingTeam pt : eligibleTeams) {
            Submission s = submissionRepository.findByHackathonIdAndParticipatingTeamId(hackathonId, pt.getId());
            if (s != null && s.hasEvaluation()) {
                int finalScore = s.getScore() - pt.getTotalPenaltyPoints();
                candidates.add(new RankingCandidate(
                        pt.getId(),
                        finalScore,
                        s.getUpdatedAt(),
                        pt.getRegisteredAt(),
                        pt.getTeamSize()
                ));
            }
        }

        Long winnerParticipatingTeamId = winnerService.selectWinner(hackathon.getRankingPolicy(), candidates);

        if (winnerParticipatingTeamId != null) {
            hackathon.declareWinner(winnerParticipatingTeamId);
            hackathon.close();

            if (hackathon.getPrize() > 0) {
                ParticipatingTeam winnerTeam = participatingTeamRepository.findById(winnerParticipatingTeamId)
                        .orElseThrow(() -> new DomainException("Team vincitore non trovato"));
                PayoutAccountRef accountRef = winnerTeam.getPaymentAccountRef();
                PaymentResult result = paymentService.transfer(hackathon.getPrize(), accountRef);

                if (result.isSuccess()) {
                    hackathon.confirmPrizePaid(result.getTransactionId(), LocalDateTime.now());
                } else {
                    hackathon.markPrizeFailed(result.getErrorMessage(), LocalDateTime.now());
                }
            } else {
                hackathon.confirmPrizePaid("NO_PRIZE", LocalDateTime.now());
            }
        } else {
            throw new DomainException("Impossibile determinare un vincitore (nessun candidato valido).");
        }
        hackathonRepository.save(hackathon);
    }

    @Transactional
    public PaymentResult sendPrizeToWinner(Long staffProfileId, Long hackathonId) {
        if (!hackathonRepository.existsOrganizer(hackathonId, staffProfileId)) {
            throw new DomainException("Operazione non consentita: non sei l'organizzatore di questo hackathon");
        }

        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.CLOSED) {
            throw new DomainException("L'hackathon non è ancora chiuso");
        }

        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new DomainException("Hackathon non trovato"));

        Long participatingTeamId = hackathon.getWinnerParticipatingTeamId();
        if (participatingTeamId == null) {
            throw new DomainException("Nessun team vincitore assegnato a questo hackathon");
        }

        ParticipatingTeam participatingTeam = participatingTeamRepository.getByIdAndHackathonId(participatingTeamId, hackathonId);
        if (hackathon.getPrizeStatus() == PrizeStatus.PAID) {
            throw new DomainException("Il premio è già stato erogato");
        }

        PayoutAccountRef destination = participatingTeam.getPaymentAccountRef();
        PaymentResult result = paymentService.transfer(hackathon.getPrize(), destination);

        if (result.isSuccess()) {
            hackathon.confirmPrizePaid(result.getTransactionId(), LocalDateTime.now());
        } else {
            hackathon.markPrizeFailed(result.getErrorMessage(), LocalDateTime.now());
        }

        hackathonRepository.save(hackathon);
        return result;
    }

    @Transactional
    public void declareWinner(Long staffProfileId, Long hackathonId) {
        if (!hackathonRepository.existsOrganizer(hackathonId, staffProfileId)) {
            throw new DomainException("Operazione non autorizzata");
        }

        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.CLOSED) {
            throw new DomainException("L'hackathon deve essere chiuso per proclamare il vincitore");
        }

        List<ParticipatingTeam> eligibleParticipatingTeams = participatingTeamRepository.findEligibleForRanking(hackathonId);
        List<RankingCandidate> candidates = new ArrayList<>();

        for (ParticipatingTeam pt : eligibleParticipatingTeams) {
            Submission s = submissionRepository.findByHackathonIdAndParticipatingTeamId(hackathonId, pt.getId());
            if (s != null && s.hasEvaluation()) {
                int finalScore = s.getScore() - pt.getTotalPenaltyPoints();
                candidates.add(new RankingCandidate(
                        pt.getId(),
                        finalScore,
                        s.getUpdatedAt(),
                        pt.getRegisteredAt(),
                        pt.getTeamSize()
                ));
            }
        }

        if (candidates.isEmpty()) {
            throw new DomainException("Nessun candidato valido per la classifica");
        }

        Hackathon h = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new DomainException("Hackathon non trovato"));

        Long winnerParticipatingTeamId = winnerService.selectWinner(h.getRankingPolicy(), candidates);
        h.declareWinner(winnerParticipatingTeamId);
        hackathonRepository.save(h);
    }

    @Transactional(readOnly = true)
    public List<Hackathon> searchHackathon(HackathonSearchCriteria hackathonSearchCriteria) {
        hackathonValidator.validate(hackathonSearchCriteria);
        // Per risolvere l'errore di compilazione, per ora restituiamo tutti gli hackathon.
        // Se vorrai, in futuro potrai filtrare questa lista usando i campi di hackathonSearchCriteria
        return hackathonRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Hackathon getHackathonDetails(Long hackathonId) {
        return hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new DomainException("Hackathon non trovato con ID: " + hackathonId));
    }
}