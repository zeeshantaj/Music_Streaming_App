package com.example.music_stream_application.Interface;

import androidx.lifecycle.MutableLiveData;

import com.example.music_stream_application.Model.CategoryModel;
import com.example.music_stream_application.Model.SongModel;

import java.util.List;

public interface FirebaseCallback {
    void onSuccess(List<SongModel> songList);
    void onFailure(String error);
}
