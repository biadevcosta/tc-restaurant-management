package com.fiap.tc.restaurant.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AddressResponse(
        @Schema(description = "Rua", example = "Av. Paulista") String street,
        @Schema(description = "Número", example = "1000") String number,
        @Schema(description = "Cidade", example = "São Paulo") String city,
        @Schema(description = "Estado", example = "SP") String state,
        @Schema(description = "CEP", example = "01310-200") String zipCode
) {}
