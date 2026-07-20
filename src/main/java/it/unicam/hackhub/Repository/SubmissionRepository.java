package it.unicam.hackhub.repository;

import it.unicam.hackhub.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Submission findByParticipatingTeamId(Long participatingTeamId);

    boolean existsByParticipatingTeamId(Long participatingTeamId);

    List<Submission> findByHackathonId(Long hackathonId);

    Submission getByIdAndHackathonId(Long id, Long hackathonId);

    boolean existsByHackathonIdAndEvaluationIsNull(Long hackathonId);

    Submission findByHackathonIdAndParticipatingTeamId(Long hackathonId, Long participatingTeamId);

    // Nota: Il metodo getRankingCandidates non dovrebbe stare qui. RankingCandidate non è un'entità JPA (Submission).
    // Questo metodo va spostato e gestito tramite una join in ParticipatingTeamRepository o come query nativa/DTO projection.
    // L'ho rimosso da questa interfaccia per evitare errori all'avvio dell'applicazione.
}