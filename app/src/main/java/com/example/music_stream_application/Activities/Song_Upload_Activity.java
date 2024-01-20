package com.example.music_stream_application.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.music_stream_application.R;
import com.github.dhaval2404.imagepicker.ImagePicker;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Song_Upload_Activity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> imagePickLauncher;
    private Uri selectedImageUri;
    private ImageView songImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_upload);

        songImage = findViewById(R.id.upload_song_cover_image);

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            songImage.setImageURI(selectedImageUri);
                        }
                    }
                });
        songImage.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .cropSquare()	    			//Crop image(Optional), Check Customization for more option
                    .compress(512)			//Final image size will be less than 4 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });



    }
}