package com.tienda.app.model;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Entidad JPA que representa las credenciales de un usuario.
 *
 * Se almacena en la tabla "user_credentials".
 * Contiene la información básica para autenticación y autorización.
 */
@Entity
@Table(name = "user_credentials")
public class UserCredentialModel {

    /**
     * Identificador único del usuario.
     * - Se genera automáticamente con autoincremento (IDENTITY).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Correo electrónico del usuario.
     * - No puede ser nulo.
     * - Longitud máxima: 254 (estándar para emails).
     */
    @Column(name = "email", nullable = false, length = 254)
    private String email;

    /**
     * Hash de la contraseña del usuario.
     * - Nunca se debe guardar la contraseña en texto plano.
     * - Este campo almacena la versión cifrada/hasheada.
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Rol del usuario en el sistema.
     * Ejemplo: "ADMIN", "USER".
     * - Se usa para control de permisos/autorización.
     */
    @Column(name = "role", nullable = false, length = 10)
    private String role;

    // ====== Getters y Setters ======

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    // ====== Métodos de utilidad ======

    /**
     * Representación en texto del objeto.
     * - Útil para debug y logs.
     * - No incluye passwordHash por seguridad.
     */
    @Override
    public String toString() {
        return "UserCredentialModel{" +
                "email='" + email + '\'' +
                '}';
    }

    /**
     * Comparación de igualdad entre objetos.
     * - Considera id, email y passwordHash.
     * - Sobrescribir equals() es importante en entidades JPA
     *   para evitar inconsistencias al trabajar con colecciones.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserCredentialModel that)) return false;
        return Objects.equals(id, that.id)
                && Objects.equals(email, that.email)
                && Objects.equals(passwordHash, that.passwordHash);
    }

    /**
     * Método hashCode() consistente con equals().
     * - Permite usar esta entidad en colecciones como HashSet o HashMap.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, email, passwordHash);
    }
}
