package com.blue.oyster.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invites_qr")
public class InviteEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "qr", nullable = false)
    private UUID qr;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_guest", nullable = false)
    private GuestEntity guest;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "used_at")
    private Instant usedAt;

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return usedAt == null;
    }

    public void markAsUsed() {
        this.usedAt = Instant.now();
    }

    public GuestEntity getGuest() {
        return guest;
    }

    public void setQr(UUID qr) {
        this.qr = qr;
    }

    public void setGuest(GuestEntity guest) {
        this.guest = guest;
    }

    public UUID getQr() {
        return qr;
    }


}
