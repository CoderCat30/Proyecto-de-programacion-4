
package com.tienda.app.model;

import java.util.List;

public class Checkout {
    //informacion personal
    private String fullName;
    private String cedula;

    // Dirección
    private String label;
    private String line1;
    private String city;
    private String state;
    private String postalCode;
    private String phone;

    //metodo de pago (numero de tarjeta e id)
    private Integer metodoPago; //cardNumber seleccionado
    private String cardnumber;

    //Lista de productos para mostrar al final y crear orden
    List<CarritoItem> carrito;


    // Getters y setters
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(Integer metodoPago) {
        this.metodoPago = metodoPago;
    }

    public void setCarrito(List<CarritoItem> carrito) {
        this.carrito = carrito;
    }

    public List<CarritoItem> getCarrito() {
        return carrito;
    }

    public String getCardnumber() {
        return cardnumber;
    }

    public void setCardnumber(String cardnumber) {
        this.cardnumber = cardnumber;
    }

}
