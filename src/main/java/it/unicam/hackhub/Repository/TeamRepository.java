package it.unicam.hackhub.repository;

import it.unicam.hackhub.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByName(String name);

    // Poiché 'members' è presumibilmente una collezione (@ElementCollection o @OneToMany)
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Team t WHERE :userId MEMBER OF t.members")
    boolean existsByMemberId(@Param("userId") Long userId);

    @Query("SELECT t FROM Team t WHERE :userId MEMBER OF t.members")
    Team findByMemberId(@Param("userId") Long userId);

    Team findByLeaderId(Long userId);
}