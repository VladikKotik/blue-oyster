package com.blue.oyster;

import com.blue.oyster.model.GuestDTO;
import com.blue.oyster.model.GuestEntity;
import com.blue.oyster.repository.GuestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//@SpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Testcontainers
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


    @BeforeEach
    void addGuests(){
        GuestEntity newGuest = new GuestEntity();
        newGuest.setFirstName("Улов");
        newGuest.setLastName("Кальмаров");
        newGuest.setCreatedAt(Instant.now());
        guestRepository.save(newGuest);


        GuestEntity newGuest2 = new GuestEntity();
        newGuest2.setFirstName("Поджог");
        newGuest2.setLastName("Амбаров");
        newGuest2.setCreatedAt(Instant.now());
        guestRepository.save(newGuest2);

    }

    @AfterEach
    void deleteGuests(){
        guestRepository.deleteAll();
    }

    @Test
    void getGuest() {
        ResponseEntity<List<GuestDTO>> response = testRestTemplate.exchange(
                "/api/v1/guest?firstName=Улов",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<GuestDTO>>() {});
            assertEquals(HttpStatus.OK, response.getStatusCode());

            List<GuestDTO> guests = response.getBody();
            assertNotNull(guests);
            assertEquals(1, guests.size());
            assertEquals("Кальмаров", guests.get(0).lastName());
    }
}
