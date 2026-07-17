package it.unicam.hackhub.controller;

import it.unicam.hackhub.model.dto.requestdto.CreateTeamDTO;
import it.unicam.hackhub.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamService teamService;
    public TeamController(TeamService teamService) { this.teamService = teamService; }

    @PostMapping
    public ResponseEntity<String> createTeam(@RequestHeader("X-User-Id") Long userId, @RequestBody CreateTeamDTO dto) {
        teamService.createTeam(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Team creato.");
    }
}