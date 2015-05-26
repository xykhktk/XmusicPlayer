package com.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析歌词，返回一个list<LrcContent>,LrcContent的内容是一行歌词的 内容及其时间
 */
public class Lrc {

	private List<LrcContent> lrcList;
	private LrcContent tempContent;
	
	public Lrc() {
		lrcList =  new ArrayList<Lrc.LrcContent>();
		tempContent = new LrcContent();
	}

	public List<LrcContent> getLrcList() {
		return lrcList;
	}

	public String readLRC(String song_path) {

		StringBuilder stringBuilder = new StringBuilder();
		File f = new File(song_path.replace(".mp3", ".lrc"));
		
		try {
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis, "GB2312");
			BufferedReader br = new BufferedReader(isr);
			String s = "";
			while ((s = br.readLine()) != null) {	//歌词类似：[00:23.75]翻开随身携带的记事本\n[00:27.71]写着许多事都是关于你
				
				s = s.replace("[", "");
				s = s.replace("]", "@");

				String splitLrc_data[] = s.split("@");
				if (splitLrc_data.length > 1) {
					tempContent.setLrc_text(splitLrc_data[1]);

					int LrcTime = TimeStr(splitLrc_data[0]);
					tempContent.setLrc_time(LrcTime);
					lrcList.add(tempContent);
					tempContent = new LrcContent();
				}

			}
			br.close();
			isr.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();

			stringBuilder.append("没有歌词文件");
		} catch (IOException e) {
			e.printStackTrace();
			stringBuilder.append("读取歌词错误");
		}
		return stringBuilder.toString();
	}

	/**
	 *根据时间字符串取得歌词的时间 
	 */
	public int TimeStr(String time) {
		time = time.replace(":", ".");	//时间的格式：00:23.75
		time = time.replace(".", "@");
		String timeData[] = time.split("@");

		int minute = Integer.parseInt(timeData[0]);	
		int second = Integer.parseInt(timeData[1]);
		int millisecond = Integer.parseInt(timeData[2]);

		int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
		return currentTime;
	}
	
	

	public class LrcContent{
		
		private String lrc_text;
		private int lrc_time;
		
		public String getLrc_text() {
			return lrc_text;
		}
		public void setLrc_text(String lrc_text) {
			this.lrc_text = lrc_text;
		}
		public int getLrc_time() {
			return lrc_time;
		}
		public void setLrc_time(int lrc_time) {
			this.lrc_time = lrc_time;
		}
	}
	
}
