package it.unicam.hackhub.controller;

import it.unicam.hackhub.model.SupportRequest;
import it.unicam.hackhub.model.dto.requestdto.*;
import it.unicam.hackhub.service.SupportRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hackathons/{hackathonId}/support-requests")
public class SupportRequestController {
    private final SupportRequestService srs;
    public SupportRequestController(SupportRequestService srs) { this.srs = srs; }

    @PostMapping
    public ResponseEntity<String> create(@RequestHeader("X-User-Id") Long uId, @PathVariable Long hackathonId, @RequestBody CreateSupportRequestDTO dto) {
        srs.createSupportRequest(uId, hackathonId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Richiesta aperta.");
    }

    @GetMapping("/{reqId}")
    public ResponseEntity<SupportRequest> getDetails(@RequestHeader("X-Staff-Id") Long sId, @PathVariable Long hackathonId, @PathVariable Long reqId) {
        return ResponseEntity.ok(srs.getSupportRequestDetails(sId, hackathonId, reqId));
    }
}