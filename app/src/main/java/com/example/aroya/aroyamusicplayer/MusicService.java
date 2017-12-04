package com.example.aroya.aroyamusicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.widget.Toast;

/**
 * Created by Aroya on 11/26/2017.
 */

public class MusicService extends Service{
    public static final int MUSIC_PLAY=0;
    public static final int MUSIC_STOP=1;
    public static final int MUSIC_EXIT=2;
    public static final int MUSIC_FRESH=3;
    public static final int MUSIC_JUMP=4;
    public static final int MUSIC_PAUSE=5;
    public MyBinder myBinder=new MyBinder();
    public MediaPlayer mp;
    public int status=MUSIC_STOP;

    //基础服务架构
    @Override
    public void onCreate(){
        super.onCreate();
        Toast.makeText(getApplicationContext(),"Create MusicService...",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"Destroy MusicService...",Toast.LENGTH_SHORT).show();
    }
    @Override
    public IBinder onBind(Intent intent){
        return myBinder;
    }
    @Override
    public boolean onUnbind(Intent intent){
        mp.reset();
        stopSelf();
        return super.onUnbind(intent);
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        return super.onStartCommand(intent,flags,startId);
    }

    public MusicService(){
        mp=new MediaPlayer();
        try{
            mp.setDataSource("/sdcard/Home.mp3");
            mp.prepare();
            mp.setLooping(true);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void MusicPlay(){
        if(mp.isPlaying()){
            status=MUSIC_PAUSE;
            mp.pause();
        }
        else {
            status=MUSIC_PLAY;
            mp.start();
        }
    }
    public void MusicStop(){
        if(mp!=null){
            status=MUSIC_STOP;
            mp.stop();
            try{
                mp.prepare();
                mp.seekTo(0);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    class MyBinder extends Binder{
        protected MusicService getService(){
            return MusicService.this;
        }
//        @Override
//        protected boolean onTransact(int code, Parcel data,Parcel reply,
//                                     int flags)throws RemoteException{
//            switch (code){
//                case MUSIC_PLAY:
//                    MusicPlay();
//                    break;
//                case MUSIC_STOP:
//                    MusicStop();
//                    break;
//                case MUSIC_EXIT:
//                    MusicStop();
//                    break;
//                case MUSIC_FRESH:
//                    break;
//                case MUSIC_JUMP:
//                    break;
//            }
//            return super.onTransact(code,data,reply,flags);
//        }
    }
}
