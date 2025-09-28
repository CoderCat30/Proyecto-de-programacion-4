package com.tienda.app.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

/**
 * Entidad JPA que representa un artículo/producto en la base de datos.
 * Está mapeada a la tabla "products".
 */
@Entity
@Table(name = "products")
public class Articulo {

    /**
     * ID único del producto.
     * Se genera automáticamente con autoincremento en la BD.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Nombre del producto.
     * No puede ser nulo y tiene una longitud máxima de 200 caracteres.
     */
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Descripción larga del producto.
     * Se guarda como TEXT en la BD (ideal para descripciones extensas).
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * URL de la imagen del producto.
     * Campo opcional con longitud máxima de 255 caracteres.
     */
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    /**
     * Estado del producto (activo/inactivo).
     * Se almacena como boolean en BD pero aquí se usa Boolean (permite null).
     * Por defecto es true (activo).
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Relación muchos-a-uno con la categoría.
     * - Cada artículo pertenece a una categoría obligatoriamente.
     * - @OnDelete(CASCADE): Si se elimina la categoría, también se eliminan sus artículos.
     * - FetchType.LAZY: la categoría solo se carga cuando se necesita.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * Cantidad en stock del producto.
     * Por defecto es 0 en la BD.
     */
    @ColumnDefault("0")
    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    /**
     * Precio del producto.
     * precision = 10 → cantidad total de dígitos (ej: 12345678.90).
     * scale = 2 → cantidad de decimales.
     * No puede ser nulo.
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // ====== Constructores ======

    /**
     * Constructor con parámetros para crear un artículo de forma directa.
     */
    public Articulo(Integer id, String name, String description, BigDecimal price,
                    String imageUrl, Boolean isActive, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.stockQuantity = stockQuantity;
    }

    /**
     * Constructor vacío requerido por JPA.
     */
    public Articulo() {}

    // ====== Getters y Setters ======

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
