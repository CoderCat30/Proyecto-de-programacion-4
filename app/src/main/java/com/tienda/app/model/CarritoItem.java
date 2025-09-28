package com.tienda.app.model;

import java.math.BigDecimal;

/**
 * Clase que representa un ítem dentro del carrito de compras.
 *
 * No es una entidad JPA (no se guarda directamente en la base de datos),
 * sino un objeto de apoyo que se almacena en la sesión del usuario
 * para manejar los productos que agrega al carrito.
 */
public class CarritoItem {

    /**
     * Identificador único del producto.
     * Se usa para relacionar este ítem con un producto real en la base de datos.
     */
    private Integer productoId;

    /**
     * Nombre del producto.
     * Se guarda aquí para no tener que consultarlo en la BD cada vez que se muestre el carrito.
     */
    private String nombre;

    /**
     * Precio unitario del producto.
     * Se almacena como BigDecimal para mayor precisión en cálculos monetarios.
     */
    private BigDecimal precio;

    /**
     * Cantidad de unidades del producto agregadas al carrito.
     */
    private int cantidad;

    // ====== Constructor ======

    /**
     * Constructor principal que permite crear un ítem de carrito
     * con toda su información: producto, nombre, precio y cantidad.
     */
    public CarritoItem(Integer productoId, String nombre, BigDecimal precio, int cantidad) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    // ====== Métodos de lógica ======

    /**
     * Calcula el subtotal de este ítem (precio * cantidad).
     * Ejemplo: precio = 10.50, cantidad = 3 → subtotal = 31.50
     */
    public BigDecimal getSubtotal() {
        return precio.multiply(new BigDecimal(cantidad));
    }

    // ====== Getters y Setters ======

    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    // ====== Utilidad ======

    /**
     * Método toString para depuración.
     * Devuelve una representación legible del ítem,
     * incluyendo el subtotal calculado.
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
