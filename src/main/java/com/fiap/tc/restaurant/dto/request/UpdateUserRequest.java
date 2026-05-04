package com.fiap.tc.restaurant.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
        @NotBlank @Schema(description = "New full name", example = "Maria Souza Updated") String name,
        @NotBlank @Email @Schema(description = "New unique email", example = "maria.new@email.com") String email,
        @NotNull @Valid @Schema(description = "New address") AddressRequest address
) {
}
