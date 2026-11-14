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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//@SpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
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
        ResponseEntity<GuestDTO[]> response = testRestTemplate.getForEntity(
                    "/guest?firstName=Улов",
                    GuestDTO[].class);

            assertEquals(HttpStatus.OK, response.getStatusCode());

            GuestDTO[] guests = response.getBody();
            assertNotNull(guests);
            assertEquals(1, guests.length);
            assertEquals("Кальмаров", guests[0].lastName());
    }
}
