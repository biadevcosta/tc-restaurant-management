package com.fiap.tc.restaurant.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotNull @Valid AddressRequest address
) {
}
