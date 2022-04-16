package com.example.receivegifts1.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.receivegifts1.R;
import com.example.receivegifts1.Service.MusicService;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private AudioManager am;
    boolean isActivityAlive = false;

    /**
     * 需要点击次数满足才会退出
     */
    private int num = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        isActivityAlive = true;
        //防止重新加载
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

        //隐藏状态栏标题栏及导航栏
        hideLaLayout();
        //获取音频服务
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //启动线程循环设置音量


        new Thread() {
            @Override
            public void run() {
                //耗时操作，完成之后更新UI
                Log.d(TAG, "run: ");
                while (true) {
                    final int m = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    Log.d(TAG, "run() m: "+m);
                    runOnUiThread(() ->
                            am.setStreamVolume(AudioManager.STREAM_MUSIC, m, AudioManager.FLAG_PLAY_SOUND));
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        //启动服务播放音乐
        final Intent intent = new Intent(getApplicationContext(), MusicService.class);
        startService(intent);
        Log.d(TAG, "start music service");
        //按钮点击事件
        final Button bt = findViewById(R.id.activitymainButton);
        bt.setOnClickListener(view -> {
            Log.d(TAG, "setOnClickListener: ");
            if (num != 0) {
                num--;
                bt.setText("再点" + num + "下就关闭程序");
            } else {
                ////停止服务并关闭音乐退出软件
                Log.d(TAG, "stop music service ");
                stopService(intent);
                finish();
                ActivityDestroy();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isActivityAlive = true;
        Log.d(TAG, "onRestart:  hideLaLayout");
        hideLaLayout();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown Event: " + event.getAction());
        if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            Toast toast = Toast.makeText(this, null, Toast.LENGTH_LONG);
            toast.setText("放弃吧，没用的！");
            toast.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void hideLaLayout() {
        Log.d(TAG, "hideLaLayout: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // 全屏显示，隐藏状态栏和导航栏，拉出状态栏和导航栏显示一会儿后消失。
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                // 全屏显示，隐藏状态栏
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        }
    }

    private void ActivityDestroy() {
        if (isActivityAlive) {
            Intent StopIntent = new Intent(this.getApplicationContext(), MusicService.class);
            stopService(StopIntent);
            try {
                Log.d(TAG, "Destroy: sleep 200ms");
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onStop();
            onDestroy();
        }
        else {
            isActivityAlive =false;
            Log.d(TAG, "Destroy: isActivityAlive: "+ isActivityAlive);
        }
    }


}