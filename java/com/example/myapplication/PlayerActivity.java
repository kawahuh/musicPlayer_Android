package com.example.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private Button btnPlayPause;
    private TextView tvTitle;
    private boolean isPlaying = false;
    private RecyclerView rvLyrics;
    private LrcAdapter lrcAdapter;
    private List<LrcLine> lrcList = new ArrayList<>();
    private int currentLrcIndex = -1;
    private Runnable lrcRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnPlayPause = findViewById(R.id.btn_play_pause);
        tvTitle = findViewById(R.id.tv_title);
        rvLyrics = findViewById(R.id.rv_lyrics);
        rvLyrics.setLayoutManager(new LinearLayoutManager(this));
        lrcAdapter = new LrcAdapter(lrcList, this);
        rvLyrics.setAdapter(lrcAdapter);

        String title = getIntent().getStringExtra("music_title");
        String path = getIntent().getStringExtra("music_path");
        tvTitle.setText(title);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnPlayPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlayPause.setText("播放");
                isPlaying = false;
            } else {
                mediaPlayer.start();
                btnPlayPause.setText("暂停");
                isPlaying = true;
            }
        });

        String musicPath = getIntent().getStringExtra("music_path");
        String musicFileName = new File(musicPath).getName();
        String lyricsKey = musicFileName.substring(0, musicFileName.lastIndexOf('.')).toLowerCase() + "_lyric";
        loadLrc(lyricsKey);
        startLrcSync();
    }

    private void loadLrc(String lyricsKey) {
        lrcList.clear();
        int resId = getResources().getIdentifier(lyricsKey, "raw", getPackageName());
        if (resId != 0) {
            try (InputStream in = getResources().openRawResource(resId);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line;
                Pattern pattern = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})]\\s*(.*)");
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        int min = Integer.parseInt(matcher.group(1));
                        int sec = Integer.parseInt(matcher.group(2));
                        int ms = Integer.parseInt(matcher.group(3));
                        if (matcher.group(3).length() == 2) ms *= 10;
                        long time = min * 60 * 1000 + sec * 1000 + ms;
                        String text = matcher.group(4);
                        lrcList.add(new LrcLine(time, text));
                    }
                }
            } catch (Exception e) {
                lrcList.clear();
                lrcList.add(new LrcLine(0, "歌词加载失败"));
            }
        } else {
            lrcList.add(new LrcLine(0, "暂无歌词"));
        }
        lrcAdapter.notifyDataSetChanged();
    }

    private void startLrcSync() {
        lrcRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && lrcList.size() > 1) {
                    long pos = mediaPlayer.getCurrentPosition();
                    int highlight = -1;
                    for (int i = 0; i < lrcList.size(); i++) {
                        if (pos >= lrcList.get(i).time) {
                            highlight = i;
                        } else {
                            break;
                        }
                    }
                    if (highlight != currentLrcIndex) {
                        currentLrcIndex = highlight;
                        lrcAdapter.setHighlightPosition(currentLrcIndex);
                        if (currentLrcIndex != -1) {
                            rvLyrics.smoothScrollToPosition(currentLrcIndex);
                        }
                    }
                }
                rvLyrics.postDelayed(this, 200);
            }
        };
        rvLyrics.post(lrcRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (rvLyrics != null && lrcRunnable != null) {
            rvLyrics.removeCallbacks(lrcRunnable);
        }
    }
} 