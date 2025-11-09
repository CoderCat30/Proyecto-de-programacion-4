package com.tienda.app.repository;

import com.tienda.app.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress,Integer> {
    public Optional<UserAddress> findByUser_Id(Integer id);

    void deleteByUser_Id(Integer id);
}
