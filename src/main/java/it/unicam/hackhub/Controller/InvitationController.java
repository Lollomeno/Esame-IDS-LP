package it.unicam.hackhub.controller;

import it.unicam.hackhub.model.Invitation;
import it.unicam.hackhub.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {
    private final InvitationService is;
    public InvitationController(InvitationService is) { this.is = is; }

    @PostMapping("/invite/{inviteeId}")
    public ResponseEntity<Invitation> invite(@RequestHeader("X-User-Id") Long uId, @PathVariable Long inviteeId) {
        return ResponseEntity.ok(is.inviteUser(uId, inviteeId));
    }

    @PostMapping("/{invId}/accept")
    public ResponseEntity<String> accept(@RequestHeader("X-User-Id") Long uId, @PathVariable Long invId) {
        is.acceptInvitation(uId, invId);
        return ResponseEntity.ok("Accettato.");
    }
}