package com.fiap.tc.restaurant.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @NotBlank @Schema(description = "Senha atual", example = "senha123") String currentPassword,
        @NotBlank @Size(min = 8) @Schema(description = "Nova senha (mínimo 8 caracteres)", example = "novaSenha456") String newPassword
) {
}
