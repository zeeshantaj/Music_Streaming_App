package com.example.music_stream_application.Model;

public class CategoryModel {
    private String name,imageUrl;

    public CategoryModel() {
    }

    public CategoryModel(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
