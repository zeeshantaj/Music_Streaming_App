package com.example.music_stream_application.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.music_stream_application.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Song_Upload_Activity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> imagePickLauncher;
    private Uri selectedImageUri;
    private ImageView songImage;
    private MaterialButton uploadBtn;
    private TextInputEditText songTile,singerName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_upload);

        songImage = findViewById(R.id.upload_song_cover_image);
        uploadBtn = findViewById(R.id.uploadBtn);
        songTile = findViewById(R.id.songTile);
        singerName = findViewById(R.id.singerName);

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

        String[] categoriesArray = {"English","Hindi","Rap","Classical","Romantic","Party"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.drop_down_item,
                categoriesArray
                );
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.uploadDropDownMenu);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Song_Upload_Activity.this, ""+autoCompleteTextView.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        uploadBtn.setOnClickListener(v -> {

            String title = songTile.getText().toString();
            String name = singerName.getText().toString();
            String category = autoCompleteTextView.getText().toString();

            if (title.isEmpty()){
                Toast.makeText(this, "SongTitle is empty", Toast.LENGTH_SHORT).show();
                return;
            }if (name.isEmpty()){
                Toast.makeText(this, "SingerName is empty", Toast.LENGTH_SHORT).show();
                return;
            }if (category.isEmpty()){
                Toast.makeText(this, "Category is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedImageUri == null) {
                Toast.makeText(this, "Image is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!songTile.getText().toString().isEmpty() && singerName.getText().toString().isEmpty()
                    && autoCompleteTextView.getText().toString().isEmpty()
                    && selectedImageUri != null){
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                String imagePath = "songs/images";
                StorageReference imageRef = storageRef.child(imagePath);
                Uri imageUri = selectedImageUri;
                imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveImageUrlInFirestore(imageUrl,title,name,autoCompleteTextView.getText().toString());
                        Toast.makeText(this, "Song Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    });

                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });


            }
        });
    }
    private void saveImageUrlInFirestore(String imageUrl,String title,String singer,String category) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("imageUrl", imageUrl);
        data.put("title", title);
        data.put("singerName", singer);
        data.put("category", category);

        // Specify the document path
        String documentPath = "category/English/"+category+"/"+title;

        // Upload the image URL to Firestore
        db.document(documentPath)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Image URL uploaded successfully
                        Log.d("Firestore", "Image URL uploaded successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure
                        Log.e("Firestore", "Error uploading image URL", e);
                    }
                });
    }
}