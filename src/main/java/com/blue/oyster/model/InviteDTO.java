package com.blue.oyster.model;

import java.time.Instant;
import java.util.UUID;

public record InviteDTO(UUID id, UUID qr, UUID guestId, Instant createdAt, Instant updatedAt, Instant usedAt) {
}
