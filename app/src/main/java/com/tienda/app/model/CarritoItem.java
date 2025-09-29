package com.tienda.app.model;

import java.math.BigDecimal;

public class CarritoItem {

    private Integer productoId;
    private String nombre;
    private BigDecimal precio;
    private int cantidad;

    // Constructor
    public CarritoItem(Integer productoId, String nombre, BigDecimal precio, int cantidad) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    // Método para calcular subtotal
    public BigDecimal getSubtotal() {
        return precio.multiply(new BigDecimal(cantidad));
    }

    // Getters y Setters
    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    // toString (opcional, útil para debug)
    @Override
    public String toString() {
        return "CarritoItem{" +
                "productoId=" + productoId +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", cantidad=" + cantidad +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}
