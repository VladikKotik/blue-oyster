package com.blue.oyster.repository;

import com.blue.oyster.model.InviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InviteRepository extends JpaRepository<InviteEntity, UUID> {
    Optional<InviteEntity> findByQr(UUID qr);

    List<InviteEntity> findAllByGuest_IdAndUsedAtIsNull(UUID guestId);


}
