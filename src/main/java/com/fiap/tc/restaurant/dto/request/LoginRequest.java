package com.fiap.tc.restaurant.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Schema(description = "User login", example = "joaosilva") String login,
        @NotBlank @Schema(description = "User password", example = "password123") String password
) {
}
