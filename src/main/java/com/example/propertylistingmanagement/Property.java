package com.example.propertylistingmanagement;

public class Property {
    private int id;
    private String type;
    private int squareFeet;
    private double price;
    private String address;
    private int sellerId;

    public Property(int id, String type, int squareFeet, double price, String address) {
        this.id = id;
        this.type = type;
        this.squareFeet = squareFeet;
        this.price = price;
        this.address = address;
    }

    public Property(int id, String type, int squareFeet, double price, String address, int sellerId) {
        this.id = id;
        this.type = type;
        this.squareFeet = squareFeet;
        this.price = price;
        this.address = address;
        this.sellerId = sellerId;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public String getType() { return type; }
    public int getSquareFeet() { return squareFeet; }
    public double getPrice() { return price; }
    public String getAddress() { return address; }
    public int getSellerId() { return sellerId; }
}