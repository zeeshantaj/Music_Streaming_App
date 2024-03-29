package com.example.music_stream_application.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.music_stream_application.MethodUtils.MethodsUtil;
import com.example.music_stream_application.R;
import com.example.music_stream_application.Utils.FirebaseHelper;

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
    private String songUrl,title,categoryType;
    long songID;
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
                String formattedTime = MethodsUtil.millisecondsToTime(duration);
                endTime.setText(formattedTime);
                isPlaying = true;
                FirebaseHelper.updateListenCount(Player_Activity.this,categoryType,title);
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