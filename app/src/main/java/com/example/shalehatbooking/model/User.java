package com.example.shalehatbooking.model;

import com.google.firebase.firestore.DocumentId;

public class User {
    @DocumentId
    private String id;
    private String username,email,phone,image;

    public User() {
    }

    public User(String id, String username, String email, String phone, String image) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.image = image;
    }

    public User(String username, String email, String phone, String imgUri) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.image = imgUri;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
