package com.tienda.app.repository;

import com.tienda.app.model.UserCredentialModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link UserCredentialModel}.
 *
 * Extiende de {@link JpaRepository}, lo que provee automáticamente:
 * - Operaciones CRUD (save, findById, findAll, delete, etc.).
 *
 * Métodos personalizados:
 * - {@link #findByEmailAndPasswordHash(String, String)} → usado para login.
 * - {@link #findByEmail(String)} → verifica si un email ya está registrado.
 * - {@link #findByEmailAndPasswordHashEquals(String, String)} → redundante, equivale al primero.
 *
 * Tipo genérico:
 * - Entidad: UserCredentialModel
 * - Clave primaria: Integer
 */
public interface UserCredentialRepository extends JpaRepository<UserCredentialModel, Integer> {

    /**
     * Busca un usuario por email y contraseña (hash).
     * Usado en el proceso de login.
     *
     * @param email email del usuario.
     * @param passwordHash hash de la contraseña.
     * @return Optional con el usuario si existe, vacío si no.
     */
    Optional<UserCredentialModel> findByEmailAndPasswordHash(String email, String passwordHash);

    /**
     * Busca un usuario por su email.
     * Usado para validar si un correo ya está registrado antes de crear una nueva cuenta.
     *
     * @param email email a buscar.
     * @return Optional con el usuario si existe, vacío si no.
     */
    Optional<UserCredentialModel> findByEmail(String email);

    /**
     * Método redundante de {@link #findByEmailAndPasswordHash(String, String)}.
     * Hace exactamente lo mismo, pero con la sintaxis "Equals".
     * Se recomienda mantener solo uno para evitar duplicidad.
     *
     * @param email email del usuario.
     * @param passwordHash hash de la contraseña.
     * @return Optional con el usuario si existe, vacío si no.
     */
    Optional<UserCredentialModel> findByEmailAndPasswordHashEquals(String email, String passwordHash);
}
