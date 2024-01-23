package com.example.music_stream_application.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.SimpleExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.ui.PlayerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.music_stream_application.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

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
    private String songUrl,title;
    @OptIn(markerClass = UnstableApi.class) @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity);

        singerName = findViewById(R.id.player_singerName);
        songName = findViewById(R.id.player_song_title_text_view);
        songImage = findViewById(R.id.player_songImg);
        playBtn = findViewById(R.id.playBtn);
        fastForward = findViewById(R.id.fastForward);
        fastRewind = findViewById(R.id.fastRewind);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        seekBar = findViewById(R.id.seekBar);
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

        Log.e("MyApp","SongUrl "+songUrl);

        songName.setText(title);
        singerName.setText(name);
        Glide.with(this)
                .load(img)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(32)))
                .into(songImage);

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
                viewCount += 1;

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
             //   playingGif.setVisibility(View.GONE);
            } else {
                mediaPlayer.start();
                isPlaying = true;
                playingGif.setVisibility(View.VISIBLE);
               // playBtn.setImageResource(R.drawable.play_icon);
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

    }
    private void addViewCount(){
        System.out.println("Count "+viewCount);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
       // String documentPath = "category/"+category+"/" + category + "/" + title;

        try {
            URI uri = new URI("https://firebasestorage.googleapis.com/v0/b/musicstream-b5a67.appspot.com/o/songs%2FClassical%2Ftere%2Ftere_ksiaudio.mp3?alt=media&token=23210a71-0016-4cc4-a75c-41f20750e01d");
            Path path = Paths.get(uri.getPath());

            boolean foundSongs = false;

            for (int i = 0; i < path.getNameCount(); i++) {
                String segment = path.getName(i).toString();

                if (foundSongs) {
                    // This is the segment following "/songs/"
                    System.out.println("Dynamic Word: " + segment);
                    break;
                }

                // Check if the current segment is "/songs/" or a similar pattern
                if (segment.toLowerCase().contains("songs")) {
                    foundSongs = true;
                }
            }

            if (!foundSongs) {
                System.out.println("URL does not contain the expected '/songs/' part.");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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

}