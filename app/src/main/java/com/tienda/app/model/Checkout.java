package com.tienda.app.model;

import java.util.List;

/**
 * Clase auxiliar (no entidad JPA) que representa la información de checkout
 * cuando un usuario finaliza la compra.
 *
 * Contiene:
 * - Datos personales del comprador.
 * - Dirección de envío.
 * - Método de pago seleccionado.
 * - Lista de productos en el carrito en el momento de la compra.
 *
 * Esta clase se usa como DTO (Data Transfer Object) para transportar
 * la información entre el formulario de checkout y la lógica de confirmación.
 */
public class Checkout {

    // ==========================
    // Información personal
    // ==========================
    private String fullName;  // Nombre completo del comprador
    private String cedula;    // Identificación (ej. cédula/DNI)

    // ==========================
    // Dirección de envío
    // ==========================
    private String label;       // Etiqueta (ej. "Casa", "Oficina")
    private String line1;       // Línea principal de la dirección
    private String city;        // Ciudad
    private String state;       // Provincia/Estado
    private String postalCode;  // Código postal
    private String phone;       // Teléfono de contacto

    // ==========================
    // Método de pago
    // ==========================
    private Integer metodoPago; // ID del método de pago seleccionado
    private String cardnumber;  // Número de tarjeta enmascarado

    // ==========================
    // Carrito asociado
    // ==========================
    private List<CarritoItem> carrito; // Lista de productos del pedido

    // ==========================
    // Getters & Setters
    // ==========================

    public String getFullName() { return fullName; }

    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCedula() { return cedula; }

    public void setCedula(String cedula) { this.cedula = cedula; }

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

    public Integer getMetodoPago() { return metodoPago; }

    public void setMetodoPago(Integer metodoPago) { this.metodoPago = metodoPago; }

    public String getCardnumber() { return cardnumber; }

    public void setCardnumber(String cardnumber) { this.cardnumber = cardnumber; }

    public List<CarritoItem> getCarrito() { return carrito; }

    public void setCarrito(List<CarritoItem> carrito) { this.carrito = carrito; }
}
