package com.tienda.app.model;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Entidad JPA que representa las credenciales de un usuario.
 *
 * Tabla: user_credentials
 *
 * Contiene:
 * - Identificador único.
 * - Email (login).
 * - Contraseña (almacenada como hash).
 * - Rol del usuario (por defecto "user").
 *
 * Esta entidad se usa para autenticación y control de roles en el sistema.
 */
@Entity
@Table(name = "user_credentials")
public class UserCredentialModel {

    /** Identificador único del usuario (clave primaria, autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /** Email del usuario (también funciona como nombre de usuario). */
    @Column(name = "email", nullable = false, length = 254)
    private String email;

    /** Contraseña del usuario (almacenada como hash, nunca en texto plano). */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /** Rol del usuario (ej. "user", "admin"). Por defecto = "user". */
    @Column(name = "role", nullable = false, length = 10)
    private String role = "user";

    // ==========================
    // Getters & Setters
    // ==========================

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }

    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    // ==========================
    // Métodos de utilidad
    // ==========================

    @Override
    public String toString() {
        return "UserCredentialModel{" +
                "email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserCredentialModel that)) return false;
        return Objects.equals(id, that.id)
                && Objects.equals(email, that.email)
                && Objects.equals(passwordHash, that.passwordHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, passwordHash);
    }
}
