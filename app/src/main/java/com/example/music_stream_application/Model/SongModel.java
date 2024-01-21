package com.example.music_stream_application.Model;

public class SongModel {
    private String title,singerName,imageUrl,songUrl,id;

    public SongModel(String title, String singerName, String imageUrl, String songUrl) {
        this.title = title;
        this.singerName = singerName;
        this.imageUrl = imageUrl;
        this.songUrl = songUrl;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
