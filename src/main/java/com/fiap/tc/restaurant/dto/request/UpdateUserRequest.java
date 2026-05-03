package com.fiap.tc.restaurant.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
        @NotBlank @Schema(description = "Novo nome completo", example = "Maria Souza Atualizada") String name,
        @NotBlank @Email @Schema(description = "Novo e-mail único", example = "maria.nova@email.com") String email,
        @NotNull @Valid @Schema(description = "Novo endereço") AddressRequest address
) {
}
