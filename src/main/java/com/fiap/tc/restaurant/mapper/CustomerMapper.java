package com.fiap.tc.restaurant.mapper;

import com.fiap.tc.restaurant.domain.Address;
import com.fiap.tc.restaurant.domain.Customer;
import com.fiap.tc.restaurant.dto.request.CreateCustomerRequest;
import com.fiap.tc.restaurant.dto.response.AddressResponse;
import com.fiap.tc.restaurant.dto.response.UserResponse;

public class CustomerMapper {

    private CustomerMapper() {}

    public static Customer toEntity(CreateCustomerRequest request) {
        var customer = new Customer();
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setLogin(request.login());

        var address = new Address();
        address.setStreet(request.address().street());
        address.setNumber(request.address().number());
        address.setCity(request.address().city());
        address.setState(request.address().state());
        address.setZipCode(request.address().zipCode());
        customer.setAddress(address);

        return customer;
    }

    public static UserResponse toResponse(Customer customer) {
        var addr = customer.getAddress();
        var addressResponse = new AddressResponse(
                addr.getStreet(),
                addr.getNumber(),
                addr.getCity(),
                addr.getState(),
                addr.getZipCode()
        );
        return new UserResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getLogin(),
                customer.getRole(),
                customer.getLastModifiedAt(),
                addressResponse
        );
    }
}
