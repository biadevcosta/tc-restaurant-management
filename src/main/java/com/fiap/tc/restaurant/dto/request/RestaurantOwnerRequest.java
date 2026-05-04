package com.fiap.tc.restaurant.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RestaurantOwnerRequest(
        @NotBlank @Schema(description = "Full name", example = "João Silva") String name,
        @NotBlank @Email @Schema(description = "Unique email", example = "joao@restaurante.com") String email,
        @NotBlank @Schema(description = "Unique login", example = "joaosilva") String login,
        @NotBlank @Size(min = 8) @Schema(description = "Password (minimum 8 characters)", example = "password123") String password,
        @NotNull @Valid @Schema(description = "Restaurant owner address") AddressRequest address
) {}
