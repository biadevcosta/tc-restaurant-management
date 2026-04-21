package com.fiap.tc.restaurant.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "restaurant_owners")

public abstract class RestaurantOwner extends BaseUser {
}
