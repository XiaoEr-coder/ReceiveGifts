package com.example.receivegifts1.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.MediaStore;

import com.example.receivegifts1.R;

public class MusicService extends Service {
    private static final String TAG = "MusicService";

    private MediaPlayer player;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        player = MediaPlayer.create(this, R.raw.audio);
        player.setLooping(true);
        player.start();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        player.start();
    }
}