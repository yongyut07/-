package com.inventory.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TransactionRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int productId;
    private String productName;
    private TransactionType type;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private LocalDateTime dateTime;

    public TransactionRecord() {
    }

    public TransactionRecord(int id, int productId, String productName, TransactionType type, int quantity,
                             double unitPrice, double totalPrice, LocalDateTime dateTime) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.type = type;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.dateTime = dateTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
}
