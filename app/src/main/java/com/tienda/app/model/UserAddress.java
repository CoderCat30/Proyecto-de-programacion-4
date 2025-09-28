package com.tienda.app.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Entidad JPA que representa una dirección de usuario.
 *
 * Se almacena en la tabla "user_address" y está asociada
 * a un usuario (UserCredentialModel).
 */
@Entity
@Table(name = "user_address")
public class UserAddress {

    /**
     * Identificador único de la dirección.
     * Se genera automáticamente con autoincremento (IDENTITY).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Relación muchos-a-uno con el usuario dueño de la dirección.
     * - Cada usuario puede tener varias direcciones registradas.
     * - FetchType.LAZY: la información del usuario se carga solo cuando se accede.
     * - OnDelete(CASCADE): si el usuario se elimina, también se borran sus direcciones.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private UserCredentialModel user;

    /**
     * Etiqueta para identificar la dirección.
     * Ejemplo: "Casa", "Oficina", "Otro".
     * - Tiene valor por defecto 'other' en la BD.
     */
    @ColumnDefault("'other'")
    @Column(name = "label", length = 255, nullable = false)
    private String label;

    /**
     * Línea principal de la dirección (calle, número, etc.).
     * Campo obligatorio.
     */
    @Column(name = "line1", nullable = false, length = 120)
    private String line1;

    /**
     * Ciudad de la dirección.
     * Campo obligatorio.
     */
    @Column(name = "city", nullable = false, length = 80)
    private String city;

    /**
     * Estado o provincia de la dirección.
     * Campo opcional.
     */
    @Column(name = "state", length = 80)
    private String state;

    /**
     * Código postal de la dirección.
     * Campo opcional.
     */
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    /**
     * Número de teléfono asociado a la dirección.
     * Campo opcional.
     */
    @Column(name = "phone", length = 25)
    private String phone;

    // ====== Getters y Setters ======

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
