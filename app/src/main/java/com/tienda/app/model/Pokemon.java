package com.tienda.app.model;

public class Pokemon {
    private int id;
    private String name;
    private int height;
    private int weight;
    private String image;

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}
