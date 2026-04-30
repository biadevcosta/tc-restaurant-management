package com.fiap.tc.restaurant.service.impl;

import com.fiap.tc.restaurant.dto.request.RestaurantOwnerRequest;
import com.fiap.tc.restaurant.dto.response.UserResponse;
import com.fiap.tc.restaurant.enums.UserRole;
import com.fiap.tc.restaurant.exception.DuplicateUserException;
import com.fiap.tc.restaurant.exception.UserNotFoundException;
import com.fiap.tc.restaurant.mapper.RestaurantOwnerMapper;
import com.fiap.tc.restaurant.repository.RestaurantOwnerRepository;
import com.fiap.tc.restaurant.service.RestaurantOwnerService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RestaurantOwnerServiceImpl implements RestaurantOwnerService {

    private final RestaurantOwnerRepository repository;
    private final PasswordEncoder passwordEncoder;

    public RestaurantOwnerServiceImpl(RestaurantOwnerRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse create(RestaurantOwnerRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new DuplicateUserException("Email already in use: " + request.email());
        }
        if (repository.existsByLogin(request.login())) {
            throw new DuplicateUserException("Login already in use: " + request.login());
        }

        var owner = RestaurantOwnerMapper.toEntity(request);
        owner.setPassword(passwordEncoder.encode(request.password()));
        owner.setRole(UserRole.ROLE_RESTAURANT_OWNER);
        owner.setLastModifiedAt(LocalDateTime.now());

        return RestaurantOwnerMapper.toResponse(repository.save(owner));
    }

    @Override
    public UserResponse findById(Long id) {
        var owner = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Restaurant owner not found with id: " + id));
        return RestaurantOwnerMapper.toResponse(owner);
    }

    @Override
    public List<UserResponse> findByName(String name) {
        return repository.findByNameContainingIgnoreCase(name).stream()
                .map(RestaurantOwnerMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException("Restaurant owner not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
