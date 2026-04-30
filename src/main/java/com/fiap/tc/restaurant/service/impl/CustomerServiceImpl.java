package com.fiap.tc.restaurant.service.impl;

import com.fiap.tc.restaurant.dto.request.CreateCustomerRequest;
import com.fiap.tc.restaurant.dto.response.UserResponse;
import com.fiap.tc.restaurant.enums.UserRole;
import com.fiap.tc.restaurant.exception.EmailAlreadyExistsException;
import com.fiap.tc.restaurant.exception.LoginAlreadyExistsException;
import com.fiap.tc.restaurant.exception.UserNotFoundException;
import com.fiap.tc.restaurant.mapper.CustomerMapper;
import com.fiap.tc.restaurant.repository.CustomerRepository;
import com.fiap.tc.restaurant.service.CustomerService;
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
}
