package com.example.music_stream_application.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.music_stream_application.Activities.Song_List_Activity;
import com.example.music_stream_application.Adapter.SongListAdapter;
import com.example.music_stream_application.Interface.FirebaseCallback;
import com.example.music_stream_application.Model.SongModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {
    public static void updateListenCount(Context context, String categoryType, String title){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String documentPath = "category/"+categoryType+"/" + categoryType + "/" + title;
        firestore.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentReference documentReference = firestore.document(documentPath);
            // Retrieve current viewCount
            DocumentSnapshot documentSnapshot = transaction.get(documentReference);
            int currentCount = documentSnapshot.getLong("viewCount").intValue();
            // Increment viewCount by 1
            transaction.update(documentReference, "viewCount", currentCount + 1);

            return null;
        })
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "count++", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("Error "+e.getLocalizedMessage());
                });
    }
    public static void getAllSongs(FirebaseCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("category").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<SongModel> songListModelList = new ArrayList<>();

                    for (QueryDocumentSnapshot categoryDocument : queryDocumentSnapshots) {
                        String categoryName = categoryDocument.getId();

                        CollectionReference songsCollection = categoryDocument.getReference().collection(categoryName);

                        songsCollection.get().addOnSuccessListener(songQueryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot songDocument : songQueryDocumentSnapshots) {
                                String title = songDocument.get("title", String.class);
                                String singerName = songDocument.get("singerName", String.class);
                                String imageUrl = songDocument.get("imageUrl", String.class);
                                String songUrl = songDocument.get("songUrl", String.class);
                                String catName = songDocument.get("categoryName", String.class);
                                long id = songDocument.getLong("viewCount").longValue();
                                int viewCount = songDocument.getLong("viewCount").intValue();

                                SongModel songModel = new SongModel(title, singerName, imageUrl, songUrl, id, viewCount, catName);
                                songListModelList.add(songModel);
                            }

                            callback.onSuccess(songListModelList);

                        }).addOnFailureListener(e -> {
                            String errorMessage = "Error getting image " + categoryName + ": " + e.getLocalizedMessage();
                            Log.e("Firestore", errorMessage, e);
                            callback.onFailure(e.getLocalizedMessage());
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    String errorMessage = "Error getting categories: " + e.getLocalizedMessage();
                    Log.e("Firestore", errorMessage, e);
                    callback.onFailure(e.getLocalizedMessage());
                });
    }
    public static void getSongs(String catName,FirebaseCallback callback){

        FirebaseFirestore.getInstance().collection("category").document(catName).collection(catName)

                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<SongModel> songListModelList = new ArrayList<>();
                        songListModelList = queryDocumentSnapshots.toObjects(SongModel.class);
                        callback.onSuccess(songListModelList);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e.getLocalizedMessage());

                    }
                });
    }
}
