package com.fiap.tc.restaurant.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @NotBlank @Schema(description = "Current password", example = "password123") String currentPassword,
        @NotBlank @Size(min = 8) @Schema(description = "New password (minimum 8 characters)", example = "newPassword456") String newPassword
) {
}
