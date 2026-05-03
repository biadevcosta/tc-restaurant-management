package com.fiap.tc.restaurant.dto.response;

import com.fiap.tc.restaurant.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UserResponse(
        @Schema(description = "ID do usuário", example = "1") Long id,
        @Schema(description = "Nome completo", example = "Maria Souza") String name,
        @Schema(description = "E-mail", example = "maria@email.com") String email,
        @Schema(description = "Login", example = "mariasouza") String login,
        @Schema(description = "Role do usuário") UserRole role,
        @Schema(description = "Data/hora da última modificação") LocalDateTime lastModifiedAt,
        @Schema(description = "Endereço") AddressResponse address
) {}
