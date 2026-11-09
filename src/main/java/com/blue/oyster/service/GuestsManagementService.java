package com.blue.oyster.service;

import com.blue.oyster.model.GuestChangeDTO;
import com.blue.oyster.model.GuestDTO;
import com.blue.oyster.model.GuestEntity;
import com.blue.oyster.model.InviteEntity;
import com.blue.oyster.model.exceptions.ClearingHistoryException;
import com.blue.oyster.model.exceptions.GuestNotFoundException;
import com.blue.oyster.model.exceptions.InvalidQrCodeException;
import com.blue.oyster.repository.GuestRepository;
import com.blue.oyster.repository.InviteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GuestsManagementService {

    private final GuestRepository guestRepository;
    private final InviteRepository inviteRepository;

    public GuestsManagementService(GuestRepository guestRepository, InviteRepository inviteRepository) {
        this.guestRepository = guestRepository;
        this.inviteRepository = inviteRepository;
    }

    public void addGuest(String firstName, String lastName) {
        GuestEntity newGuest = new GuestEntity();
        newGuest.setFirstName(firstName);
        newGuest.setLastName(lastName);
        guestRepository.save(newGuest);
    }

    private GuestEntity findGuestByName(String firstName, String lastName) {
        return guestRepository.findByFirstNameAndLastName(firstName, lastName).orElseThrow(() -> new GuestNotFoundException("Гость не найден: " + firstName + " " + lastName));
    }


    public void banGuest(String firstName, String lastName) {
        GuestEntity guest = findGuestByName(firstName, lastName);
        guestRepository.delete(guest);
        // инвайты удаляем каскадно
    }

    @Transactional
    public void changeGuestName(GuestChangeDTO guestChange) {
        GuestEntity guest = findGuestByName(guestChange.oldFirstName(), guestChange.oldLastName());
        guest.setFirstName(guestChange.newFirstName());
        guest.setLastName(guestChange.newLastName());
    }

    @Transactional
    public GuestDTO entryTheClub(UUID qrCodeInvitation) {
        InviteEntity invitation = inviteRepository.findById(qrCodeInvitation).orElseThrow(() -> new InvalidQrCodeException("сегодня вас тут не ждут!!!"));
        if (!invitation.isActive()) throw new InvalidQrCodeException("сегодня вас тут не ждут!!!, qr code протух");
        GuestEntity guest = invitation.getGuest();
        invitation.markAsUsed();
        inviteGuestForNextTime(guest);
        return new GuestDTO(guest.getFirstName(), guest.getLastName());
    }

    public List<GuestDTO> getAllGuests() {
        return guestRepository.findAll().stream().map(g -> new GuestDTO(g.getFirstName(), g.getLastName())).toList();
    }

    public GuestDTO getGuestByName(String firstName, String lastName) {
        GuestEntity guest = findGuestByName(firstName, lastName);
        return new GuestDTO(guest.getFirstName(), guest.getLastName());
    }

    public void deleteInvitation(UUID qrCodeInvitation) {
        InviteEntity invitation = inviteRepository.findById(qrCodeInvitation).orElseThrow(() -> new InvalidQrCodeException("invitation not found"));
        if (invitation.isActive()) inviteRepository.delete(invitation);
        else throw new ClearingHistoryException("нельзя изменять историю посещений!");
    }


    @Transactional
    public void moveInvitationToOtherGuest(UUID qrCodeInvitation, String firstName, String lastName) {
        InviteEntity invitation = inviteRepository.findById(qrCodeInvitation).orElseThrow(() -> new InvalidQrCodeException("invitation not found"));
        if (!invitation.isActive()) {
            throw new ClearingHistoryException("нельзя изменять историю посещений!");
        } else {
            GuestEntity guest = findGuestByName(firstName, lastName);
            invitation.setGuest(guest);
            inviteRepository.save(invitation);
        }
    }

    @Transactional
    public UUID inviteGuest(String firstName, String lastName) {
        GuestEntity guest = findGuestByName(firstName, lastName);
        InviteEntity invitation = createInvitationHelper(guest);
        return invitation.getQr();
    }

    //qr код нельзя вертать по тз, и в след раз гость его будет брать из базы
    private void inviteGuestForNextTime(GuestEntity guest) {
        createInvitationHelper(guest);
    }

    private InviteEntity createInvitationHelper(GuestEntity guest) {
        inviteRepository.findAllByGuest_IdAndUsedAtIsNull(guest.getId()).forEach(InviteEntity::markAsUsed);
        inviteRepository.flush();

        InviteEntity invitation = new InviteEntity();
        invitation.setGuest(guest);
        inviteRepository.save(invitation);
        return invitation;
    }


}
