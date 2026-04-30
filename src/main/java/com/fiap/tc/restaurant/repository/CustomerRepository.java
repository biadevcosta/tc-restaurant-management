package com.fiap.tc.restaurant.repository;

import com.fiap.tc.restaurant.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByLogin(String login);

    List<Customer> findByNameContainingIgnoreCase(String name);
}
