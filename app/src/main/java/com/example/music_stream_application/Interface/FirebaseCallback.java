package com.example.music_stream_application.Interface;

import com.example.music_stream_application.Model.SongModel;

import java.util.List;

public interface FirebaseCallback {
    void onSuccess(List<SongModel> songList);
    void onFailure(String error);
}
