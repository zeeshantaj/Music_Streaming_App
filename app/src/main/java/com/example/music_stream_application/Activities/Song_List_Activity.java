package com.example.music_stream_application.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.example.music_stream_application.Adapter.Category_Adapter;
import com.example.music_stream_application.Adapter.SongListAdapter;
import com.example.music_stream_application.Interface.FirebaseCallback;
import com.example.music_stream_application.MainActivity;
import com.example.music_stream_application.Model.CategoryModel;
import com.example.music_stream_application.Model.SongListModel;
import com.example.music_stream_application.Model.SongModel;
import com.example.music_stream_application.R;
import com.example.music_stream_application.Utils.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class Song_List_Activity extends AppCompatActivity {

    private RecyclerView listRecycler;
    private List<SongModel> songListModelList;
    private SongListAdapter listAdapter;
    private MaterialSearchBar searchView;
    public LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);


        Intent intent = getIntent();
        String categoryName = intent.getStringExtra("categoryName");
        boolean isCategory = intent.getBooleanExtra("isCategory",false);

        searchView = (MaterialSearchBar) findViewById(R.id.mainSongSearch);
        searchView.setMaxSuggestionCount(4);
        searchView.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                performSearch(text.toString());

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        listRecycler = findViewById(R.id.songListRecycler);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        songListModelList = new ArrayList<>();
        listRecycler.setLayoutManager(layoutManager);

        if (isCategory){
            FirebaseHelper.getSongs(categoryName, new FirebaseCallback() {
                @Override
                public void onSuccess(List<SongModel> songList) {
                    listAdapter = new SongListAdapter(songList);
                    listRecycler.setAdapter(listAdapter);
                    songListModelList = songList;
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(Song_List_Activity.this, "Error "+error, Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            FirebaseHelper.getAllSongs( new FirebaseCallback() {
                @Override
                public void onSuccess(List<SongModel> songList) {
                    listAdapter = new SongListAdapter(songList);
                    listRecycler.setAdapter(listAdapter);
                    songListModelList = songList;
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(Song_List_Activity.this, "Error "+error, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


//    private void songListData(String path){
//        FirebaseFirestore.getInstance().collection("category").document(path).collection(path)
//                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                        songListModelList = queryDocumentSnapshots.toObjects(SongModel.class);
//                        listAdapter = new SongListAdapter(songListModelList);
//                        listRecycler.setAdapter(listAdapter);
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(Song_List_Activity.this, "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    //todo all songs


//    private void getAllSongImage(){
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        firestore.collection("category").get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    for (QueryDocumentSnapshot categoryDocument : queryDocumentSnapshots) {
//                        String categoryName = categoryDocument.getId();
//
//                        // Assuming each category has a subcollection with the same name as the category
//                        CollectionReference songsCollection = categoryDocument.getReference().collection(categoryName);
//
//                        // Query songs within the subcollection
//                        songsCollection.get().addOnSuccessListener(songQueryDocumentSnapshots -> {
//                            for (QueryDocumentSnapshot songDocument : songQueryDocumentSnapshots) {
//
//                                //String title, String singerName, String imageUrl, String songUrl, long id, int viewCount
//                                String title = songDocument.get("title",String.class);
//                                String singerName = songDocument.get("singerName",String.class);
//                                String imageUrl = songDocument.get("imageUrl",String.class);
//                                String songUrl = songDocument.get("songUrl",String.class);
//                                String catName = songDocument.get("categoryName",String.class);
//                                long id = songDocument.getLong("viewCount").longValue();
//                                int viewCount = songDocument.getLong("viewCount").intValue();
//
//                                SongModel songModel = new SongModel(title,singerName,imageUrl,songUrl,id,viewCount,catName);
//                                songListModelList.add(songModel);
//                                //songListModelList = songQueryDocumentSnapshots.toObjects(SongModel.class);
//                                listAdapter = new SongListAdapter(songListModelList);
//                                listRecycler.setAdapter(listAdapter);
//                            }
//
//                        }).addOnFailureListener(e -> {
//                            Toast.makeText(this, "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                            Log.e("Firestore", "Error getting image " + categoryName, e);
//                        });
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                    Log.e("Firestore", "Error getting categories", e);
//                });
//    }

    public  void performSearch(String query) {
        int positionToScroll = -1;
        for (int i = 0; i < songListModelList.size(); i++) {
            if (songListModelList.get(i).getTitle().toLowerCase().trim().equals(query.toLowerCase())) {
                positionToScroll = i;
                break; // Stop searching after finding the first match
            }
        }
        if (positionToScroll != -1) {
            LinearSmoothScroller smoothScroller = new LinearSmoothScroller(listRecycler.getContext()) {
                @Override
                protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_START;
                }
            };


            smoothScroller.setTargetPosition(positionToScroll);
            layoutManager.startSmoothScroll(smoothScroller);
        }
        else {
            Toast.makeText(listRecycler.getContext(), "No matching item found for: " + query, Toast.LENGTH_SHORT).show();
        }

    }
}