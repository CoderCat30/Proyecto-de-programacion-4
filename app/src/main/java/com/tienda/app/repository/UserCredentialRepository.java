package com.tienda.app.repository;

import com.tienda.app.model.UserCredentialModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredentialModel, Integer> {
    //Para login
    Optional<UserCredentialModel> findByEmailAndPasswordHash(String email, String passwordHash);
    //Para evitar repeticion del email en el sistema
    Optional<UserCredentialModel> findByEmail(String email);

    Optional<UserCredentialModel> findByEmailAndPasswordHashEquals(String email, String passwordHash);
}
