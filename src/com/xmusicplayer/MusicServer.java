package com.xmusicplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.domain.Music;
import com.util.Constent;
import com.util.GetMusicList;
import com.util.Lrc;
import com.util.Lrc.LrcContent;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MusicServer extends Service implements Runnable {

	public static List<Music> musiclist;	
	public static MediaPlayer musicplayer;	
	public static int service_id = 1; 
	private String cmd;
	public static int lrcIndex = 0;
	private SeekBarBroadcastReceiver seekbarReceiver;
	
	private Lrc mlrc;
	private List<LrcContent> LrcContentList = new ArrayList<Lrc.LrcContent>();
	
	Handler lrcHandler = new Handler();
	Runnable lrcRunnable = new Runnable(){
		@Override
		public void run() {
			LrcContentIndex();
			lrcHandler.postDelayed(lrcRunnable, 100);
		}
	};
	
	@Override
	public void onCreate() {
		seekbarReceiver = new SeekBarBroadcastReceiver();
		IntentFilter filter1 = new IntentFilter("com.xmusicplayer.seekbar");
		this.registerReceiver(seekbarReceiver, filter1);
		
		CmdBroadcastReceiver  CmdReciver = new CmdBroadcastReceiver();
		IntentFilter filter2 = new IntentFilter("com.xmusicplayer.cmd");
		this.registerReceiver(CmdReciver, filter2);
		
		new Thread(this).start();//死循环：每隔一段时间就广播一次进度，以便于seekbar改变状态 
		Log.i("myservice", "music service onCreate()");
		super.onCreate();
	
	}
	
	private class  SeekBarBroadcastReceiver extends BroadcastReceiver{	//接收播放进度条,改变进度

		@Override
		public void onReceive(Context context, Intent intent) {
			int i = intent.getIntExtra("seekbarPosition", 1);
			//Log.i("myservice", "receiv seekbar:"+i);
			musicplayer.seekTo(i*musicplayer.getDuration()/100);	//i的值最大是100.
			musicplayer.start();
		}
	} 
	
	private class CmdBroadcastReceiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent) {
			
			musiclist = GetMusicList.GetMusicListData(getApplicationContext());
			
			cmd = intent.getStringExtra("cmd");
			service_id = intent.getIntExtra("id", 1);
			if (cmd.equals("play") || cmd.equals("pre") || cmd.equals("next")){
				if ( null != musicplayer ){
					musicplayer.release();
					musicplayer = null;
					//Log.i("myservice", "release");
				}
				PlayMusic(service_id);
			}else if(cmd.equals("stop")){
				if(null != musicplayer){
					musicplayer.stop();
					SimpleMusicPlayer.isPlaying = false;
				}
			}else if(cmd.equals("pause")){
				if (null != musicplayer)
					musicplayer.pause();
					Log.i("myservice","musicplayer.pause();");
					SimpleMusicPlayer.isPlaying = false;
			}else if(cmd.equals("replay")){
					if (musicplayer != null){
						musicplayer.start();
						Log.i("myservice","musicplayer restart");
					}
			}
			
			if (musicplayer != null){
				musicplayer.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer mp) {
						
						musicplayer.reset();
						boolean isloop = SimpleMusicPlayer.getIsloop();
						
						if(isloop  == false){	//isloop == false顺序播放,id+1;否则不变；
							service_id ++;
							if (service_id > (musiclist.size()-1))
								service_id = 0;
						}
						PlayMusic(service_id);
					}
				});
			}
			
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.i("myservice", "onStartCommand()");
		musiclist = GetMusicList.GetMusicListData(getApplicationContext());
		return super.onStartCommand(intent, flags, startId);
	}

	public void PlayMusic(int id){
		
		Music m = musiclist.get(id);
		String url = m.getUrl();
		//Log.i("myservice", "PlayMusic() url:"+url);
		Uri uri = Uri.parse(url);
		musicplayer = new MediaPlayer();//
		musicplayer.reset();
		musicplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
		try {
			musicplayer.setDataSource(getApplicationContext(), uri);
			musicplayer.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		musicplayer.start();
		while(SimpleMusicPlayer.isPlaying == null){}	//
		SimpleMusicPlayer.isPlaying = true;
		lrcHandler.post(lrcRunnable);
		
		Intent intent = new Intent(Constent.BROCAST_TO_LIST);
		intent.putExtra("cmd", "playnewsong");
		sendBroadcast(intent);
		
	}

	@Override
	
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void run() {
		while (true){
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			if (null != musicplayer){	
				int progress = musicplayer.getCurrentPosition();
				int total = musicplayer.getDuration();
				//Intent intent = new Intent("com.xmusicplayer.progress");
				Intent intent = new Intent(Constent.BROCAST_TO_PLAYER);
				intent.putExtra("progress", progress);
				intent.putExtra("total", total);
				sendBroadcast(intent);
				
			}
			
		}
	}

	/**
	 *  根据目前播放时间，确定List<LrcContent> 中对应的的元素，对应元素的下标保存在lrcIndex
	 */
	public int LrcContentIndex() {	
		
		mlrc = new Lrc();
		mlrc.readLRC(musiclist.get(service_id).getUrl());
		LrcContentList = mlrc.getLrcList();
		
		int CurrentTime = 0;
		int TotalTime = 0;
		if (musicplayer.isPlaying()) {
			CurrentTime = musicplayer.getCurrentPosition();
			TotalTime = musicplayer.getDuration();
		}
		if (CurrentTime < TotalTime) {

			for (int i = 0; i < LrcContentList.size(); i++) {
				if (i < LrcContentList.size() - 1) {
					if (CurrentTime < LrcContentList.get(i).getLrc_time() && i == 0) {
						lrcIndex = i;
					}	//getLrc_time() 得到的值，是歌词解析后，相对应的时间（单位毫秒）
					if (CurrentTime > LrcContentList.get(i).getLrc_time()
							&& CurrentTime < LrcContentList.get(i + 1).getLrc_time()) {
						lrcIndex = i;
					}
				}
				if (i == LrcContentList.size() - 1
						&& CurrentTime > LrcContentList.get(i).getLrc_time()) {
					lrcIndex = i;
				}
			}
		}
		return lrcIndex;
	}

	@Override
	public void onDestroy() {
		this.unregisterReceiver(seekbarReceiver);
		super.onDestroy();
	}
}
