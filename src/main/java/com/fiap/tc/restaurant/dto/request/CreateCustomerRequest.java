package com.fiap.tc.restaurant.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
        @NotBlank @Schema(description = "Nome completo", example = "Maria Souza") String name,
        @NotBlank @Email @Schema(description = "E-mail único", example = "maria@email.com") String email,
        @NotBlank @Schema(description = "Login único", example = "mariasouza") String login,
        @NotBlank @Size(min = 8) @Schema(description = "Senha (mínimo 8 caracteres)", example = "senha123") String password,
        @NotNull @Valid @Schema(description = "Endereço do cliente") AddressRequest address
) {}
