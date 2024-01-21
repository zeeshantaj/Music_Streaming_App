package com.example.music_stream_application.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.music_stream_application.Model.SongModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUtils {
    public static void saveSongDataIntoFirebase(Context context,String imageUrl,String title,String singer,String category,String songUrl){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        SongModel data = new SongModel(title,singer,imageUrl,songUrl);

        String documentPath = "category/English/"+category+"/"+title;

        // Upload the image URL to Firestore
        db.document(documentPath)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Image URL uploaded successfully
                        Toast.makeText(context, "Song Uploaded successfully", Toast.LENGTH_SHORT).show();
                        Log.d("Firestore", "Image URL uploaded successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure
                        Toast.makeText(context, "Song Uploaded successfully", Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error uploading image URL", e);
                    }
                });
    }
}
