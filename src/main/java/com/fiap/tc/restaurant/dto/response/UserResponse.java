package com.fiap.tc.restaurant.dto.response;

import com.fiap.tc.restaurant.enums.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        String login,
        UserRole role,
        LocalDateTime lastModifiedAt,
        AddressResponse address
) {}
