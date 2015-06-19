package com.util;

import java.util.ArrayList;
import java.util.List;

import com.util.Lrc.LrcContent;

import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class LrcView extends TextView {

	private Paint currentPaint;
	private Paint notcurrentPaint;
	private int index;
	private List<Lrc.LrcContent> lrcList = new ArrayList<Lrc.LrcContent>();
	private float high;
	private float width;
	private float texthigh = 40;
	
	public void setIndex(int index) {
		this.index = index;
	}

	public List<Lrc.LrcContent> getLrcList() {
		return lrcList;
	}

	public void setLrcList(List<Lrc.LrcContent> lrcList) {
		this.lrcList = lrcList;
	}

	public LrcView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LrcView(Context context) {
		super(context);
		init();
	}

	public void init(){
		currentPaint = new Paint();
		currentPaint.setAntiAlias(true);
		currentPaint.setTextAlign(Paint.Align.CENTER);
		notcurrentPaint = new Paint();
		notcurrentPaint.setAntiAlias(true);
		notcurrentPaint.setTextAlign(Paint.Align.CENTER);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.high = h;
		this.width = w;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (null == canvas)
			return;
		
		currentPaint.setColor(Color.argb(200, 255, 255, 255));
		notcurrentPaint.setColor(Color.argb(100, 255, 255, 255));
		currentPaint.setTextSize((int)texthigh*4/5);
		notcurrentPaint.setTextSize((int)texthigh*3/5);
		
		setText("");
		try {
			canvas.drawText(lrcList.get(index).getLrc_text(), width/2, high/2, currentPaint);
			
			float tempy = high/2;
			for (int i = index - 1; i > 0;i--){
				tempy = tempy - texthigh;
				canvas.drawText(lrcList.get(i).getLrc_text(), width/2, tempy, notcurrentPaint);
			}
			
			tempy = high/2;
			for (int i = index + 1; i < lrcList.size();i++){
				tempy = tempy + texthigh;
				canvas.drawText(lrcList.get(i).getLrc_text(), width/2, tempy, notcurrentPaint);
			}
		} catch (Exception e) {
			setText("Ã»ÓÐ¸è´Ê");
			e.printStackTrace();
		}
		
	}
	
}
