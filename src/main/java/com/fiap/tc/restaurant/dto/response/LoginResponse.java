package com.fiap.tc.restaurant.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "JWT Bearer token") String token,
        @Schema(description = "ID do usuário autenticado", example = "1") Long userId,
        @Schema(description = "Tipo do usuário", example = "CUSTOMER", allowableValues = {"CUSTOMER", "RESTAURANT_OWNER"}) String userType
) {
}
