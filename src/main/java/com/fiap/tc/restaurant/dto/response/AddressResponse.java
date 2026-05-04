package com.fiap.tc.restaurant.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AddressResponse(
        @Schema(description = "Street name", example = "Av. Paulista") String street,
        @Schema(description = "Street number", example = "1000") String number,
        @Schema(description = "City", example = "São Paulo") String city,
        @Schema(description = "State abbreviation", example = "SP") String state,
        @Schema(description = "Zip code", example = "01310-200") String zipCode
) {}
