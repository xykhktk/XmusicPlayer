package com.domain;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 *	������
 */
public class ActionItem {
	
	public CharSequence mTitle;//�ı�����
//	public Drawable mDrawable;
	
	public ActionItem(CharSequence title){
		this.mTitle = title;
	}
	
	public ActionItem(Context context, int titleId){
		this.mTitle = context.getResources().getText(titleId);
	}
	
	public ActionItem(Context context, CharSequence title) {
		this.mTitle = title;
	}
}
