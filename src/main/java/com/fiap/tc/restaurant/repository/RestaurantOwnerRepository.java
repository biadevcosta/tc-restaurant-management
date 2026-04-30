package com.fiap.tc.restaurant.repository;

import com.fiap.tc.restaurant.domain.RestaurantOwner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantOwnerRepository extends JpaRepository<RestaurantOwner, Long> {

    boolean existsByEmail(String email);

    boolean existsByLogin(String login);

    Optional<RestaurantOwner> findByEmail(String email);

    List<RestaurantOwner> findByNameContainingIgnoreCase(String name);
}
