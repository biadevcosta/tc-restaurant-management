package com.fiap.tc.restaurant.service;

import com.fiap.tc.restaurant.dto.request.RestaurantOwnerRequest;
import com.fiap.tc.restaurant.dto.request.UpdatePasswordRequest;
import com.fiap.tc.restaurant.dto.request.UpdateUserRequest;
import com.fiap.tc.restaurant.dto.response.UserResponse;

import java.util.List;

public interface RestaurantOwnerService {

    UserResponse create(RestaurantOwnerRequest request);

    UserResponse findById(Long id);

    List<UserResponse> findByName(String name);

    void delete(Long id);

    UserResponse update(Long id, UpdateUserRequest dto);

    void updatePassword(Long id, UpdatePasswordRequest dto);
}
