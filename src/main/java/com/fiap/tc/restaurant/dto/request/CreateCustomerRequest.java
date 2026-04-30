package com.fiap.tc.restaurant.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String login,
        @NotBlank @Size(min = 8) String password,
        @NotNull @Valid AddressRequest address
) {}
