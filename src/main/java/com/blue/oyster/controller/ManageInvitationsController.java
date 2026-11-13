package com.blue.oyster.controller;

import com.blue.oyster.model.InvitationQrDTO;
import com.blue.oyster.model.InviteDTO;
import com.blue.oyster.service.GuestsManagementService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1")
public class ManageInvitationsController {
    private final GuestsManagementService guestsManagementService;

    public ManageInvitationsController(GuestsManagementService guestsManagementService) {
        this.guestsManagementService = guestsManagementService;
    }

    @GetMapping("invitation/{invitationId}")
    public InviteDTO getInvitation(@PathVariable UUID invitationId) {
        return guestsManagementService.getInvitationById(invitationId);
    }

    @DeleteMapping("invitation/{invitationId}")
    public void deleteInvitation(@PathVariable UUID invitationId) {
        guestsManagementService.deleteInvitation(invitationId);
    }

    @PutMapping("invitation/{invitationId}/{guestId}")
    public void moveInvitationToOtherGuest(@PathVariable UUID qrCode, @PathVariable UUID guestId) {
        guestsManagementService.moveInvitationToOtherGuest(qrCode, guestId);
    }

    @PostMapping("invitation/{guestId}")
    public InvitationQrDTO inviteGuest(@PathVariable UUID guestId) {
        return guestsManagementService.inviteGuest(guestId);
    }
}
