package com.example.music_stream_application.Activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.music_stream_application.R;
import com.example.music_stream_application.Utils.FirebaseUtils;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Song_Upload_Activity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> imagePickLauncher;
    private ActivityResultLauncher<String> audioPickLauncher;
    private Uri selectedImageUri,selectedAudioUri;
    private ImageView songImage;
    private MaterialButton uploadBtn;
    private TextInputEditText songTile, singerName;
    private TextView audioPickTxt;
    private static final int PICK_AUDIO_REQUEST = 99;
    String[] categoriesArray = {"English", "Hindi", "Rap", "Classical", "Romantic", "Party"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_upload);

        songImage = findViewById(R.id.upload_song_cover_image);
        uploadBtn = findViewById(R.id.uploadBtn);
        songTile = findViewById(R.id.songTile);
        singerName = findViewById(R.id.singerName);
        audioPickTxt = findViewById(R.id.audioPickTxt);

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

        audioPickLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        selectedAudioUri = result;
                        String fileName = getFileName(selectedAudioUri);
                        audioPickTxt.setText(fileName);
                    }
                });

        songImage.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .cropSquare()                    //Crop image(Optional), Check Customization for more option
                    .compress(512)            //Final image size will be less than 4 MB(Optional)
                    .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });
        audioPickTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioPickLauncher.launch("audio/*");
            }
        });


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
                Toast.makeText(Song_Upload_Activity.this, "" + autoCompleteTextView.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        uploadBtn.setOnClickListener(v -> {

            String title = songTile.getText().toString();
            String name = singerName.getText().toString();
            String category = autoCompleteTextView.getText().toString();

            if (title.isEmpty()) {
                Toast.makeText(this, "SongTitle is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (name.isEmpty()) {
                Toast.makeText(this, "SingerName is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (category.isEmpty()) {
                Toast.makeText(this, "Category is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedImageUri == null) {
                Toast.makeText(this, "Image is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedAudioUri == null) {
                Toast.makeText(this, "Image is empty", Toast.LENGTH_SHORT).show();
                return;
            }


                StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                StorageReference songTitleRef = storageRef.child("songs/" +category+"/"+ title);

                //for image
                StorageReference songImageRef = songTitleRef.child(title+"_"+name+"image.jpg");
                UploadTask uploadImageTask = songImageRef.putFile(selectedImageUri);

                // for audio
                StorageReference songAudioRef = songTitleRef.child(title+"_"+name+"audio.mp3");
                UploadTask uploadAudioTask = songAudioRef.putFile(selectedAudioUri);

                // to combine both task
                List<UploadTask> uploadTasks = new ArrayList<>();
                uploadTasks.add(uploadImageTask);
                uploadTasks.add(uploadAudioTask);

                List<Uri> downloadUrls = new ArrayList<>();



            int totalTasks = uploadTasks.size();
                final AtomicInteger tasksCompleted = new AtomicInteger(0);
                for (UploadTask task : uploadTasks) {
                    task.addOnProgressListener(taskSnapshot -> {
                        // Update progress here
                        long bytesTransferred = taskSnapshot.getBytesTransferred();
                        long totalBytes = taskSnapshot.getTotalByteCount();
                        int progress = (int) ((bytesTransferred * 100) / totalBytes);

                        // You can use the progress variable for updating a progress bar or UI
                        Log.d("UploadProgress", "Progress: " + progress + "%");
                    });

                task.addOnSuccessListener(taskSnapshot -> {
                    // Handle success
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uri != null) {
                                downloadUrls.add(uri);
                                tasksCompleted.incrementAndGet();

                                if (tasksCompleted.get() == totalTasks) {
                                    if (!downloadUrls.isEmpty()) {
                                        Uri imageDownloadUrl = downloadUrls.get(0);
                                        Uri audioDownloadUrl = downloadUrls.get(1);
                                        Log.e("MyApp", "image url" + imageDownloadUrl);
                                        Log.e("MyApp", "audio url" + audioDownloadUrl);
                                    } else {
                                        // Handle the case where downloadUrls is empty
                                        Log.e("MyApp", "Download URLs list is empty");
                                    }
                                }

                                Toast.makeText(Song_Upload_Activity.this, "Completed ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });




                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK) {
            // The user picked an audio file. Handle the selected file here.
            selectedAudioUri = data.getData();
            String fileName = getFileName(selectedAudioUri);
            audioPickTxt.setText(fileName);
            // Do something with the selected audio file URI
        }
    }

    private void saveImageUrlInFirestore(String imageUrl, String title, String singer, String category) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("imageUrl", imageUrl);
        data.put("title", title);
        data.put("singerName", singer);
        data.put("category", category);

        // Specify the document path
        String documentPath = "category/English/" + category + "/" + title;

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

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        } else if (uri.getScheme().equals("file")) {
            result = new File(uri.getPath()).getName();
        }
        return result;
    }
}