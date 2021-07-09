package com.example.shalehatbooking.model;

import com.google.firebase.firestore.DocumentId;

public class Shalehats {
    @DocumentId
    private String id;
    private String name , location , description , image,address ;
    private double price , rating ;
//
    public static final String FIELD_CITY = "location";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_POPULARITY = "rating";
    public static final String FIELD_AVG_RATING = "rating";

    public Shalehats() {

    }

    public Shalehats(String id, String name, String location, String description, String image, double price, double rating,String address) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.image = image;
        this.price = price;
        this.rating = rating;
        this.address = address;
    }

    public Shalehats(String name, String location, String description, String image, double price, double rating,String address) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.image = image;
        this.price = price;
        this.rating = rating;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
