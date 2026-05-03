package com.fiap.tc.restaurant.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @NotBlank @Schema(description = "Nome da rua", example = "Rua das Flores") String street,
        @NotBlank @Schema(description = "Número", example = "123") String number,
        @NotBlank @Schema(description = "Cidade", example = "São Paulo") String city,
        @NotBlank @Schema(description = "Estado (sigla)", example = "SP") String state,
        @NotBlank @Schema(description = "CEP", example = "01310-100") String zipCode
) {}
