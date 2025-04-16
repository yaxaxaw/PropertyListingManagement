package com.example.propertylistingmanagement;

public class Property {
    private int id;
    private String type;
    private int squareFeet;
    private double price;
    private String address;
    private int sellerId;
    private String status;

    public Property(int id, String type, int squareFeet, double price, String address) {
        this.id = id;
        this.type = type;
        this.squareFeet = squareFeet;
        this.price = price;
        this.address = address;
    }

    public Property(int id, String type, int squareFeet, double price, String address, int sellerId, String status) {
        this.id = id;
        this.type = type;
        this.squareFeet = squareFeet;
        this.price = price;
        this.address = address;
        this.sellerId = sellerId;
        this.status = status;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public int getSquareFeet() { return squareFeet; }
    public double getPrice() { return price; }
    public String getAddress() { return address; }
    public int getSellerId() { return sellerId; }
    public String getStatus() { return status; }

    public void setId(int id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setSquareFeet(int squareFeet) { this.squareFeet = squareFeet; }
    public void setPrice(double price) { this.price = price; }
    public void setAddress(String address) { this.address = address; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }
    public void setStatus(String status) { this.status = status; }
}