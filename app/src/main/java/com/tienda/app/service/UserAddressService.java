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

    public UserAddress create(UserAddress userAddress){
        return userAddressRepository.save(userAddress);
    }

    public UserAddress getUserAddressByUserId(Integer id){
        return userAddressRepository.findByUser_Id(id).orElse(null);
    }

    public UserAddress updateUserAddress(UserAddress userAddress){
        return userAddressRepository.save(userAddress);
    }

    public void eliminarUserAddressByUser(Integer id){
        userAddressRepository.deleteByUser_Id(id);
    }
}
