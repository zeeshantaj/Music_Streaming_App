package com.example.music_stream_application.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_stream_application.Adapter.Category_Adapter;
import com.example.music_stream_application.Adapter.TrendingAdapter;
import com.example.music_stream_application.MainActivity;
import com.example.music_stream_application.Model.CategoryModel;
import com.example.music_stream_application.Model.SongModel;
import com.example.music_stream_application.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Home_Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_frgment, container, false);
    }

    private RecyclerView categoryRecyclerView, trendingRecyclerView;
    private Category_Adapter categoryAdapter;
    private TrendingAdapter trendingAdapter;
    private List<CategoryModel> categoryModelList;
    private List<SongModel> trendingList;
    private LinearLayout trendingContainer;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        categoryRecyclerView = view.findViewById(R.id.categories_recycler_view);
        trendingRecyclerView = view.findViewById(R.id.trending_recycler_view);
        trendingContainer = view.findViewById(R.id.trendingContainer);
        categoryData();
        trendingData();
    }

    private void categoryData() {
        categoryModelList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
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
                        Toast.makeText(getActivity(), "Error " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void trendingData() {

        trendingList = new ArrayList<>();
         // Replace with your RecyclerView ID
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        trendingRecyclerView.setLayoutManager(layoutManager);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("category").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot categoryDocument : queryDocumentSnapshots) {
                            String categoryName = categoryDocument.getId();

                            // Assuming each category has a subcollection with the same name as the category
                            CollectionReference songsCollection = categoryDocument.getReference().collection(categoryName);

                            // Query songs within the subcollection
                            songsCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot songQueryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot songDocument : songQueryDocumentSnapshots) {
                                        String songTitle = songDocument.getId();

                                        Long viewCount = songDocument.getLong("viewCount");
                                        if (viewCount != null) {
                                            int viewCountValue = viewCount.intValue();

                                            Log.d("Firestore", "Category: " + categoryName + ", Song: " + songTitle + ", ViewCount: " + viewCountValue);

                                            if (viewCount >= 2) {
                                                // add trend recyclerView item here
                                                SongModel model = songDocument.toObject(SongModel.class);
                                                trendingList.add(model);
                                                trendingContainer.setVisibility(View.VISIBLE);
                                            }

                                        } else {
                                            Log.e("Firestore", "ViewCount field not found for song: " + songTitle);
                                        }

                                    }
                                    // Update the adapter outside the inner loop
                                    trendingAdapter = new TrendingAdapter(trendingList);
                                    trendingRecyclerView.setAdapter(trendingAdapter);
                                    trendingAdapter.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Firestore", "Error getting songs for category " + categoryName, e);
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error getting categories", e);
                    }
                });
    }


}
