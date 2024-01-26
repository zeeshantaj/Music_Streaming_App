package com.example.music_stream_application.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.music_stream_application.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import java.io.IOException;
import java.util.Locale;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class Player_Activity extends AppCompatActivity {

    private TextView songName,singerName;
    private ImageView songImage;
    private MediaPlayer mediaPlayer;
    private Handler handler;

    private TextView startTime,endTime;
    private ImageView playBtn,fastForward,fastRewind,playingGif;
    private SeekBar seekBar;
    private int total;
    private boolean isPlaying;
    private int viewCount = 0;
    private String songUrl,title,categoryType;
    SharedPreferences sharedPreferences;
    long songID;
    @OptIn(markerClass = UnstableApi.class) @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity);

//        sharedPreferences = getSharedPreferences("categorySharedPreference",MODE_PRIVATE);
//        categoryType = sharedPreferences.getString("categoryType","");


        singerName = findViewById(R.id.player_singerName);
        songName = findViewById(R.id.player_song_title_text_view);
        songImage = findViewById(R.id.player_songImg);
        playBtn = findViewById(R.id.playBtn);
        fastForward = findViewById(R.id.fastForward);
        fastRewind = findViewById(R.id.fastRewind);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        seekBar = findViewById(R.id.seekBar);
        LinearLayout rootContainer = findViewById(R.id.play_activity_container);
        playingGif = findViewById(R.id.song_gif_image_view);

        handler = new Handler();
        mediaPlayer = new MediaPlayer();
        if ( mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }


        Intent intent = getIntent();
        title = intent.getStringExtra("songName");
        String name = intent.getStringExtra("singerName");
        String img = intent.getStringExtra("songImage");
        songUrl = intent.getStringExtra("songUrl");
        categoryType = intent.getStringExtra("categoryName");
        songID = intent.getLongExtra("songID",0);

        Log.e("MyApp","SongUrl "+songUrl);

        songName.setText(title);
        singerName.setText(name);
        Glide.with(this)
                .load(img)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        // Set the Drawable as the background of the container
                        rootContainer.setBackground(resource);
                    }
                });
        Glide.with(this)
                .load(img)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(132)))
                .into(songImage);

        Glide.with(this)
                .load(R.drawable.media_playing)
                .into(playingGif);


        try {
                mediaPlayer.setDataSource(songUrl);
            }
            catch (IOException e){
                e.printStackTrace();
            }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Start playing when prepared
                mediaPlayer.start();

                playingGif.setVisibility(View.VISIBLE);
                updateSeekBar();

                int duration = mp.getDuration();
                String formattedTime = millisecondsToTime(duration);
                endTime.setText(formattedTime);
                isPlaying = true;
                addViewCount();
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (percent < 100) {



                } else {

                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Handle completion if needed

                //endTime.setText(String.valueOf(total));

            }
        });
        mediaPlayer.prepareAsync();
        playBtn.setOnClickListener(v -> {
            playBtn.setImageResource(R.drawable.pause_icon);
            if (isPlaying){
                mediaPlayer.pause();
                isPlaying = false;
                playingGif.setVisibility(View.GONE);
            } else {
                mediaPlayer.start();
                isPlaying = true;
                playingGif.setVisibility(View.VISIBLE);
                playBtn.setImageResource(R.drawable.play_icon);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        fastForward.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
            }
        });
        fastRewind.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
            }
        });

        blurView();
    }
    private void blurView(){
        float radius = 20f;

        BlurView blurView = findViewById(R.id.blurView);
        View decorView = getWindow().getDecorView();
        // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

        // Optional:
        // Set drawable to draw in the beginning of each blurred frame.
        // Can be used in case your layout has a lot of transparent space and your content
        // gets a too low alpha value after blur is applied.
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView, new RenderScriptBlur(this)) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurRadius(radius);
    }
    private void addViewCount(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String documentPath = "category/"+categoryType+"/" + categoryType + "/" + title;

        System.out.println("doc Path "+documentPath);

        firestore.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentReference documentReference = firestore.document(documentPath);

                        // Retrieve current viewCount
                        DocumentSnapshot documentSnapshot = transaction.get(documentReference);
                        int currentCount = documentSnapshot.getLong("viewCount").intValue();

                        String name = documentSnapshot.get("title",String.class);
                        System.out.println("title "+name);

                        // Increment viewCount by 1
                        transaction.update(documentReference, "viewCount", currentCount + 1);

                        return null;
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Player_Activity.this, "count++", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Player_Activity.this, "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        System.out.println("Error "+e.getLocalizedMessage());
                    }
                });
    }
    public String millisecondsToTime(long milliseconds) {
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        String time;
        if (hours > 0) {
            time = String.format(Locale.US,"%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            time = String.format(Locale.US,"%02d:%02d", minutes, seconds);
        }
        return time;
    }
    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    total = mediaPlayer.getDuration();
                    seekBar.setProgress(currentPosition);
                    seekBar.setMax(total);

                    //  int currentPosition = mediaPlayer.getCurrentPosition();

                    // Calculate minutes and seconds of the current position
                    int minutes = (currentPosition / 1000) / 60;
                    int seconds = (currentPosition / 1000) % 60;

                    // Update duration TextView with the current position
                    String durationString = String.format(Locale.US,"%02d:%02d", minutes, seconds);
                    startTime.setText(durationString);

                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mediaPlayer.release();
//        mediaPlayer.stop();
    }
}