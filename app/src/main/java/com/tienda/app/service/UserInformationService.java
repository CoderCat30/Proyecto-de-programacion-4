package com.tienda.app.service;

import com.tienda.app.model.UserInformation;
import com.tienda.app.repository.UserInformationRepository;
import org.springframework.stereotype.Service;

@Service
public class UserInformationService {
    private final UserInformationRepository userInformationRepository;
    public UserInformationService(UserInformationRepository userInformationRepository) {
        this.userInformationRepository = userInformationRepository;
    }

    public UserInformation create(UserInformation userInformation) {
        return userInformationRepository.save(userInformation);
    }

    public UserInformation getUserInformationById(Integer id){
        return userInformationRepository.findById(id).orElse(null);
    }

    public void eliminarUserInformation(Integer id){
        userInformationRepository.deleteById(id);
    }
}
