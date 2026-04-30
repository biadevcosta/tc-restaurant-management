package com.fiap.tc.restaurant.dto.response;

public record AddressResponse(
        String street,
        String number,
        String city,
        String state,
        String zipCode
) {}
