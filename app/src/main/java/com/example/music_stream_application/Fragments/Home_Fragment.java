package com.example.music_stream_application.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_stream_application.Adapter.Category_Adapter;
import com.example.music_stream_application.MainActivity;
import com.example.music_stream_application.Model.CategoryModel;
import com.example.music_stream_application.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Home_Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_frgment,container,false);
    }
    private RecyclerView categoryRecyclerView;
    private Category_Adapter categoryAdapter;
    private List<CategoryModel> categoryModelList;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        categoryRecyclerView = view.findViewById(R.id.categories_recycler_view);
        categoryData();
    }
    private void categoryData(){
        categoryModelList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
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
                        Toast.makeText(getActivity(), "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
