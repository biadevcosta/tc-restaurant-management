package com.fiap.tc.restaurant.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "JWT Bearer token") String token,
        @Schema(description = "Authenticated user ID", example = "1") Long userId,
        @Schema(description = "User type", example = "CUSTOMER", allowableValues = {"CUSTOMER", "RESTAURANT_OWNER"}) String userType
) {
}
