package com.blue.oyster;

import com.blue.oyster.model.GuestDTO;
import com.blue.oyster.repository.GuestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Testcontainers
@Sql(scripts = "/test-data/add-guests.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/clean-guests.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
class GuestsContainerAndTests {

    @Container
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    GuestRepository guestRepository;

    @Autowired
    TestRestTemplate testRestTemplate;

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
    }

    @Test
    void addGuest() {
        GuestDTO newGuest = new GuestDTO("Райан", "Гослинг");

        ResponseEntity<Void> response = testRestTemplate.postForEntity(
                "/api/v1/guest",
                newGuest,
                Void.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertTrue(guestRepository.findByFirstNameAndLastName("Райан", "Гослинг").isPresent());

    }

    @Test
    void renameGuest() {
        GuestDTO guest = new GuestDTO("Покупатель", "Эклипсов");

        ResponseEntity<Void> response = testRestTemplate.postForEntity(
                "/api/v1/guest/37c1f40d-801b-42e9-be7b-6013ddd5dae4",
                guest,
                Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(guestRepository.findByFirstNameAndLastName("Покупатель", "Эклипсов").isPresent());
    }


    @Test
    void deleteGuest() {
        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/v1/guest/37c1f40d-801b-42e9-be7b-6013ddd5dae4",
                HttpMethod.DELETE,
                null,
                Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(guestRepository.findByFirstNameAndLastName("Покупатель", "Черностоев").isPresent());
    }

}
