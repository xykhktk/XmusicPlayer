package com.adapter;

import java.util.List;

import com.domain.Music;
import com.xmusicplayer.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicListAdapter extends BaseAdapter {

	
	private List<Music> listmusic;
	private Context context;
	
	public MusicListAdapter(List<Music> music, Context context) {
		super();
		this.listmusic = music;
		this.context = context;
	}

	@Override
	public int getCount() {
		return listmusic.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listmusic.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null)	{
			convertView = LayoutInflater.from(context).inflate(R.layout.music_list_adapter, null);
		}
		
		Music m = listmusic.get(position);
		
		ImageView imageview = (ImageView) convertView.findViewById(R.id.music_list_adapter_imageview);
		imageview.setBackgroundResource(R.drawable.music);
		
		TextView name = (TextView) convertView.findViewById(R.id.music_list_adapter_textview_name);
		name.setText(m.getName().substring(0, m.getName().length()-4));
		
		TextView singer = (TextView) convertView.findViewById(R.id.music_list_adapter_textview_singer);
		singer.setText(m.getSinger());
		
		TextView time = (TextView) convertView.findViewById(R.id.music_list_adapter_textview_time);
		//Log.i("gettime",m.getTime()+"");
		String times = convertime((int)m.getTime());
		//Log.i("gettime",times);
		time.setText(times);
		
		return convertView;
	}
	
	
	public String convertime(int time)
	{
		int sec;
		int min;
		
		time /=1000;
		sec = time%60;
		min = time/60;
		return String.format("%02d:%02d", min,sec);
	}

}
