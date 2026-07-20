package it.unicam.hackhub.repository;

import it.unicam.hackhub.model.ParticipatingTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipatingTeamRepository extends JpaRepository<ParticipatingTeam, Long> {

    // Nota: nel tuo codice originale cercavi "pt.team". Assicurati che l'attributo in ParticipatingTeam si chiami "teamId" se è un Long,
    // o "team" se è una relazione @ManyToOne. Ho usato "teamId" in base ai tuoi handler precedenti.
    boolean existsByHackathonIdAndTeamId(Long hackathonId, Long teamId);

    ParticipatingTeam findByHackathonIdAndTeamId(Long hackathonId, Long teamId);

    ParticipatingTeam getByIdAndHackathonId(Long id, Long hackathonId);

    // Supponendo che 'membersSnapshot' sia una collezione (@ElementCollection)
    @Query("SELECT pt FROM ParticipatingTeam pt WHERE pt.hackathonId = :hackathonId AND :userId MEMBER OF pt.membersSnapshot")
    ParticipatingTeam findByHackathonIdAndActiveMemberId(@Param("hackathonId") Long hackathonId, @Param("userId") Long userId);

    @Query("SELECT pt FROM ParticipatingTeam pt WHERE pt.hackathonId = :hackathonId AND pt.isDisqualified = false")
    List<ParticipatingTeam> findEligibleForRanking(@Param("hackathonId") Long hackathonId);
}