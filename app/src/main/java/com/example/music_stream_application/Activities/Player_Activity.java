package com.example.music_stream_application.Activities;

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

import com.bumptech.glide.Glide;
import com.example.music_stream_application.R;

import java.io.IOException;
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
        String title = intent.getStringExtra("songName");
        String name = intent.getStringExtra("singerName");
        String img = intent.getStringExtra("songImage");
        String songUrl = intent.getStringExtra("songUrl");

        Log.e("MyApp","SongUrl "+songUrl);

        songName.setText(title);
        singerName.setText(name);
        Glide.with(this)
                .load(img)
                .into(songImage);
//        Glide.with(this)
//                .load(R.drawable.media_playing)
//                .circleCrop()
//                .into(playingGif);


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