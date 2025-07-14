package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    public String name;
    public List<String> musicPaths;

    public Playlist(String name) {
        this.name = name;
        this.musicPaths = new ArrayList<>();
    }
} 