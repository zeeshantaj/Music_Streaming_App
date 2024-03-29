package com.example.music_stream_application.Fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.music_stream_application.Activities.Song_List_Activity;
import com.example.music_stream_application.Activities.Song_Upload_Activity;
import com.example.music_stream_application.Login.LoginActivity;
import com.example.music_stream_application.MethodUtils.MethodsUtil;
import com.example.music_stream_application.Model.SongModel;
import com.example.music_stream_application.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Upload_Song_Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_song_upload,container,false);
    }
    private ActivityResultLauncher<Intent> imagePickLauncher;
    private ActivityResultLauncher<String> audioPickLauncher;
    private Uri selectedImageUri, selectedAudioUri;
    private ImageView songImage;
    private MaterialButton uploadBtn;
    private TextInputEditText songTile, singerName;
    private TextView audioPickTxt;
    String[] categoriesArray = {"English", "Rap", "Classical", "Romantic", "Party"};
    private ProgressBar uploadProgressBar;
    private TextView progressPercentageTextView;
    private String category;
    private ConstraintLayout uploadSongContainer;

    private FirebaseAuth auth;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        songImage = view.findViewById(R.id.upload_song_cover_image);
        uploadBtn = view.findViewById(R.id.uploadBtn);
        songTile = view.findViewById(R.id.songTile);
        singerName = view.findViewById(R.id.singerName);
        audioPickTxt = view.findViewById(R.id.audioPickTxt);
        uploadSongContainer = view.findViewById(R.id.uploadSongContainer);
        uploadProgressBar = view.findViewById(R.id.your_upload_progress_bar_id);
        progressPercentageTextView = view.findViewById(R.id.your_progress_percentage_text_view_id);

        auth = FirebaseAuth.getInstance();

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK) {
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
                        String fileName = MethodsUtil.getFileName(selectedAudioUri,getActivity());
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
                getActivity(),
                R.layout.drop_down_item,
                categoriesArray
        );
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.uploadDropDownMenu);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "" + autoCompleteTextView.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        uploadBtn.setOnClickListener(v -> {


            FirebaseUser user = auth.getCurrentUser();


            String title = songTile.getText().toString();
            String name = singerName.getText().toString();
            String UID = auth.getUid();
            category = autoCompleteTextView.getText().toString();
            if (user == null){
                Toast.makeText(getActivity(), "User is not authenticated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }
            if (UID.isEmpty()){
                Toast.makeText(getActivity(), "User not present", Toast.LENGTH_SHORT).show();
            }
            if (title.isEmpty()) {
                Toast.makeText(getActivity(), "SongTitle is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (name.isEmpty()) {
                Toast.makeText(getActivity(), "SingerName is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (category.isEmpty()) {
                Toast.makeText(getActivity(), "Category is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedImageUri == null) {
                Toast.makeText(getActivity(), "Image is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedAudioUri == null) {
                Toast.makeText(getActivity(), "Image is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            StorageReference songTitleRef = storageRef.child("songs/" + category + "/" + title);

            //for image
            StorageReference songImageRef = songTitleRef.child(title + "_" + name + "image.jpg");
            UploadTask uploadImageTask = songImageRef.putFile(selectedImageUri);

            // for audio
            StorageReference songAudioRef = songTitleRef.child(title + "_" + name + "audio.mp3");
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

                    updateProgressUI(progress);

                });

                task.addOnSuccessListener(taskSnapshot -> {
                    // Handle success
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        if (uri != null) {
                            downloadUrls.add(uri);
                            tasksCompleted.incrementAndGet();

                            if (tasksCompleted.get() == totalTasks) {
                                if (!downloadUrls.isEmpty()) {
                                    Uri imageDownloadUrl = downloadUrls.get(0);
                                    Uri audioDownloadUrl = downloadUrls.get(1);


                                    long id = System.currentTimeMillis();

                                    int viewCount = 0;

                                    SongModel songModel = new SongModel(title, name, imageDownloadUrl.toString(), audioDownloadUrl.toString(),id, viewCount,category,UID);


                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    String documentPath = "category/"+category+"/" + category + "/" + title;

                                    db.document(documentPath)
                                            .set(songModel, SetOptions.merge())
                                            .addOnSuccessListener(unused -> {
                                                btnUiUpdate();
                                                Toast.makeText(getActivity(), "Song Uploaded Successfully ", Toast.LENGTH_SHORT).show();
                                                snackBar();
                                            }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                                }
                            }



                        }
                    });


                }).addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    uploadBtn.setEnabled(true);
                });
            }

        });
    }
    private void snackBar(){
//        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content),
//                "Upload complete!",
//                Snackbar.LENGTH_INDEFINITE);
//        snackbar.setAction("View", v -> {
//
//            Intent intent = new Intent(getActivity(), Song_List_Activity.class);
//            intent.putExtra("categoryName",category);
//            intent.putExtra("isCategory",true);
//            startActivity(intent);
//        });
//
//        // Show the Snackbar
//        snackbar.show();


        Snackbar snackbar = Snackbar.make(uploadSongContainer, "", Snackbar.LENGTH_INDEFINITE);
        View customSnackbarView = getLayoutInflater().inflate(R.layout.custom_snackbar, null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT); // Make Snackbar background transparent
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.addView(customSnackbarView, 0);

        Button btnView = customSnackbarView.findViewById(R.id.btnView);
        Button btnDismiss = customSnackbarView.findViewById(R.id.btnDismiss);

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Song_List_Activity.class);
                intent.putExtra("categoryName", category);
                intent.putExtra("isCategory", true);
                startActivity(intent);
                snackbar.dismiss(); // Dismiss the snackbar if needed
            }
        });

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss(); // Dismiss the snackbar if needed
                // Your negative action logic here
            }
        });

        snackbar.show();
    }

    private void btnUiUpdate() {
        uploadBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_check_24, 0);
        uploadBtn.setText("Uploaded");
        uploadBtn.setBackgroundColor(getResources().getColor(R.color.green));
        uploadBtn.setEnabled(false);
    }

    private void updateProgressUI(int progress) {
        // Update the progress bar
        uploadProgressBar.setVisibility(View.VISIBLE);
        progressPercentageTextView.setVisibility(View.VISIBLE);
        uploadProgressBar.setProgress(progress);

        // Update the progress percentage text view
        String progressText = progress + "%";
        progressPercentageTextView.setText(progressText);
    }
}
