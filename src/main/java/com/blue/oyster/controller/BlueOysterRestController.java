package com.blue.oyster.controller;

import com.blue.oyster.model.GuestChangeDTO;
import com.blue.oyster.model.GuestDTO;
import com.blue.oyster.service.GuestsManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/blueOyster")
public class BlueOysterRestController {


    private final GuestsManagementService guestsManagementService;

    public BlueOysterRestController(GuestsManagementService guestsManagementService) {
        this.guestsManagementService = guestsManagementService;
    }

    @GetMapping("/guests")
    public List<GuestDTO> getAllGuests() {
        return guestsManagementService.getAllGuests();
    }

    @GetMapping("/guest/{firstName}/{lastName}")
    public GuestDTO findGuest(@PathVariable String firstName, @PathVariable String lastName) {
        return guestsManagementService.getGuestByName(firstName, lastName);
    }

    @PostMapping("/guest/{firstName}/{lastName}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addGuest(@PathVariable String firstName, @PathVariable String lastName) {
        guestsManagementService.addGuest(firstName, lastName);
    }

    @PutMapping("/guest")
    public void renameGuest(@RequestBody GuestChangeDTO guest) {
        guestsManagementService.changeGuestName(guest);
    }

    @DeleteMapping("/guest/{firstName}/{lastName}")
    public void banGuest(@PathVariable String firstName, @PathVariable String lastName) {
        guestsManagementService.banGuest(firstName, lastName);
    }

    @GetMapping("/entryTheClub/{qrCode}")
    public String entryTheClub(@PathVariable UUID qrCode) {
        GuestDTO guest = guestsManagementService.entryTheClub(qrCode);
        return "welcome, " + guest.firstName() + " " + guest.lastName();
    }

    @DeleteMapping("invitation/{qr}")
    public void deleteInvitation(@PathVariable UUID qrCode) {
        guestsManagementService.deleteInvitation(qrCode);
    }

    @PutMapping("invitation/{qr}/{firstName}/{lastName}")
    public void moveInvitationToOtherGuest(@PathVariable UUID qrCode, @PathVariable String firstName, @PathVariable String lastName) {
        guestsManagementService.moveInvitationToOtherGuest(qrCode, firstName, lastName);
    }

    @PostMapping("invitation/{firstName}/{lastName}")
    public UUID inviteGuest(@PathVariable String firstName, @PathVariable String lastName) {
        return guestsManagementService.inviteGuest(firstName, lastName);
    }
}
