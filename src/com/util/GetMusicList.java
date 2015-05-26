package com.util;

import java.util.ArrayList;
import java.util.List;

import com.domain.Music;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.widget.ListView;

public class GetMusicList {

	
	public  static List<Music>  GetMusicListData(Context context) {
	
		List<Music> music_list = new ArrayList<Music>();
	
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
			null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
	
		cursor.moveToFirst();
		for (int i = 0;i<cursor.getCount();i++)
		{
			Music m = new Music();
			
			String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
			String is_mp3 = name.substring(name.length() - 3,name.length());	//判断最后3位是不是mp3
			
			if (is_mp3.equals("mp3"))
			{
				String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
				String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
				String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
				String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
				long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));	//大小
				long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
			
				m.setName(name);
				m.setTitle(title);
				m.setSinger(singer);
				m.setAlbum(album);
				m.setUrl(url);
				m.setSize(size);
				m.setTime(time);
			
				music_list.add(m);
			}
			cursor.moveToNext();
		}  
		cursor.close();
		return music_list;
	}
}
