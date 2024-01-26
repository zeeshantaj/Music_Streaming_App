package com.example.music_stream_application.MethodUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.appcompat.app.AppCompatActivity;

import com.example.music_stream_application.Activities.Player_Activity;
import com.example.music_stream_application.Model.SongModel;

import java.io.File;
import java.util.Locale;

public class MethodsUtil {

    public static String millisecondsToTime(long milliseconds) {
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        String time;
        if (hours > 0) {
            time = String.format(Locale.US,"%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            time = String.format(Locale.US,"%02d:%02d", minutes, seconds);
        }
        return time;
    }

    public static String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        } else if (uri.getScheme().equals("file")) {
            result = new File(uri.getPath()).getName();
        }
        return result;
    }

    public static void setIntent(Context currentContext, Class<?> desiredActivity, SongModel model){

        Intent intent = new Intent(currentContext,desiredActivity);
        intent.putExtra("songName",model.getTitle());
        intent.putExtra("singerName",model.getSingerName());
        intent.putExtra("songImage",model.getImageUrl());
        intent.putExtra("songUrl",model.getSongUrl());
        intent.putExtra("songId",model.getId());
        intent.putExtra("categoryName",model.getCategoryName());
        currentContext.startActivity(intent);
    }
}
