package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import android.widget.TextView;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import com.example.myapplication.PlaylistAdapter;
import com.example.myapplication.Playlist;
import com.example.myapplication.PlaylistManager;
import com.example.myapplication.Music;
import com.example.myapplication.MusicAdapter;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private List<Music> musicList = new ArrayList<>();
    private static final int REQUEST_PERMISSION = 1001;
    private TextView tvMusicCount;
    private PlaylistManager playlistManager;
    private List<Playlist> playlistList;
    private RecyclerView rvPlaylist;
    private PlaylistAdapter playlistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        tvMusicCount = findViewById(R.id.tv_music_count);

        // 自动复制 raw 音乐到私有目录
        copyRawToMusicLocal(R.raw.tianxia_zhangjie, "tianxia_zhangjie.mp3");
        copyRawToMusicLocal(R.raw.pianai_zhangyunjing, "pianai_zhangyunjing.mp3");
        copyRawToMusicLocal(R.raw.hongseggaogenxie_caijianya, "hongseggaogenxie_caijianya.mp3");
        copyRawToMusicLocal(R.raw.weiyi_dengziqi, "weiyi_dengziqi.mp3");

        recyclerView = findViewById(R.id.recyclerViewMusic);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        musicAdapter = new MusicAdapter(musicList, this);
        recyclerView.setAdapter(musicAdapter);

        loadMusicFiles();

        playlistManager = new PlaylistManager(this);
        playlistList = playlistManager.getAllPlaylists();
        rvPlaylist = findViewById(R.id.rv_playlist);
        rvPlaylist.setLayoutManager(new LinearLayoutManager(this));
        playlistAdapter = new PlaylistAdapter(playlistList, this);
        rvPlaylist.setAdapter(playlistAdapter);

        playlistAdapter.setOnPlaylistActionListener(new PlaylistAdapter.OnPlaylistActionListener() {
            @Override
            public void onDelete(int position) {
                playlistList.remove(position);
                playlistManager.saveAllPlaylists(playlistList);
                playlistAdapter.updateData(playlistList);
                Toast.makeText(MainActivity.this, "歌单已删除", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(MainActivity.this, PlaylistDetailActivity.class);
                intent.putExtra("playlist_index", position);
                startActivity(intent);
            }
        });

        Button btnNewPlaylist = new Button(this);
        btnNewPlaylist.setText("新建歌单");
        btnNewPlaylist.setOnClickListener(v -> showNewPlaylistDialog());
        LinearLayout layout = (LinearLayout) findViewById(R.id.main_linear_layout);
        layout.addView(btnNewPlaylist, 1); // 添加到按钮下方

        // 添加跳转到Main2Activity的按钮点击事件
        Button btnGotoMain2 = findViewById(R.id.btn_goto_main2);
        btnGotoMain2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAnchorView(R.id.fab)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void copyRawToMusicLocal(int rawResId, String outFileName) {
        File musicDir = getExternalFilesDir("music_local");
        if (musicDir == null) return;
        File outFile = new File(musicDir, outFileName);
        if (outFile.exists()) return;
        try (InputStream in = getResources().openRawResource(rawResId);
             OutputStream out = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMusicFiles() {
        musicList.clear();
        File musicDir = getExternalFilesDir("music_local");
        if (musicDir != null && musicDir.exists() && musicDir.isDirectory()) {
            File[] files = musicDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".mp3")) {
                        musicList.add(new Music(file.getName(), file.getAbsolutePath()));
                    }
                }
            }
        }
        musicAdapter.notifyDataSetChanged();
        if (tvMusicCount != null) {
            tvMusicCount.setText("音乐数量: " + musicList.size());
        }
        if (musicList.isEmpty()) {
            Toast.makeText(this, "未找到音乐文件", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNewPlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_new_playlist, null);
        EditText etName = dialogView.findViewById(R.id.et_playlist_name);
        builder.setTitle("新建歌单")
                .setView(dialogView)
                .setPositiveButton("确定", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (!name.isEmpty()) {
                        Playlist playlist = new Playlist(name);
                        playlistManager.addPlaylist(playlist);
                        playlistList = playlistManager.getAllPlaylists();
                        playlistAdapter.updateData(playlistList);
                        Toast.makeText(this, "歌单创建成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "歌单名不能为空", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}