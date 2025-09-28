package com.tienda.app.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Entidad JPA que representa un método de pago (tarjeta) de un usuario.
 * Se almacena en la tabla "billing_method".
 */
@Entity
@Table(name = "billing_method")
public class BillingMethod {

    /**
     * Identificador único del método de pago.
     * Se genera automáticamente (auto-incremental).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Relación muchos-a-uno con el usuario dueño de la tarjeta.
     * - Cada usuario puede tener varios métodos de pago.
     * - FetchType.LAZY: el usuario solo se carga cuando se accede.
     * - OnDelete(CASCADE): si se elimina el usuario, también se eliminan sus tarjetas.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private UserCredentialModel user;

    /**
     * Número de tarjeta de crédito/débito.
     * - No puede ser nulo.
     * - Máximo 12 caracteres (puede estar simulando truncamiento por seguridad).
     */
    @Column(name = "card_number", nullable = false, length = 12)
    private String cardNumber;

    /**
     * Marca de la tarjeta (Visa, MasterCard, Amex, etc.).
     * - No puede ser nulo.
     * - Longitud máxima: 50 caracteres.
     */
    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    /**
     * Mes de expiración de la tarjeta (1-12).
     */
    @Column(name = "exp_month", nullable = false)
    private Byte expMonth;

    /**
     * Año de expiración de la tarjeta (ejemplo: 2025).
     */
    @Column(name = "exp_year", nullable = false)
    private Short expYear;

    /**
     * Nombre del titular de la tarjeta (como aparece en la tarjeta).
     * - Campo opcional.
     */
    @Column(name = "name_on_card", length = 100)
    private String nameOnCard;

    // ====== Getters y Setters ======

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public UserCredentialModel getUser() { return user; }
    public void setUser(UserCredentialModel user) { this.user = user; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Byte getExpMonth() { return expMonth; }
    public void setExpMonth(Byte expMonth) { this.expMonth = expMonth; }

    public Short getExpYear() { return expYear; }
    public void setExpYear(Short expYear) { this.expYear = expYear; }

    public String getNameOnCard() { return nameOnCard; }
    public void setNameOnCard(String nameOnCard) { this.nameOnCard = nameOnCard; }

    // ====== Lógica adicional ======

    /**
     * Método de utilidad para "enmascarar" la información sensible de la tarjeta.
     * - Limpia los campos de expiración.
     * - Oculta todos los dígitos intermedios del número de tarjeta,
     *   dejando visibles solo los 2 primeros y los 4 últimos.
     *
     * Ejemplo:
     *   123456789012 → 12******9012
     */
    public void clearFields() {
        this.expMonth = null;
        this.expYear = null;

        // Si el número de tarjeta es inválido o demasiado corto, no hace nada
        if (cardNumber == null || cardNumber.length() < 6) {
            return;
        }

        // Se mantienen los 2 primeros y 4 últimos dígitos
        String first2 = cardNumber.substring(0, 2);
        String last4 = cardNumber.substring(cardNumber.length() - 4);

        // Se reemplaza el resto por "*"
        String middle = "*".repeat(cardNumber.length() - 6);

        this.cardNumber = first2 + middle + last4;
    }
}
