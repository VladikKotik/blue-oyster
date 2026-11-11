package com.blue.oyster.service;

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

import java.time.Instant;
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

    public void addGuest(GuestDTO guest) {
        GuestEntity newGuest = new GuestEntity();
        newGuest.setFirstName(guest.firstName());
        newGuest.setLastName(guest.lastName());
        newGuest.setCreatedAt(Instant.now());
        guestRepository.save(newGuest);
    }


    public void deleteGuest(UUID guestId) {
        GuestEntity guest = findGuestById(guestId);
        guestRepository.delete(guest);
        // инвайты удаляем каскадно
    }

    @Transactional
    public void changeGuestName(UUID id, GuestDTO guestChange) {
        GuestEntity guest = findGuestById(id);
        guest.setFirstName(guestChange.firstName());
        guest.setLastName(guestChange.lastName());
        guest.setUpdatedAt(Instant.now());
    }

    @Transactional
    public GuestDTO entryTheClub(UUID qrCodeInvitation) {
        InviteEntity invitation = inviteRepository.findByQr(qrCodeInvitation)
                .orElseThrow(() -> new InvalidQrCodeException("данный qr code не найден"));
        if (!invitation.isActive()) {
            throw new InvalidQrCodeException("qr code не действителен, он уже был использован");
        }
        GuestEntity guest = invitation.getGuest();
        invitation.markAsUsed();
        invitation.setUpdatedAt(Instant.now());
        inviteGuestForNextTime(guest);
        return new GuestDTO(guest.getFirstName(), guest.getLastName());
    }

    public List<GuestDTO> findGuests(UUID id) {
        if (id == null) return findAllGuests();
        else return List.of(GuestDTO.entitryToDto(findGuestById(id)));
    }

    private List<GuestDTO> findAllGuests() {
        return guestRepository.findAll().stream().map(GuestDTO::entitryToDto).toList();
    }

    private GuestEntity findGuestById(UUID id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new GuestNotFoundException("Гость не найден: " + id));
    }


    public void deleteInvitation(UUID id) {
        InviteEntity invitation = findInvitationById(id);
        if (invitation.isActive()) inviteRepository.delete(invitation);
        else throw new ClearingHistoryException("нельзя изменять историю посещений!");
    }


    @Transactional
    public void moveInvitationToOtherGuest(UUID invitationId, UUID guestId) {
        InviteEntity invitation = findInvitationById(invitationId);
        if (!invitation.isActive()) {
            throw new ClearingHistoryException("нельзя изменять историю посещений!");
        } else {
            GuestEntity guest = findGuestById(guestId);
            invitation.setGuest(guest);
            invitation.setUpdatedAt(Instant.now());
            inviteRepository.save(invitation);
        }
    }

    public InviteEntity findInvitationById(UUID id) {
        return inviteRepository.findById(id)
                .orElseThrow(() -> new InvalidQrCodeException("invitation not found"));
    }

    @Transactional
    public UUID inviteGuest(UUID guestId) {
        GuestEntity guest = findGuestById(guestId);
        InviteEntity invitation = createInvitationHelper(guest);
        return invitation.getQr();
    }

    //qr код нельзя вертать по тз, и в след раз гость его будет брать из базы
    private void inviteGuestForNextTime(GuestEntity guest) {
        createInvitationHelper(guest);
    }

    private InviteEntity createInvitationHelper(GuestEntity guest) {
        inviteRepository.findAllByGuest_IdAndUsedAtIsNull(guest.getId()).forEach(InviteEntity::markAsUsed);
        InviteEntity invitation = new InviteEntity();
        invitation.setQr(UUID.randomUUID());
        invitation.setGuest(guest);
        invitation.setCreatedAt(Instant.now());
        inviteRepository.save(invitation);
        return invitation;
    }

}
