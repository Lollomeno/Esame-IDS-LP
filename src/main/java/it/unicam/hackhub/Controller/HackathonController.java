package it.unicam.hackhub.controller;

import it.unicam.hackhub.model.Hackathon;
import it.unicam.hackhub.model.dto.requestdto.*;
import it.unicam.hackhub.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/hackathons")
public class HackathonController {
    private final HackathonService hs;
    private final ParticipatingTeamService pts;
    public HackathonController(HackathonService hs, ParticipatingTeamService pts) { this.hs = hs; this.pts = pts; }

    @PostMapping
    public ResponseEntity<String> create(@RequestHeader("X-Staff-Id") Long sId, @RequestBody CreateHackathonDTO dto) {
        hs.createHackathon(sId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Hackathon creato.");
    }

    @GetMapping("/search")
    public ResponseEntity<List<Hackathon>> search(HackathonSearchCriteria criteria) {
        return ResponseEntity.ok(hs.searchHackathon(criteria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hackathon> getDetails(@PathVariable Long id) {
        return ResponseEntity.ok(hs.getHackathonDetails(id));
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<String> register(@RequestHeader("X-User-Id") Long uId, @PathVariable Long id, @RequestBody RegisterTeamDTO dto) {
        pts.registerTeamToHackathon(uId, id, dto);
        return ResponseEntity.ok("Iscritto.");
    }

    @PostMapping("/{id}/declare-winner")
    public ResponseEntity<String> declareWinner(@RequestHeader("X-Staff-Id") Long sId, @PathVariable Long id) {
        hs.declareWinner(sId, id);
        return ResponseEntity.ok("Vincitore proclamato.");
    }
}