package com.fiap.tc.restaurant.service;

import com.fiap.tc.restaurant.dto.request.CreateCustomerRequest;
import com.fiap.tc.restaurant.dto.response.UserResponse;

import java.util.List;

public interface CustomerService {

    UserResponse create(CreateCustomerRequest dto);

    UserResponse findById(Long id);

    List<UserResponse> findByName(String name);

    void delete(Long id);
}
