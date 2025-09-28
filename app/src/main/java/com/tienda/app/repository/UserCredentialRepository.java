package com.tienda.app.repository;

import com.tienda.app.model.UserCredentialModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad UserCredentialModel (credenciales de usuario).
 *
 * - Extiende JpaRepository<UserCredentialModel, Integer>, lo que le da acceso
 *   a todas las operaciones CRUD sin necesidad de implementarlas manualmente.
 *
 * - Define consultas personalizadas para autenticación y validación de usuarios.
 */
public interface UserCredentialRepository extends JpaRepository<UserCredentialModel, Integer> {

    /**
     * Busca un usuario por email y contraseña (hash).
     * - Se usa en el login para validar credenciales.
     * - Retorna un Optional porque puede que no exista coincidencia.
     *
     * Ejemplo:
     *   userCredentialRepository.findByEmailAndPasswordHash("user@test.com", "hash123");
     */
    Optional<UserCredentialModel> findByEmailAndPasswordHash(String email, String passwordHash);

    /**
     * Busca un usuario por email.
     * - Se usa para verificar si ya existe un correo registrado en el sistema.
     * - Permite evitar duplicados al registrar usuarios.
     *
     * Ejemplo:
     *   userCredentialRepository.findByEmail("user@test.com");
     */
    Optional<UserCredentialModel> findByEmail(String email);

    /**
     * Variante de búsqueda por email y password con el sufijo "Equals".
     * - Es equivalente a findByEmailAndPasswordHash, ya que Spring Data
     *   interpreta ambos de la misma manera.
     * - Podría considerarse redundante, pero es válido.
     */
    Optional<UserCredentialModel> findByEmailAndPasswordHashEquals(String email, String passwordHash);
}
