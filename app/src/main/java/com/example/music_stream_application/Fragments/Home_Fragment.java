package com.example.music_stream_application.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.music_stream_application.Activities.Song_List_Activity;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

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
    private List<String> imageList;

    private LinearLayout allSongContainer;
    private ImageView[] imageViews;
    private int currentPosition = 0;
    private Handler handler = new Handler();
    private static final int ANIMATION_DURATION = 1000; // in milliseconds

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        categoryRecyclerView = view.findViewById(R.id.categories_recycler_view);
        trendingRecyclerView = view.findViewById(R.id.trending_recycler_view);
        trendingContainer = view.findViewById(R.id.trendingContainer);
        allSongContainer = view.findViewById(R.id.allSongContainer);
        categoryData();
        trendingData();
        getAllSongImage();

        allSongContainer.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Song_List_Activity.class);
            intent.putExtra("isCategory",false);

            v.getContext().startActivity(intent);
        });

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
                                            //Log.d("Firestore", "Category: " + categoryName + ", Song: " + songTitle + ", ViewCount: " + viewCountValue);
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

    private void getAllSongImage(){
        imageList = new ArrayList<>();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("category").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot categoryDocument : queryDocumentSnapshots) {
                        String categoryName = categoryDocument.getId();

                        // Assuming each category has a subcollection with the same name as the category
                        CollectionReference songsCollection = categoryDocument.getReference().collection(categoryName);

                        // Query songs within the subcollection
                        songsCollection.get().addOnSuccessListener(songQueryDocumentSnapshots -> {
                            int counter = 0;
                            for (QueryDocumentSnapshot songDocument : songQueryDocumentSnapshots) {
                                String imageUrl = songDocument.get("imageUrl",String.class);
                                imageList.add(imageUrl);
                                counter++;
                                if (counter >= 15) {
                                    // Stop the loop if 15 URLs are loaded
                                    break;
                                }

                                Log.e("Firestore", "image url "+counter + imageUrl);
                            }
                            loadImagesIntoImageViews();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("Firestore", "Error getting image " + categoryName, e);
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error getting categories", e);
                });
    }

    private void loadImagesIntoImageViews() {
        // Assuming you have an array of ImageViews in your layout
        imageViews = new ImageView[]{
                getActivity().findViewById(R.id.img),
                getActivity().findViewById(R.id.img1),
                getActivity().findViewById(R.id.img2),
                getActivity().findViewById(R.id.img3),
                getActivity().findViewById(R.id.img4),
                getActivity().findViewById(R.id.img5),
                getActivity().findViewById(R.id.img6),
                getActivity().findViewById(R.id.img7),
                getActivity().findViewById(R.id.img8),
                getActivity().findViewById(R.id.img9),
                getActivity().findViewById(R.id.img10),
                getActivity().findViewById(R.id.img11),
                getActivity().findViewById(R.id.img12),
                getActivity().findViewById(R.id.img13),
                getActivity().findViewById(R.id.img14)
        };

        // Loop through the imageList and load each URL into the corresponding ImageView using Glide
        for (int i = 0; i < Math.min(imageList.size(), imageViews.length); i++) {
            String imageUrl = imageList.get(i);
            ImageView imageView = imageViews[i];

            Glide.with(requireContext())
                    .load(imageUrl)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(132)))
                    .into(imageView);
        }
        loadImages();

        // Schedule the animation task to run every 5 seconds
        handler.postDelayed(imageChangeRunnable, 5000);
        blurView();
    }
    private Runnable imageChangeRunnable = new Runnable() {
        @Override
        public void run() {
            // Increment the current position
            currentPosition = (currentPosition + 1) % imageList.size();

            // Load new images and animate positions
            loadImages();

            // Schedule the next iteration after 5 seconds
            handler.postDelayed(this, 5000);
        }
    };
    private void loadImages() {
        for (int i = 0; i < Math.min(imageList.size(), imageViews.length); i++) {
            int newPosition = (i + currentPosition) % imageViews.length;
            String imageUrl = imageList.get(i);
            ImageView imageView = imageViews[newPosition];

            // Use ObjectAnimator to animate alpha for fading in and out
            ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f);
            fadeAnimator.setDuration(ANIMATION_DURATION); // Adjust duration as needed

            // Use an AnimatorListener to load new image after animation completes
            fadeAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // Load new image into the ImageView using Glide after animation completes
                    Glide.with(requireContext())
                            .load(imageUrl)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(132)))
                            .into(imageView);

                    // Reset alpha to prepare for the next animation
                    imageView.setAlpha(1f);
                }
            });

            fadeAnimator.start();
        }
    }
    private void blurView(){
        float radius = 20f;
        BlurView blurView =  getActivity().findViewById(R.id.allSongBlueView);
        View decorView = getActivity().getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView, new RenderScriptBlur(getActivity())) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurRadius(radius);
    }
}
