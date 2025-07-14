package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlaylistManager {
    private static final String PREF_NAME = "playlists";
    private static final String KEY_LIST = "playlist_list";
    private SharedPreferences sp;
    private Gson gson = new Gson();

    public PlaylistManager(Context context) {
        sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<Playlist> getAllPlaylists() {
        String json = sp.getString(KEY_LIST, "");
        if (json.isEmpty()) return new ArrayList<>();
        Type type = new TypeToken<List<Playlist>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void saveAllPlaylists(List<Playlist> list) {
        String json = gson.toJson(list);
        sp.edit().putString(KEY_LIST, json).apply();
    }

    public void addPlaylist(Playlist playlist) {
        List<Playlist> list = getAllPlaylists();
        list.add(playlist);
        saveAllPlaylists(list);
    }
} 