package com.tienda.app.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Entidad JPA que representa un método de pago de un usuario.
 *
 * Tabla: billing_method
 * Cada registro corresponde a una tarjeta de crédito/débito asociada a un usuario.
 */
@Entity
@Table(name = "billing_method")
public class BillingMethod {

    /** Identificador único del método de pago (clave primaria, autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Relación muchos-a-uno con el usuario propietario de la tarjeta.
     * - fetch = LAZY → se carga solo si se accede explícitamente.
     * - OnDelete(CASCADE) → si se elimina el usuario, también se eliminan sus métodos de pago.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private UserCredentialModel user;

    /** Número de tarjeta (máx. 12 caracteres, requerido). */
    @Column(name = "card_number", nullable = false, length = 12)
    private String cardNumber;

    /** Marca de la tarjeta (Visa, Mastercard, Amex, etc.). */
    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    /** Mes de expiración de la tarjeta (1-12). */
    @Column(name = "exp_month", nullable = false)
    private Integer expMonth;

    /** Año de expiración de la tarjeta (ejemplo: 2030). */
    @Column(name = "exp_year", nullable = false)
    private Integer expYear;

    /** Nombre impreso en la tarjeta. */
    @Column(name = "name_on_card", length = 100)
    private String nameOnCard;

    // ==========================
    // Getters & Setters
    // ==========================

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public UserCredentialModel getUser() { return user; }

    public void setUser(UserCredentialModel user) { this.user = user; }

    public String getCardNumber() { return cardNumber; }

    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getBrand() { return brand; }

    public void setBrand(String brand) { this.brand = brand; }

    public Integer getExpMonth() { return expMonth; }

    public void setExpMonth(Integer expMonth) { this.expMonth = expMonth; }

    public Integer getExpYear() { return expYear; }

    public void setExpYear(Integer expYear) { this.expYear = expYear; }

    public String getNameOnCard() { return nameOnCard; }

    public void setNameOnCard(String nameOnCard) { this.nameOnCard = nameOnCard; }

    // ==========================
    // Métodos auxiliares
    // ==========================

    /**
     * Limpia o enmascara ciertos campos sensibles de la tarjeta.
     * - expMonth y expYear se ponen en null.
     * - El número de tarjeta se enmascara, dejando solo los 2 primeros y 4 últimos dígitos visibles.
     *
     * Ejemplo:
     *   411111111111 → 41******1111
     */
    public void clearFields() {
        this.expMonth = null;
        this.expYear = null;

        if (cardNumber == null || cardNumber.length() < 6) {
            return; // Si es inválido, no se modifica
        }

        String first2 = cardNumber.substring(0, 2);
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        String middle = "*".repeat(cardNumber.length() - 6);

        this.cardNumber = first2 + middle + last4;
    }
}
