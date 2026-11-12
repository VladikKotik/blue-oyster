package com.blue.oyster.controller;

import com.blue.oyster.model.GuestDTO;
import com.blue.oyster.service.GuestsManagementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1")
public class BlueOysterClubRestController {

    private final GuestsManagementService guestsManagementService;

    public BlueOysterClubRestController(GuestsManagementService guestsManagementService) {
        this.guestsManagementService = guestsManagementService;
    }


    @GetMapping("/entry-the-club/{qrCode}")
    public GuestDTO entryTheClub(@PathVariable UUID qrCode) {
        return guestsManagementService.entryTheClub(qrCode);
    }

}
