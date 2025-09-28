package com.tienda.app.model;

/**
 * Clase de apoyo (DTO / POJO) que representa la información
 * que un usuario ingresa al momento de finalizar la compra.
 *
 * No es una entidad JPA, por lo tanto no se guarda directamente
 * en la base de datos, sino que se usa como objeto temporal
 * para capturar los datos del formulario de checkout.
 */
public class Checkout {

    /**
     * Nombre del comprador.
     * Normalmente se usa para identificar quién recibe el pedido.
     */
    private String nombre;

    /**
     * Dirección de envío del pedido.
     */
    private String direccion;

    /**
     * Método de pago seleccionado por el usuario.
     * Ejemplo: tarjeta, PayPal, transferencia, etc.
     */
    private String metodoPago;

    // ====== Getters y Setters ======

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
}
