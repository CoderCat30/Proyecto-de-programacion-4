package com.tienda.app.model;

import jakarta.persistence.*;

/**
 * Entidad JPA que representa una categoría de productos.
 *
 * Está mapeada a la tabla "categories" en la base de datos.
 *
 * Su objetivo principal es servir como agrupador de artículos
 * (cada producto pertenece a una categoría).
 */
@Entity
@Table(name = "categories")
public class Category {

    /**
     * Identificador único de la categoría.
     * - Se genera automáticamente con autoincremento (IDENTITY).
     * - Se almacena en la columna "id".
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // ====== Getters y Setters ======

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
