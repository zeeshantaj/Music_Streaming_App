package com.example.music_stream_application.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music_stream_application.Activities.Player_Activity;
import com.example.music_stream_application.MethodUtils.MethodsUtil;
import com.example.music_stream_application.Model.SongModel;
import com.example.music_stream_application.R;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.Viewholder> {
    private List<SongModel> songListModels;

    public SongListAdapter(List<SongModel> songListModels) {
        this.songListModels = songListModels;
    }

    @NonNull
    @Override
    public SongListAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_item_recycler,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongListAdapter.Viewholder holder, int position) {
        SongModel model = songListModels.get(position);
        holder.title.setText(model.getTitle());
        holder.singerName.setText(model.getSingerName());

        Glide.with(holder.itemView.getContext())
                .load(model.getImageUrl())
                .into(holder.imageView);


        holder.itemView.setOnClickListener(v -> {
            MethodsUtil.setIntent(v.getContext(),Player_Activity.class,model);
        });



    }

    @Override
    public int getItemCount() {
        return songListModels.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView title,singerName;
        private ImageView imageView;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.list_song_title);
            singerName = itemView.findViewById(R.id.song_subtitle_text_view);
            imageView = itemView.findViewById(R.id.list_song_cover_image);

        }
    }
}
