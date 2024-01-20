package com.example.music_stream_application.Model;

public class SongListModel {
    private String id,imageUrl,songUrl,subtitle,title;

    public SongListModel() {
    }

    public SongListModel(String id, String imageUrl, String songUrl, String subtitle, String title) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.songUrl = songUrl;
        this.subtitle = subtitle;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getTitle() {
        return title;
    }
}
