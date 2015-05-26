package com.xmusicplayer;

import java.util.ArrayList;
import java.util.List;

import com.domain.Music;
import com.util.Constent;
import com.util.GetMusicList;
import com.util.Lrc;
import com.util.LrcView;
import com.util.Lrc.LrcContent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SimpleMusicPlayer extends Activity implements  OnGestureListener{
	
	private static LrcView lrctextview;
	private List<LrcContent> LrcContentList = new ArrayList<Lrc.LrcContent>();
	
	private Button btn_pre;
	private Button btn_pause_or_play;
	private Button btn_loop;
	private Button btn_next;
	private SeekBar seekbar;
	private TextView singer_song;

	private static Boolean isloop = false;
	private Boolean isInfront = false;
	private int songId = 1;
	public static Boolean isPlaying = false;
	public static List<Music> musiclist;
	private seekbarBroadcastReciever reciever;
	private static SimpleMusicPlayer smp;
	
	private final int FLING_MIN_DISTANCE = 100;
	private GestureDetector gd;
	
	private Lrc mlrc;
	Handler lrcHandler = new Handler();
	Runnable lrcRunnable = new Runnable(){
		@Override
		public void run() {
			lrctextview.setIndex(MusicServer.lrcIndex);
			lrctextview.invalidate();
			lrcHandler.postDelayed(lrcRunnable, 100);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.simple_music_player);
		Log.i("myservice", "music player onCreate()");
		
		btn_pre = (Button) findViewById(R.id.simple_music_player_btn_pre);
		btn_pause_or_play = (Button) findViewById(R.id.simple_music_player_btn_pause_or_play);
		btn_next = (Button) findViewById(R.id.simple_music_player_btn_next);
		btn_loop = (Button) findViewById(R.id.simple_music_player_btn_loop);
		seekbar = (SeekBar) findViewById(R.id.simple_music_player_seekbar);
		lrctextview = (LrcView) findViewById(R.id.simple_music_player_lrctextview);
		singer_song = (TextView) findViewById(R.id.simple_music_player_textview_singer_song);
		
		btn_pre.setOnClickListener(new btnlistener());
		btn_pause_or_play.setOnClickListener(new btnlistener());
		btn_loop.setOnClickListener(new btnlistener());
		btn_next.setOnClickListener(new btnlistener());
		
		musiclist = GetMusicList.GetMusicListData(getApplicationContext());
		
		setLrcView();
		lrcHandler.post(lrcRunnable);
		gd = new GestureDetector(this, this);
		smp = this;
		
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				seekbar.setProgress(seekbar.getProgress());
				Intent intent = new Intent("com.xmusicplayer.seekbar");
				intent.putExtra("seekbarPosition", seekbar.getProgress());
				Log.i("myservice", "seekbarPosition to intent:"+seekbar.getProgress());
				sendBroadcast(intent);	
			}});
		
	}
	
	
	private void setLrcView(){
		mlrc = new Lrc();
		mlrc.readLRC(MusicServer.musiclist.get(MusicServer.service_id).getUrl());	//解析歌词
		LrcContentList = mlrc.getLrcList();	//读取解析后的歌词
		lrctextview.setLrcList(LrcContentList);	// LrcContentList 导入lrctextview
		lrctextview.setAnimation(AnimationUtils.loadAnimation(SimpleMusicPlayer.this, R.anim.alpha_z));
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		songId = getIntent().getIntExtra("id", 1);
		
		reciever = new seekbarBroadcastReciever();
		//IntentFilter filter = new IntentFilter("com.xmusicplayer.progress");
		IntentFilter filter = new IntentFilter(Constent.BROCAST_TO_PLAYER);
		this.registerReceiver(reciever, filter);
		setTitle();
	}

	/**
	 * 接收广播，更新进度条。广播来源于service。
	 */
	public class seekbarBroadcastReciever extends BroadcastReceiver{	

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int progress = intent.getIntExtra("progress", 1);
			int total = intent.getIntExtra("total", 1);
			seekbar.setProgress(100*progress/total);
			seekbar.invalidate();//刷新view
		}
		
	}
	
	private class btnlistener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			if (v == btn_pre){
				songId = MusicServer.service_id;
				songId--;
				if (songId < 0) songId = 0;
				
				Intent intent = new Intent("com.xmusicplayer.cmd");
				intent.putExtra("cmd", "pre");
				intent.putExtra("id", songId);
				sendBroadcast(intent);
				setTitle();
			}
			else if(v == btn_pause_or_play ){
				String s = (String) btn_pause_or_play.getText();
				if ( s.equals("暂停") ){
					Intent intent = new Intent("com.xmusicplayer.cmd");
					intent.putExtra("cmd", "pause");
					intent.putExtra("id", songId);
					sendBroadcast(intent);
					btn_pause_or_play.setText("播放");
				}else if(s.equals("播放")) {
					Intent intent = new Intent("com.xmusicplayer.cmd");
					intent.putExtra("cmd", "replay");
					intent.putExtra("id", songId);
					sendBroadcast(intent);
					btn_pause_or_play.setText("暂停");
				}
				
			}
			else if(v == btn_loop ){
				if (isloop == false){
					isloop = true;
					btn_loop.setText("单曲循环");
				}else {
					isloop = false;
					btn_loop.setText("顺序播放");
				}
			}
			else if(v == btn_next ){
				songId = MusicServer.service_id;
				songId = songId + 1;
				if (songId > (musiclist.size()-1)) songId = musiclist.size()-1;
				
				Intent intent = new Intent("com.xmusicplayer.cmd");
				intent.putExtra("cmd", "next");
				intent.putExtra("id", songId);
				sendBroadcast(intent);
				setTitle();
			}
			
		}
		
	} 
	
	@Override
	protected void onDestroy() {
		this.unregisterReceiver(reciever);
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isInfront = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		isInfront = false;
	}

	
	public void setTitle(){
		Music m = musiclist.get(songId);
		singer_song.setText(m.getSinger()+ "  "+ m.getTitle());
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i("myservice","SimpleMusicPlayer onTouchEvent");
		return gd.onTouchEvent(event);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	/**
	 * 左/右滑动回到list页面
	 */
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if ((e1.getX()- e2.getX()) > FLING_MIN_DISTANCE){
			Intent intent = new Intent(SimpleMusicPlayer.this, SimpleMusicList.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
		}else if ((e2.getX() - e1.getX()) > FLING_MIN_DISTANCE){
			Intent intent = new Intent(SimpleMusicPlayer.this, SimpleMusicList.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}


	public static Boolean getIsloop() {
		return isloop;
	}


	public void setIsloop(Boolean isloop) {
		this.isloop = isloop;
	}
	
}
