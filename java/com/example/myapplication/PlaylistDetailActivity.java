package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity {
    private RecyclerView rv;
    private PlaylistManager playlistManager;
    private Playlist playlist;
    private SongInPlaylistAdapter adapter;
    private List<String> musicPaths = new ArrayList<>();
    private int playlistIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);
        RecyclerView rv = findViewById(R.id.rv_playlist_detail);
        rv.setLayoutManager(new LinearLayoutManager(this));
        playlistIndex = getIntent().getIntExtra("playlist_index", -1);
        playlistManager = new PlaylistManager(this);
        List<Playlist> all = playlistManager.getAllPlaylists();
        if (playlistIndex >= 0 && playlistIndex < all.size()) {
            playlist = all.get(playlistIndex);
            musicPaths.addAll(playlist.musicPaths);
        }
        adapter = new SongInPlaylistAdapter(musicPaths, playlist, playlistManager, all, playlistIndex, this);
        rv.setAdapter(adapter);
    }
} 