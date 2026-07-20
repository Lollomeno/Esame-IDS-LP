package it.unicam.hackhub.repository;

import it.unicam.hackhub.model.Invitation;
import it.unicam.hackhub.model.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Invitation i " +
           "WHERE i.teamId = :teamId AND i.invitee = :inviteeId AND i.status = :status")
    boolean existsPendingByTeamIdAndInviteeId(@Param("teamId") Long teamId, 
                                              @Param("inviteeId") Long inviteeId, 
                                              @Param("status") InvitationStatus status);

    Invitation findByIdAndInviteeIdAndStatus(Long id, Long inviteeId, InvitationStatus status);

    List<Invitation> findByInviteeId(Long userId);

    Invitation getByIdAndInviteeId(Long id, Long inviteeId);
}