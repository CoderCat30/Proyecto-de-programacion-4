package com.tienda.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(nullable = false)
    private double price;

    @Column(name = "sale_price")
    private Double salePrice; // Puede ser null

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // Usamos Boolean, no boolean

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    // ⚡️ Constructores
    public Articulo() {}

    public Articulo(String name, String description, String sku, double price, String imageUrl) {
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isActive = true;
        this.isFeatured = false;
    }

    // ⚡️ Getters & Setters
    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getSku() { return sku; }

    public void setSku(String sku) { this.sku = sku; }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }

    public Double getSalePrice() { return salePrice; }

    public void setSalePrice(Double salePrice) { this.salePrice = salePrice; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getIsActive() { return isActive; }

    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsFeatured() { return isFeatured; }

    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }
}
