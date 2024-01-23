package com.example.music_stream_application.Model;

public class SongModel {
    private String title,singerName,imageUrl,songUrl;
    private int viewCount;
    private long id;


    public SongModel() {
    }

    public SongModel(String title, String singerName, String imageUrl, String songUrl, long id, int viewCount) {
        this.title = title;
        this.singerName = singerName;
        this.imageUrl = imageUrl;
        this.songUrl = songUrl;
        this.id = id;
        this.viewCount = viewCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
