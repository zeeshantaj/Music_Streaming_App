package com.example.music_stream_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.music_stream_application.Adapter.Category_Adapter;
import com.example.music_stream_application.Fragments.Home_Fragment;
import com.example.music_stream_application.Fragments.Upload_Song_Fragment;
import com.example.music_stream_application.Model.CategoryModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    private BottomNavigationView navigationView;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.bottomSheetNavigation);
        frameLayout = findViewById(R.id.homeParentFrameLayout);

        setFragment(new Home_Fragment());

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navHome){
                    setFragment(new Home_Fragment());
                }
                if (id == R.id.navUploadSong){
                    setFragment(new Upload_Song_Fragment());
                }
                return true;
            }
        });
    }
    private void setFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.homeParentFrameLayout,fragment);
        //transaction.commit();
        if (fragment instanceof Home_Fragment){
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}