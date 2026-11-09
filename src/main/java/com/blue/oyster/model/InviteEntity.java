package com.blue.oyster.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invites_qr")
public class InviteEntity {

    @Id
    @Column(name = "qr", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID qr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_guest", nullable = false)
    private GuestEntity guest;

    @Column(name = "used_at")
    private Instant usedAt;

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
