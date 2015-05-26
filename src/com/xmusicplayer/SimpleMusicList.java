package com.xmusicplayer;

import java.util.List;

import com.adapter.MusicListAdapter;
import com.domain.Music;
import com.util.Constent;
import com.util.GetMusicList;

import android.R.integer;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleMusicList extends Activity implements OnGestureListener {
	
	private ListView lv;
	private List<Music> musiclist;
	private GestureDetector gd;
	private int FLING_MIN = 100;
	private TextView playingMusicName;
	private listBroadcastReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.simple_music_list);
		
		lv = (ListView) findViewById(R.id.simple_music_list_listview);
		playingMusicName = (TextView) findViewById(R.id.simple_music_list_textview_playingmusicname);
		gd = new GestureDetector(this, this);
		
		musiclist = GetMusicList.GetMusicListData(getApplicationContext());
		MusicListAdapter adapter = new MusicListAdapter(musiclist, getApplicationContext());
		lv.setAdapter(adapter);
		
		receiver = new listBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(Constent.BROCAST_TO_LIST);
		registerReceiver(receiver, intentFilter);
		
		lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Intent intent2 = new Intent("com.xmusicplayer.cmd");
				intent2.putExtra("cmd", "play");
				intent2.putExtra("id", arg2);
				sendBroadcast(intent2);
				
				Intent intent = new Intent(SimpleMusicList.this, SimpleMusicPlayer.class);
				intent.putExtra("id", arg2);
				startActivity(intent);
			}});
		
		playingMusicName.setOnClickListener(new OnClickListener (){

			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),SimpleMusicPlayer.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}});
		
		
		Intent intent = new Intent(SimpleMusicList.this, MusicServer.class);
		startService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "没用的菜单项");
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "没用的菜单项");
		menu.add(Menu.NONE, Menu.FIRST + 3, 3, "没用的菜单项");
		menu.add(Menu.NONE, Menu.FIRST + 4, 4, "没用的菜单项");
		menu.add(Menu.NONE, Menu.FIRST + 5, 5, "没用的菜单项");
		menu.add(Menu.NONE, Menu.FIRST + 6, 6, "退出");
		//return super.onCreateOptionsMenu(menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		
		case Menu.FIRST+1: Toast.makeText(this, "摆设用的菜单", Toast.LENGTH_SHORT).show();break;
		case Menu.FIRST+2: Toast.makeText(this, "摆设用的菜单", Toast.LENGTH_SHORT).show();break;
		case Menu.FIRST+3: Toast.makeText(this, "摆设用的菜单", Toast.LENGTH_SHORT).show();break;
		case Menu.FIRST+4: Toast.makeText(this, "摆设用的菜单", Toast.LENGTH_SHORT).show();break;
		case Menu.FIRST+5: Toast.makeText(this, "摆设用的菜单", Toast.LENGTH_SHORT).show();break;
		case Menu.FIRST+6: 
			//SimpleMusicPlayer.smp.finish();
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Log.i("myservice",""+(e1.getX()- e2.getX()));
		if((e2.getX() - e1.getX()) > FLING_MIN || (e2.getX() - e1.getX()) < FLING_MIN){
			Intent intent = new Intent(this, SimpleMusicPlayer.class);
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
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i("myservice","SimpleMusicList onTouchEvent");
		return gd.onTouchEvent(event);
	}
	
	/**
	 * 在service播放新的一首歌时，接收其发来的广播，
	 * 设置这个页面下的一个textview内容为播放的歌曲名
	 */
	public class listBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String cmd = intent.getStringExtra("cmd");
			if (cmd.equals("playnewsong")){
				Music m = new Music();
				m = MusicServer.musiclist.get(MusicServer.service_id);
				playingMusicName.setText("正在播放："+m.getName().substring(0,m.getName().length()-4));
			}
			
		}
		
	}
	
	@Override
	protected void onDestroy() {
		this.unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		this.gd.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);//让父类接收手势，而不是自定义的listview
	}
}
