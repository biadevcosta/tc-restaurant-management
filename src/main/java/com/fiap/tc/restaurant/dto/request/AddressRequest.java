package com.fiap.tc.restaurant.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @NotBlank String street,
        @NotBlank String number,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String zipCode
) {}
