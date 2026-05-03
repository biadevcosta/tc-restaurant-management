package com.fiap.tc.restaurant.service.impl;

import com.fiap.tc.restaurant.dto.request.RestaurantOwnerRequest;
import com.fiap.tc.restaurant.dto.request.UpdatePasswordRequest;
import com.fiap.tc.restaurant.dto.request.UpdateUserRequest;
import com.fiap.tc.restaurant.dto.response.UserResponse;
import com.fiap.tc.restaurant.enums.UserRole;
import com.fiap.tc.restaurant.exception.DuplicateUserException;
import com.fiap.tc.restaurant.exception.EmailAlreadyExistsException;
import com.fiap.tc.restaurant.exception.InvalidPasswordException;
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

    @Override
    public UserResponse update(Long id, UpdateUserRequest dto) {
        var owner = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Restaurant owner not found with id: " + id));

        repository.findByEmail(dto.email())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new EmailAlreadyExistsException("Email already in use: " + dto.email());
                });

        owner.setName(dto.name());
        owner.setEmail(dto.email());
        owner.getAddress().setStreet(dto.address().street());
        owner.getAddress().setNumber(dto.address().number());
        owner.getAddress().setCity(dto.address().city());
        owner.getAddress().setState(dto.address().state());
        owner.getAddress().setZipCode(dto.address().zipCode());
        owner.setLastModifiedAt(LocalDateTime.now());

        return RestaurantOwnerMapper.toResponse(repository.save(owner));
    }

    @Override
    public void updatePassword(Long id, UpdatePasswordRequest dto) {
        var owner = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Restaurant owner not found with id: " + id));

        if (!passwordEncoder.matches(dto.currentPassword(), owner.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        owner.setPassword(passwordEncoder.encode(dto.newPassword()));
        owner.setLastModifiedAt(LocalDateTime.now());
        repository.save(owner);
    }
}
