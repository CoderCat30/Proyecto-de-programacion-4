package com.tienda.app.model;

import jakarta.persistence.*;

/**
 * Entidad JPA que representa una categoría de productos.
 *
 * Tabla: categories
 *
 * Cada categoría puede estar asociada a múltiples productos (relación uno-a-muchos desde Articulo).
 */
@Entity
@Table(name = "categories")
public class Category {

    /** Identificador único de la categoría (clave primaria, autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // ==========================
    // Getters & Setters
    // ==========================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
