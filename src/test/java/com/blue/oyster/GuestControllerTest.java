package com.blue.oyster;

import com.blue.oyster.model.GuestDTO;
import com.blue.oyster.model.GuestEntity;
import com.blue.oyster.model.InviteEntity;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GuestControllerTest extends BlueOysterTestContainer {

    @Test
    void getGuestByFirstName() {
        ResponseEntity<List<GuestDTO>> response = testRestTemplate.exchange(
                "/api/v1/guest?firstName=Улов",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<GuestDTO>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<GuestDTO> guests = response.getBody();
        assertNotNull(guests);
        assertEquals(1, guests.size());
        assertEquals("Кальмаров", guests.get(0).lastName());
    }

    @Test
    void getGuestByLastName() {
        ResponseEntity<List<GuestDTO>> response = testRestTemplate.exchange(
                "/api/v1/guest?lastName=Кальмаров",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<GuestDTO>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<GuestDTO> guests = response.getBody();
        assertNotNull(guests);
        assertEquals(1, guests.size());
        assertEquals("Улов", guests.get(0).firstName());
    }

    @Test
    void getGuestByName() {
        ResponseEntity<List<GuestDTO>> response = testRestTemplate.exchange(
                "/api/v1/guest?firstName=Улов&lastName=Кальмаров",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<GuestDTO>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<GuestDTO> guests = response.getBody();
        assertNotNull(guests);
        assertEquals(1, guests.size());
        assertEquals("Кальмаров", guests.get(0).lastName());
    }


    @Test
    void getGuestByNoName() {
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/guest",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<String>() {
                });
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assert response.getBody() != null;
        assertTrue(response.getBody().contains("Не задан параметр для поиска гостя"));
    }

    @Test
    void addGuest() {
        GuestDTO newGuest = new GuestDTO("Райан", "Гослинг");

        ResponseEntity<Void> response = testRestTemplate.postForEntity(
                "/api/v1/guest",
                newGuest,
                Void.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        List<GuestEntity> guests = guestRepository.findAll();
        assertNotNull(guests.stream()
                .filter(g -> g.getFirstName().equals("Райан") && g.getLastName().equals("Гослинг"))
                .findAny());
    }

    @Test
    void renameGuest() {

        UUID guestId = UUID.fromString("37c1f40d-801b-42e9-be7b-6013ddd5dae4");

        GuestEntity guest = guestRepository.findById(guestId).orElseThrow();

        Instant updatedAtOld = guest.getUpdatedAt();

        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/v1/guest/" + guestId,
                HttpMethod.PUT,
                new HttpEntity<>(new GuestDTO("Покупатель", "Эклипсов")),
                Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertTrue(guestRepository.findById(guestId)
                .isPresent()
        );

        assertTrue(guestRepository.findById(guestId).get().getUpdatedAt().isAfter(updatedAtOld));
    }


    @Test
    void deleteGuest() {

        UUID guestId = UUID.fromString("37c1f40d-801b-42e9-be7b-6013ddd5dae4");

        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/v1/guest/" + guestId,
                HttpMethod.DELETE,
                null,
                Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertFalse(guestRepository.findById(guestId)
                .isPresent()
        );

        List<InviteEntity> invitations = inviteRepository.findAll();
        assertTrue(invitations.stream().filter(i -> i.getGuest().getId().equals(guestId)).findAny().isEmpty());

    }

}
