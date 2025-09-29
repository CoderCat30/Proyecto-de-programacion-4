package com.tienda.app.model;

import java.math.BigDecimal;

/**
 * Clase auxiliar (no entidad JPA) que representa un ítem dentro del carrito de compras.
 *
 * Contiene la información mínima necesaria para mostrar y calcular subtotales
 * sin necesidad de cargar la entidad completa de la base de datos.
 */
public class CarritoItem {

    /** Identificador del producto (referencia a la entidad Articulo). */
    private Integer productoId;

    /** Nombre del producto. */
    private String nombre;

    /** Precio unitario del producto. */
    private BigDecimal precio;

    /** Cantidad del producto agregada al carrito. */
    private int cantidad;

    /**
     * Constructor principal.
     *
     * @param productoId id del producto
     * @param nombre nombre del producto
     * @param precio precio unitario
     * @param cantidad cantidad en el carrito
     */
    public CarritoItem(Integer productoId, String nombre, BigDecimal precio, int cantidad) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    /**
     * Calcula el subtotal del ítem en el carrito.
     *
     * @return precio * cantidad
     */
    public BigDecimal getSubtotal() {
        return precio.multiply(new BigDecimal(cantidad));
    }

    // ==========================
    // Getters & Setters
    // ==========================

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

    /**
     * Representación en texto del objeto (útil para debug).
     */
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
