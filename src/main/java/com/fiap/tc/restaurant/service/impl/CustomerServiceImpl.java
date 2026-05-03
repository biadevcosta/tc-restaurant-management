package com.fiap.tc.restaurant.service.impl;

import com.fiap.tc.restaurant.domain.BaseUser;
import com.fiap.tc.restaurant.dto.request.CreateCustomerRequest;
import com.fiap.tc.restaurant.dto.request.UpdatePasswordRequest;
import com.fiap.tc.restaurant.dto.request.UpdateUserRequest;
import com.fiap.tc.restaurant.dto.response.UserResponse;
import com.fiap.tc.restaurant.enums.UserRole;
import com.fiap.tc.restaurant.exception.EmailAlreadyExistsException;
import com.fiap.tc.restaurant.exception.InvalidPasswordException;
import com.fiap.tc.restaurant.exception.LoginAlreadyExistsException;
import com.fiap.tc.restaurant.exception.UnauthorizedOperationException;
import com.fiap.tc.restaurant.exception.UserNotFoundException;
import com.fiap.tc.restaurant.mapper.CustomerMapper;
import com.fiap.tc.restaurant.repository.CustomerRepository;
import com.fiap.tc.restaurant.service.CustomerService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(CustomerRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse create(CreateCustomerRequest dto) {
        if (repository.findByEmail(dto.email()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already in use: " + dto.email());
        }
        if (repository.findByLogin(dto.login()).isPresent()) {
            throw new LoginAlreadyExistsException("Login already in use: " + dto.login());
        }

        var customer = CustomerMapper.toEntity(dto);
        customer.setRole(UserRole.ROLE_CUSTOMER);
        customer.setPassword(passwordEncoder.encode(dto.password()));
        customer.setLastModifiedAt(LocalDateTime.now());

        return CustomerMapper.toResponse(repository.save(customer));
    }

    @Override
    public UserResponse findById(Long id) {
        var customer = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Customer not found with id: " + id));
        return CustomerMapper.toResponse(customer);
    }

    @Override
    public List<UserResponse> findByName(String name) {
        return repository.findByNameContainingIgnoreCase(name).stream()
                .map(CustomerMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException("Customer not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest dto) {
        BaseUser authenticated = (BaseUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authenticated.getId().equals(id)) {
            throw new UnauthorizedOperationException("You can only modify your own data");
        }

        var customer = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Customer not found with id: " + id));

        repository.findByEmail(dto.email())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new EmailAlreadyExistsException("Email already in use: " + dto.email());
                });

        customer.setName(dto.name());
        customer.setEmail(dto.email());
        customer.getAddress().setStreet(dto.address().street());
        customer.getAddress().setNumber(dto.address().number());
        customer.getAddress().setCity(dto.address().city());
        customer.getAddress().setState(dto.address().state());
        customer.getAddress().setZipCode(dto.address().zipCode());
        customer.setLastModifiedAt(LocalDateTime.now());

        return CustomerMapper.toResponse(repository.save(customer));
    }

    @Override
    public void updatePassword(Long id, UpdatePasswordRequest dto) {
        BaseUser authenticated = (BaseUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authenticated.getId().equals(id)) {
            throw new UnauthorizedOperationException("You can only modify your own data");
        }

        var customer = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Customer not found with id: " + id));

        if (!passwordEncoder.matches(dto.currentPassword(), customer.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        customer.setPassword(passwordEncoder.encode(dto.newPassword()));
        customer.setLastModifiedAt(LocalDateTime.now());
        repository.save(customer);
    }
}
