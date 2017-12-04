package com.example.aroya.aroyamusicplayer;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Intent serviceIntent;
    ImageView backet;
    ObjectAnimator backetAnime;
    SeekBar seekBar;
    TextView nowStatus,nowTime,allTime;
    Button play,stop,quit;
    private MusicService.MyBinder myBinder;
    private MusicService musicService;
    boolean musicStart=false;
    SimpleDateFormat time=new SimpleDateFormat("mm:ss");
    private AroyaServiceConnection aroyaServiceConnection=new AroyaServiceConnection();
    public Handler handler=new Handler();
    public Runnable runnable=new Runnable() {
        @Override
        public void run() {
            nowTime.setText(time.format(musicService.mp.getCurrentPosition()));
            seekBar.setProgress(musicService.mp.getCurrentPosition());
            seekBar.setMax(musicService.mp.getDuration());
            allTime.setText(time.format(musicService.mp.getDuration()));
            switch (musicService.status){
                case MusicService.MUSIC_PLAY:
                    nowStatus.setText(R.string.status_play);
                    break;
                case MusicService.MUSIC_PAUSE:
                    nowStatus.setText(R.string.status_pause);
                    break;
                case MusicService.MUSIC_STOP:
                    nowStatus.setText(R.string.status_stop);
                    break;
            }
            handler.postDelayed(runnable,200);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        serviceIntent=new Intent(MainActivity.this,MusicService.class);
        startService(serviceIntent);
        bindService(serviceIntent,aroyaServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void init(){
        verifyStoragePermissions(this);
        setContentView(R.layout.activity_main);
        backet=(ImageView) findViewById(R.id.backet);
        backet.setImageResource(R.mipmap.home_backet);
        backetAnime= ObjectAnimator.ofFloat(backet,"rotation",0f,360.0f);
        backetAnime.setDuration(10000);
        backetAnime.setInterpolator(new LinearInterpolator());
        backetAnime.setRepeatCount(-1);

        seekBar=(SeekBar) findViewById(R.id.progress);
        seekBar.setProgress(0);//0%
        seekBar.setOnSeekBarChangeListener(new seekBarChange());

        nowStatus=(TextView)findViewById(R.id.status);
        nowTime=(TextView)findViewById(R.id.time_now);
        allTime=(TextView)findViewById(R.id.time_all);

        play=(Button)findViewById(R.id.play);
        stop=(Button)findViewById(R.id.stop);
        quit=(Button)findViewById(R.id.quit);
        play.setOnClickListener(this);
        stop.setOnClickListener(this);
        quit.setOnClickListener(this);

    }
    private void start(){
        if(musicStart){
            if(backetAnime.isPaused()){
                backetAnime.resume();
            }
            else backetAnime.pause();
        }
        else{
            backetAnime.start();
            musicStart=true;
        }
        musicService.MusicPlay();
        handler.post(runnable);
    }
    private void stop(){
        musicStart=false;
        backetAnime.start();
        backetAnime.pause();
        musicService.MusicStop();
        handler.removeCallbacks(runnable);
    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.play:
                start();
                break;
            case R.id.stop:
                stop();
                break;
            case R.id.quit:
                finish();
                break;
        }
    }
    private class AroyaServiceConnection implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder){
            myBinder=(MusicService.MyBinder)iBinder;
            musicService=myBinder.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            unbindService(aroyaServiceConnection);
            aroyaServiceConnection=null;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissons[],int[] grantResults){
        if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"权限申请成功",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"权限申请失败",Toast.LENGTH_SHORT).show();
        }
    }
    public static void verifyStoragePermissions(Activity activity){
        try{
            int permission= ActivityCompat.checkSelfPermission(
                    activity,"android.permission.READ_EXTERNAL-STORAGE");
            if(permission!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private class seekBarChange implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser){
            if(fromUser==true){
                musicService.mp.seekTo(progress);
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar){

        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar){

        }

    }
}

