package com.tienda.app.service;

import com.tienda.app.model.UserCredentialModel;
import com.tienda.app.repository.UserCredentialRepository;
import org.springframework.stereotype.Service;

@Service
public class UserCredentialService {

    private final UserCredentialRepository userCredentialRepository;
    public UserCredentialService(UserCredentialRepository userCredentialRepository) {
        this.userCredentialRepository = userCredentialRepository;
    }



    public UserCredentialModel registrarUser(String email, String passwordHash) {
        if (email == null || email.isEmpty() || passwordHash == null || passwordHash.isEmpty()) return null;
        UserCredentialModel userCredential = new UserCredentialModel();
        userCredential.setEmail(email);
        userCredential.setPasswordHash(passwordHash);
        return userCredentialRepository.save(userCredential);
    }

    //evitar emais repetidos en el sistema
    public UserCredentialModel buscarPorEmail(String email) {
        return userCredentialRepository.findByEmail(email).orElse(null);
    }



}
