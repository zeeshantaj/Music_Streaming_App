package com.example.music_stream_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.music_stream_application.Adapter.Category_Adapter;
import com.example.music_stream_application.Model.CategoryModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private Category_Adapter categoryAdapter;
    private List<CategoryModel> categoryModelList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        categoryData();
    }
    private void categoryData(){
        categoryModelList = new ArrayList<>();
        categoryRecyclerView = findViewById(R.id.categories_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        categoryRecyclerView.setLayoutManager(layoutManager);

        FirebaseFirestore.getInstance().collection("category")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        categoryModelList = queryDocumentSnapshots.toObjects(CategoryModel.class);
                        categoryAdapter = new Category_Adapter(categoryModelList);
                        categoryRecyclerView.setAdapter(categoryAdapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}