package com.fiap.tc.restaurant.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @NotBlank @Schema(description = "Street name", example = "Rua das Flores") String street,
        @NotBlank @Schema(description = "Street number", example = "123") String number,
        @NotBlank @Schema(description = "City", example = "São Paulo") String city,
        @NotBlank @Schema(description = "State abbreviation", example = "SP") String state,
        @NotBlank @Schema(description = "Zip code", example = "01310-100") String zipCode
) {}
