package it.unicam.hackhub.controller;

import it.unicam.hackhub.model.dto.requestdto.AddEvaluationDTO;
import it.unicam.hackhub.service.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hackathons/{hackathonId}/submissions/{subId}/evaluations")
public class EvaluationController {
    private final EvaluationService es;
    public EvaluationController(EvaluationService es) { this.es = es; }

    @PostMapping
    public ResponseEntity<String> add(@RequestHeader("X-Staff-Id") Long sId, @PathVariable Long hackathonId, @PathVariable Long subId, @RequestBody AddEvaluationDTO dto) {
        es.addEvaluation(sId, hackathonId, subId, dto);
        return ResponseEntity.ok("Valutazione salvata.");
    }
}