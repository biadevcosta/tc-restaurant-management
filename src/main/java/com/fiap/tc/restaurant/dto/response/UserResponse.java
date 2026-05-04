package com.fiap.tc.restaurant.dto.response;

import com.fiap.tc.restaurant.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UserResponse(
        @Schema(description = "User ID", example = "1") Long id,
        @Schema(description = "Full name", example = "Maria Souza") String name,
        @Schema(description = "Email", example = "maria@email.com") String email,
        @Schema(description = "Login", example = "mariasouza") String login,
        @Schema(description = "User role") UserRole role,
        @Schema(description = "Last modification date/time") LocalDateTime lastModifiedAt,
        @Schema(description = "Address") AddressResponse address
) {}
