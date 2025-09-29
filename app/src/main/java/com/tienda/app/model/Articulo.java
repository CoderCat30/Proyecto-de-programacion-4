package com.tienda.app.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

/**
 * Entidad JPA que representa un artículo (producto) de la tienda.
 *
 * Se mapea a la tabla "products" en la base de datos e incluye:
 * - Información básica: nombre, descripción, imagen, estado.
 * - Relación con la categoría.
 * - Control de stock y precio.
 */
@Entity
@Table(name = "products")
public class Articulo {

    /** Identificador único del producto (clave primaria, autoincremental). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Nombre del producto (obligatorio, hasta 200 caracteres). */
    @Column(nullable = false, length = 200)
    private String name;

    /** Descripción del producto (campo largo tipo TEXT en la base de datos). */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** URL de la imagen asociada al producto. */
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    /** Indica si el producto está activo o no (true por defecto). */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Relación muchos-a-uno con la categoría del producto.
     * - fetch = LAZY → se carga solo si se accede explícitamente.
     * - OnDelete(CASCADE) → si se elimina la categoría, se eliminan también sus productos.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /** Cantidad en stock disponible (valor por defecto = 0 en la base de datos). */
    @ColumnDefault("0")
    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    /**
     * Precio del producto.
     * - precision = 10, scale = 2 → permite hasta 10 dígitos, con 2 decimales.
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // ==========================
    // Constructores
    // ==========================

    public Articulo() {}

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

    // ==========================
    // Getters & Setters
    // ==========================

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
