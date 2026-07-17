package it.unicam.hackhub.controller;

import it.unicam.hackhub.model.Submission;
import it.unicam.hackhub.model.dto.requestdto.AddSubmissionDTO;
import it.unicam.hackhub.service.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hackathons/{hackathonId}/submissions")
public class SubmissionController {
    private final SubmissionService ss;
    public SubmissionController(SubmissionService ss) { this.ss = ss; }

    @PostMapping
    public ResponseEntity<String> create(@RequestHeader("X-User-Id") Long uId, @PathVariable Long hackathonId, @RequestBody AddSubmissionDTO dto) {
        ss.createSubmission(uId, hackathonId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Sottomissione caricata.");
    }

    @GetMapping("/{subId}")
    public ResponseEntity<Submission> getDetails(@RequestHeader("X-Staff-Id") Long sId, @PathVariable Long hackathonId, @PathVariable Long subId) {
        return ResponseEntity.ok(ss.getSubmissionDetails(sId, hackathonId, subId));
    }
}