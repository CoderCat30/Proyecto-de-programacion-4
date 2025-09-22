package com.tienda.app.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "billing_method")
public class BillingMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private UserCredentialModel user;

    @Column(name = "card_number", nullable = false, length = 12)
    private String cardNumber;


    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    @Column(name = "exp_month", nullable = false)
    private Byte expMonth;

    @Column(name = "exp_year", nullable = false)
    private Short expYear;

    @Column(name = "name_on_card", length = 100)
    private String nameOnCard;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserCredentialModel getUser() {
        return user;
    }

    public void setUser(UserCredentialModel user) {
        this.user = user;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Byte getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(Byte expMonth) {
        this.expMonth = expMonth;
    }

    public Short getExpYear() {
        return expYear;
    }

    public void setExpYear(Short expYear) {
        this.expYear = expYear;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }



    public void clearFields() {
        this.expMonth = null;
        this.expYear = null;
        if (cardNumber == null || cardNumber.length() < 6) {
            return; // si es inválido, se queda como está
        }
        String first2 = cardNumber.substring(0, 2);
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        String middle = "*".repeat(cardNumber.length() - 6);
        this.cardNumber =  first2 + middle + last4;
    }

}