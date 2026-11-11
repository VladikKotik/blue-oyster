package com.blue.oyster.controller;

import com.blue.oyster.model.InviteEntity;
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

    //так не надо, но предположим, как будто эндпоинт защищен и нам нужна вся его служебная информация
    @GetMapping("invitation/{invitationId}")
    public InviteEntity getInvitation(@PathVariable UUID invitationId) {
        return guestsManagementService.findInvitationById(invitationId);
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
    public UUID inviteGuest(@PathVariable UUID guestId) {
        return guestsManagementService.inviteGuest(guestId);
    }
}
