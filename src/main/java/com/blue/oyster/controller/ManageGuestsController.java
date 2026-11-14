package com.blue.oyster.controller;

import com.blue.oyster.model.GuestDTO;
import com.blue.oyster.service.GuestsManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1")
public class ManageGuestsController {


    private final GuestsManagementService guestsManagementService;

    public ManageGuestsController(GuestsManagementService guestsManagementService) {
        this.guestsManagementService = guestsManagementService;
    }

    @GetMapping("/guest")
    public List<GuestDTO> findGuests(@RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName) {
        return guestsManagementService.findGuests(firstName, lastName);
    }

    @PostMapping("/guest")
    @ResponseStatus(HttpStatus.CREATED)
    public void addGuest(@RequestBody GuestDTO guest) {
        guestsManagementService.addGuest(guest);
    }

    @PutMapping("/guest/{guestId}")
    public void renameGuest(@PathVariable UUID guestId, @RequestBody GuestDTO guest) {
        guestsManagementService.changeGuestName(guestId, guest);
    }

    @DeleteMapping("/guest/{guestId}")
    public void deleteGuest(@PathVariable UUID guestId) {
        guestsManagementService.deleteGuest(guestId);
    }
}
