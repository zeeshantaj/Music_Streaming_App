package com.example.music_stream_application.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.example.music_stream_application.Adapter.Category_Adapter;
import com.example.music_stream_application.Adapter.SongListAdapter;
import com.example.music_stream_application.MainActivity;
import com.example.music_stream_application.Model.CategoryModel;
import com.example.music_stream_application.Model.SongListModel;
import com.example.music_stream_application.Model.SongModel;
import com.example.music_stream_application.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Song_List_Activity extends AppCompatActivity {

    private RecyclerView listRecycler;
    private List<SongModel> songListModelList;
    private SongListAdapter listAdapter;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);


        Intent intent = getIntent();
        String name = intent.getStringExtra("categoryName");
        Log.e("MyApp","cat name "+name);


        sharedPreferences = getSharedPreferences("categorySharedPreference",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("categoryType", name); // Use the appropriate method based on the data type
        editor.apply();

        songListData(name);
    }

    @Override
    protected void onDestroy() {
        editor.clear();
        Log.e("MyApp","song list actvity on destory");
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        Log.e("MyApp","song list actvity on stop");

        super.onStop();
    }

    private void songListData(String path){
        songListModelList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        listRecycler = findViewById(R.id.songListRecycler);
        listRecycler.setLayoutManager(layoutManager);

        FirebaseFirestore.getInstance().collection("category").document(path).collection(path)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        songListModelList = queryDocumentSnapshots.toObjects(SongModel.class);
                        listAdapter = new SongListAdapter(songListModelList);
                        listRecycler.setAdapter(listAdapter);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Song_List_Activity.this, "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



    }

}