package com.fiap.tc.restaurant.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Schema(description = "Login do usuário", example = "joaosilva") String login,
        @NotBlank @Schema(description = "Senha do usuário", example = "senha123") String password
) {
}
