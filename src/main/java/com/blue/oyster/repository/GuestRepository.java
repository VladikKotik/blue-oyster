package com.blue.oyster.repository;

import com.blue.oyster.model.GuestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GuestRepository extends JpaRepository<GuestEntity, UUID>, JpaSpecificationExecutor<GuestEntity> {

    Optional<GuestEntity> findByFirstNameAndLastName(String firstName, String lastName);

}
