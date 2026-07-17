package it.unicam.hackhub.controller;

import it.unicam.hackhub.model.Report;
import it.unicam.hackhub.model.dto.requestdto.CreateReportDTO;
import it.unicam.hackhub.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hackathons/{hackathonId}/reports")
public class ReportController {
    private final ReportService rs;
    public ReportController(ReportService rs) { this.rs = rs; }

    @PostMapping("/participating-teams/{teamId}")
    public ResponseEntity<String> create(@RequestHeader("X-Staff-Id") Long sId, @PathVariable Long hackathonId, @PathVariable Long teamId, @RequestBody CreateReportDTO dto) {
        rs.createReport(sId, hackathonId, teamId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Segnalazione inviata.");
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<Report> getDetails(@RequestHeader("X-Staff-Id") Long sId, @PathVariable Long hackathonId, @PathVariable Long reportId) {
        return ResponseEntity.ok(rs.getReportDetails(sId, hackathonId, reportId));
    }
}