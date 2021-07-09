package com.example.shalehatbooking.model;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

public class Slide implements Serializable {
    @DocumentId
    String id ;
    private String imageUrl, title;

    public Slide() {
    }

    public Slide(String imageUrl, String title) {
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
