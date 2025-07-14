package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SongInPlaylistAdapter extends RecyclerView.Adapter<SongInPlaylistAdapter.ViewHolder> {
    private List<String> musicPaths;
    private Playlist playlist;
    private PlaylistManager playlistManager;
    private List<Playlist> allPlaylists;
    private int playlistIndex;
    private Context context;

    public SongInPlaylistAdapter(List<String> musicPaths, Playlist playlist, PlaylistManager playlistManager, List<Playlist> allPlaylists, int playlistIndex, Context context) {
        this.musicPaths = musicPaths;
        this.playlist = playlist;
        this.playlistManager = playlistManager;
        this.allPlaylists = allPlaylists;
        this.playlistIndex = playlistIndex;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song_in_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String path = musicPaths.get(position);
        String name = path.substring(path.lastIndexOf("/") + 1);
        holder.tvSong.setText(name);
        holder.btnRemove.setOnClickListener(v -> {
            musicPaths.remove(position);
            playlist.musicPaths.remove(path);
            allPlaylists.set(playlistIndex, playlist);
            playlistManager.saveAllPlaylists(allPlaylists);
            notifyItemRemoved(position);
            Toast.makeText(context, "已移除", Toast.LENGTH_SHORT).show();
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("music_title", name);
            intent.putExtra("music_path", path);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return musicPaths.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSong;
        Button btnRemove;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSong = itemView.findViewById(R.id.tv_song_name);
            btnRemove = itemView.findViewById(R.id.btn_remove_song);
        }
    }
} 