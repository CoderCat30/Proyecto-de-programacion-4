package com.tienda.app.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Entidad JPA que almacena la información personal adicional de un usuario.
 *
 * Está mapeada a la tabla "user_information".
 * Se relaciona 1 a 1 con la tabla de credenciales (UserCredentialModel).
 */
@Entity
@Table(name = "user_information")
public class UserInformation {

    /**
     * ID del usuario.
     * - Es también la clave primaria de esta tabla.
     * - Coincide con el ID de la tabla "user_credentials".
     */
    @Id
    @Column(name = "user_id", nullable = false)
    private Integer id;

    /**
     * Número de identificación personal (ej. cédula/DNI/pasaporte).
     * - Campo obligatorio.
     */
    @Column(name = "cedula", nullable = false, length = 20)
    private String cedula;

    /**
     * Relación uno-a-uno con la tabla "user_credentials".
     * - @MapsId: significa que comparte la misma clave primaria (user_id).
     * - FetchType.LAZY: solo carga los datos de credenciales cuando se necesiten.
     * - OnDelete(CASCADE): si se elimina el usuario, también se elimina su información personal.
     */
    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private UserCredentialModel userCredentials;

    /**
     * Nombre completo del usuario.
     * - Campo obligatorio.
     */
    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    // ====== Getters y Setters ======

    public UserCredentialModel getUserCredentials() {
        return userCredentials;
    }

    public void setUserCredentials(UserCredentialModel userCredentials) {
        this.userCredentials = userCredentials;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
}
