package com.fiap.tc.restaurant.mapper;

import com.fiap.tc.restaurant.domain.Address;
import com.fiap.tc.restaurant.domain.RestaurantOwner;
import com.fiap.tc.restaurant.dto.request.RestaurantOwnerRequest;
import com.fiap.tc.restaurant.dto.response.AddressResponse;
import com.fiap.tc.restaurant.dto.response.UserResponse;

public class RestaurantOwnerMapper {

    private RestaurantOwnerMapper() {}

    public static RestaurantOwner toEntity(RestaurantOwnerRequest request) {
        var owner = new RestaurantOwner();
        owner.setName(request.name());
        owner.setEmail(request.email());
        owner.setLogin(request.login());

        var address = new Address();
        address.setStreet(request.address().street());
        address.setNumber(request.address().number());
        address.setCity(request.address().city());
        address.setState(request.address().state());
        address.setZipCode(request.address().zipCode());
        owner.setAddress(address);

        return owner;
    }

    public static UserResponse toResponse(RestaurantOwner owner) {
        var addr = owner.getAddress();
        var addressResponse = new AddressResponse(
                addr.getStreet(),
                addr.getNumber(),
                addr.getCity(),
                addr.getState(),
                addr.getZipCode()
        );
        return new UserResponse(
                owner.getId(),
                owner.getName(),
                owner.getEmail(),
                owner.getLogin(),
                owner.getRole(),
                owner.getLastModifiedAt(),
                addressResponse
        );
    }
}
