package com.inventory.model;

import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String category;
    private double costPrice;
    private double sellPrice;
    private int quantity;
    private int minQuantity;

    public Product() {
    }

    public Product(int id, String name, String category, double costPrice, double sellPrice, int quantity, int minQuantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.costPrice = costPrice;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getCostPrice() { return costPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }
    public double getSellPrice() { return sellPrice; }
    public void setSellPrice(double sellPrice) { this.sellPrice = sellPrice; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getMinQuantity() { return minQuantity; }
    public void setMinQuantity(int minQuantity) { this.minQuantity = minQuantity; }

    public boolean isLowStock() {
        return quantity <= minQuantity;
    }
    @Override
public String toString() {
    return name;
}
}
