package com.tienda.app.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

/**
 * Entidad JPA que representa una cuenta bancaria o tarjeta en el sistema.
 *
 * Tabla: bank
 * Contiene los datos básicos de una tarjeta bancaria y su saldo asociado,
 * que se utilizan para simular transacciones y validar pagos.
 */
@Entity
@Table(name = "bank")
public class Bank {

    /** Identificador único de la tarjeta/banco (clave primaria, autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /** Nombre del banco emisor de la tarjeta (ejemplo: "Banco Nacional"). */
    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    /** Número de la tarjeta (máx. 12 caracteres, requerido). */
    @Column(name = "card_number", nullable = false, length = 12)
    private String cardNumber;

    /** Marca de la tarjeta (Visa, Mastercard, Amex, etc.). */
    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    /** Mes de expiración de la tarjeta (1-12). */
    @Column(name = "exp_month", nullable = false)
    private Integer expMonth;

    /** Año de expiración de la tarjeta (ejemplo: 2028). */
    @Column(name = "exp_year", nullable = false)
    private Integer expYear;

    /** Nombre impreso en la tarjeta. */
    @Column(name = "name_on_card", length = 100)
    private String nameOnCard;

    /**
     * Saldo disponible en la cuenta asociada a la tarjeta.
     * Valor por defecto: 0.00
     * precision = 10, scale = 2 → hasta 10 dígitos con 2 decimales.
     */
    @ColumnDefault("0.00")
    @Column(name = "balance", precision = 10, scale = 2)
    private BigDecimal balance;

    // ==========================
    // Getters & Setters
    // ==========================

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getBankName() { return bankName; }

    public void setBankName(String bankName) { this.bankName = bankName; }

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

    public BigDecimal getBalance() { return balance; }

    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
