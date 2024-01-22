package com.example.music_stream_application.Model;

public class SongListModel {
    private String id,imageUrl,songUrl, singerName,title;

    public SongListModel() {
    }

    public SongListModel(String id, String imageUrl, String songUrl, String singerName, String title) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.songUrl = songUrl;
        this.singerName = singerName;
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

    public String getSingerName() {
        return singerName;
    }

    public String getTitle() {
        return title;
    }
}
