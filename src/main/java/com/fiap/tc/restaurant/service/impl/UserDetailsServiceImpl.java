package com.fiap.tc.restaurant.service.impl;

import com.fiap.tc.restaurant.repository.CustomerRepository;
import com.fiap.tc.restaurant.repository.RestaurantOwnerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final RestaurantOwnerRepository restaurantOwnerRepository;

    public UserDetailsServiceImpl(CustomerRepository customerRepository,
                                  RestaurantOwnerRepository restaurantOwnerRepository) {
        this.customerRepository = customerRepository;
        this.restaurantOwnerRepository = restaurantOwnerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return customerRepository.findByLogin(login)
                .map(u -> (UserDetails) u)
                .or(() -> restaurantOwnerRepository.findByLogin(login).map(u -> (UserDetails) u))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
    }
}
