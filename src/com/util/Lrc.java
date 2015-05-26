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
 * ������ʣ�����һ��list<LrcContent>,LrcContent��������һ�и�ʵ� ���ݼ���ʱ��
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
			while ((s = br.readLine()) != null) {	//������ƣ�[00:23.75]��������Я���ļ��±�\n[00:27.71]д������¶��ǹ�����
				
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

			stringBuilder.append("û�и���ļ�");
		} catch (IOException e) {
			e.printStackTrace();
			stringBuilder.append("��ȡ��ʴ���");
		}
		return stringBuilder.toString();
	}

	/**
	 *����ʱ���ַ���ȡ�ø�ʵ�ʱ�� 
	 */
	public int TimeStr(String time) {
		time = time.replace(":", ".");	//ʱ��ĸ�ʽ��00:23.75
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
