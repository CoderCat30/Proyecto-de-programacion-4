package com.tienda.app.service;

import com.tienda.app.model.UserAddress;
import com.tienda.app.repository.UserAddressRepository;
import org.springframework.stereotype.Service;

@Service
public class UserAddressService
{
    private final UserAddressRepository userAddressRepository;
    public UserAddressService(UserAddressRepository userAddressRepository) {
        this.userAddressRepository = userAddressRepository;
    }

    public UserAddress getUserAddressById(Integer id){
        return userAddressRepository.findById(id).orElse(null);
    }

    public UserAddress updateUserAddress(Integer id, UserAddress userAddress){
        return userAddressRepository.save(userAddress);
    }
}
