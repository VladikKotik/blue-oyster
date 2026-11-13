package com.blue.oyster.model;

public record GuestDTO(String firstName, String lastName) {

    public static GuestDTO entityToDto(GuestEntity entity) {
        return new GuestDTO(entity.getFirstName(), entity.getLastName());
    }
}
