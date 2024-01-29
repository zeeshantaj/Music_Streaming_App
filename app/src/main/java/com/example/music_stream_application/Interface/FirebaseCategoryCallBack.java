package com.example.music_stream_application.Interface;

import com.example.music_stream_application.Model.CategoryModel;

import java.util.List;

public interface FirebaseCategoryCallBack {
    void onSuccessCategory(List<CategoryModel> categoryModelList);
    void onFailure(String error);
}
