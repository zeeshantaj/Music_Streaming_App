package com.example.music_stream_application.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.music_stream_application.Activities.Player_Activity;
import com.example.music_stream_application.MethodUtils.MethodsUtil;
import com.example.music_stream_application.Model.SongModel;
import com.example.music_stream_application.R;

import org.w3c.dom.Text;

import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ViewHolder> {
    private List<SongModel> modelList;

    public TrendingAdapter(List<SongModel> modelList) {
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public TrendingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trending_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingAdapter.ViewHolder holder, int position) {

        SongModel model = modelList.get(position);
        holder.songName.setText(model.getTitle());
        holder.songName.setSelected(true);
        holder.singerName.setText(model.getSingerName());
        holder.singerName.setSelected(true);
        Glide.with(holder.itemView.getContext())
                .load(model.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(32)))
                .into(holder.imageView);



        holder.itemView.setOnClickListener(v -> {
            MethodsUtil.setIntent(v.getContext(),Player_Activity.class,model);

        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView songName,singerName;
        private ImageView imageView;
        private BlurView blurView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.trendSongText);
            imageView = itemView.findViewById(R.id.trendImage);
            singerName = itemView.findViewById(R.id.trendSingerName);
            blurView = itemView.findViewById(R.id.trendBlurView);


            float radius = 20f;

// Get the activity reference using the context of the itemView
            Activity activity = (Activity) itemView.getContext();
            View decorView = activity.getWindow().getDecorView();
            ViewGroup rootView = decorView.findViewById(android.R.id.content);
            Drawable windowBackground = decorView.getBackground();

            blurView.setupWith(rootView, new RenderScriptBlur(itemView.getContext())) // or RenderEffectBlur
                    .setFrameClearDrawable(windowBackground) // Optional

                    .setBlurRadius(radius);

        }
    }
}
