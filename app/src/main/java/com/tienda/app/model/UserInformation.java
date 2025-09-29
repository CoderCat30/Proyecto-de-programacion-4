package com.tienda.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa la información personal de un usuario.
 *
 * Tabla: user_information
 *
 * Se relaciona directamente con UserCredentialModel mediante el campo `user_id`.
 * Contiene datos adicionales que complementan las credenciales básicas:
 * - Cédula o identificación personal.
 * - Nombre completo del usuario.
 */
@Entity
@Table(name = "user_information")
public class UserInformation {

    /**
     * Identificador único del usuario.
     * Corresponde al mismo id de la tabla user_credentials (1:1).
     */
    @Id
    @Column(name = "user_id", nullable = false)
    private Integer id;

    /** Documento de identificación (ej. cédula/DNI/pasaporte). */
    @Column(name = "cedula", nullable = false, length = 20)
    private String cedula;

    /** Nombre completo del usuario. */
    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    // ==========================
    // Getters & Setters
    // ==========================

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getCedula() { return cedula; }

    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getFullName() { return fullName; }

    public void setFullName(String fullName) { this.fullName = fullName; }
}
