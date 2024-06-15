package com.example.test.models;

public class Banner {
    public Banner(int banner) {
        this.imageUrl = banner;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    private int imageUrl;


}
