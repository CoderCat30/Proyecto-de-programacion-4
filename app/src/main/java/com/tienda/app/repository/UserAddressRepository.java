package com.tienda.app.repository;

import com.tienda.app.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress,Integer> {
}
