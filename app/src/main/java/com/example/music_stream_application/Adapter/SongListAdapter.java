package com.example.music_stream_application.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_stream_application.Model.SongListModel;
import com.example.music_stream_application.R;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.Viewholder> {
    private List<SongListModel> songListModels;

    public SongListAdapter(List<SongListModel> songListModels) {
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
        SongListModel model = songListModels.get(position);
        holder.title.setText(model.getTitle());

    }

    @Override
    public int getItemCount() {
        return songListModels.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView title,songUrl;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.list_song_title);

        }
    }
}
