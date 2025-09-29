package com.tienda.app.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Entidad JPA que representa una dirección de usuario.
 *
 * Tabla: user_address
 * Cada usuario puede tener una o varias direcciones asociadas.
 */
@Entity
@Table(name = "user_address")
public class UserAddress {

    /** Identificador único de la dirección (clave primaria, autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Relación muchos-a-uno con el usuario propietario de la dirección.
     * - fetch = LAZY → se carga solo si se accede explícitamente.
     * - OnDelete(CASCADE) → si se elimina el usuario, también se eliminan sus direcciones.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private UserCredentialModel user;

    /**
     * Etiqueta para identificar la dirección (ej. "Casa", "Oficina").
     * Valor por defecto en la base de datos: 'other'.
     */
    @ColumnDefault("'other'")
    @Column(name = "label", length = 255, nullable = false)
    private String label;

    /** Línea principal de la dirección (ej. calle, número). */
    @Column(name = "line1", nullable = false, length = 120)
    private String line1;

    /** Ciudad de la dirección. */
    @Column(name = "city", nullable = false, length = 80)
    private String city;

    /** Estado o provincia (opcional). */
    @Column(name = "state", length = 80)
    private String state;

    /** Código postal (opcional). */
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    /** Teléfono de contacto asociado a la dirección (opcional). */
    @Column(name = "phone", length = 25)
    private String phone;

    // ==========================
    // Getters & Setters
    // ==========================

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public UserCredentialModel getUser() { return user; }

    public void setUser(UserCredentialModel user) { this.user = user; }

    public String getLabel() { return label; }

    public void setLabel(String label) { this.label = label; }

    public String getLine1() { return line1; }

    public void setLine1(String line1) { this.line1 = line1; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }

    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }

    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }
}
