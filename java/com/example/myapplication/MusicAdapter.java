package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.Toast;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    private List<Music> musicList;
    private Context context;
    private List<Playlist> playlistList;
    private PlaylistManager playlistManager;

    public MusicAdapter(List<Music> musicList, Context context) {
        this.musicList = musicList;
        this.context = context;
        this.playlistManager = new PlaylistManager(context);
        this.playlistList = playlistManager.getAllPlaylists();
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.titleText.setText(music.getTitle());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("music_title", music.getTitle());
            intent.putExtra("music_path", music.getPath());
            context.startActivity(intent);
        });
        holder.btnAddToPlaylist.setOnClickListener(v -> {
            playlistList = playlistManager.getAllPlaylists();
            if (playlistList.isEmpty()) {
                Toast.makeText(context, "请先新建歌单", Toast.LENGTH_SHORT).show();
                return;
            }
            String[] names = new String[playlistList.size()];
            for (int i = 0; i < playlistList.size(); i++) names[i] = playlistList.get(i).name;
            new AlertDialog.Builder(context)
                .setTitle("添加到歌单")
                .setItems(names, (dialog, which) -> {
                    Playlist pl = playlistList.get(which);
                    if (!pl.musicPaths.contains(music.getPath())) {
                        pl.musicPaths.add(music.getPath());
                        playlistManager.saveAllPlaylists(playlistList);
                        Toast.makeText(context, "已添加到歌单", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "该歌曲已在歌单中", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        Button btnAddToPlaylist;
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tv_music_title);
            btnAddToPlaylist = itemView.findViewById(R.id.btn_add_to_playlist);
        }
    }
} 