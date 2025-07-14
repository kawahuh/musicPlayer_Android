package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private List<Playlist> playlistList;
    private Context context;

    public PlaylistAdapter(List<Playlist> playlistList, Context context) {
        this.playlistList = playlistList;
        this.context = context;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        holder.tvName.setText(playlistList.get(position).name);
        holder.itemView.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onClick(position);
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onDelete(position);
        });
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public void updateData(List<Playlist> newList) {
        this.playlistList = newList;
        notifyDataSetChanged();
    }

    public interface OnPlaylistActionListener {
        void onDelete(int position);
        void onClick(int position);
    }
    private OnPlaylistActionListener actionListener;
    public void setOnPlaylistActionListener(OnPlaylistActionListener listener) {
        this.actionListener = listener;
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        Button btnDelete;
        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_playlist_name);
            btnDelete = itemView.findViewById(R.id.btn_delete_playlist);
        }
    }
} 