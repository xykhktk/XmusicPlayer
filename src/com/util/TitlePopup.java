package com.util;

import java.util.ArrayList;

import com.domain.ActionItem;
import com.xmusicplayer.R;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 *	标题按钮上的弹窗菜单
 */
public class TitlePopup extends PopupWindow {
	private Context mContext;

	protected final int LIST_PADDING = 10;//列表弹窗的间隔
	private Rect mRect = new Rect();//一个矩形
	private final int[] mLocation = new int[2];//坐标的位置（x、y）
	private int mScreenWidth,mScreenHeight;//屏幕的宽度和高度
	private boolean mIsDirty;//判断是否需要添加或更新列表子类项
	private int popupGravity = Gravity.NO_GRAVITY;	//位置不在中心
	private OnItemOnClickListener mItemOnClickListener;//弹窗子类项选中时的监听
	private ListView mListView;	//列表对象
	private ArrayList<ActionItem> mActionItems = new ArrayList<ActionItem>();	//定义弹窗子类项列表		
	
	/**
	 * 布局的参数
	 */
	public TitlePopup(Context context){
		
		this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}
	
	public TitlePopup(Context context, int width, int height){
		this.mContext = context;
		
		setFocusable(true);//获得焦点
		setTouchable(true);	//设置弹窗内可点击
		setOutsideTouchable(true);//设置弹窗外可点击
		mScreenWidth = PxUtil.getScreenWidth(mContext);//获得屏幕的宽度和高度
		mScreenHeight = PxUtil.getScreenHeight(mContext);
		setWidth(width);//设置弹窗的宽度和高度
		setHeight(height);
		//setBackgroundDrawable(new BitmapDrawable());
		setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.pop_item_normal));
		setContentView(LayoutInflater.from(mContext).inflate(R.layout.list_pop_menu, null));//设置弹窗的布局界面
		
		initUI();
	}
		
	/**
	 * 初始化弹窗列表
	 */
	private void initUI(){
		mListView = (ListView) getContentView().findViewById(R.id.list_pop_menu_listview);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,long arg3) {
				//点击子类项后，弹窗消失
				dismiss();
				
				if(mItemOnClickListener != null)
					mItemOnClickListener.onItemClick(mActionItems.get(index), index);
			}
		}); 
		mListView.setScrollingCacheEnabled(false);
	}
	
	/**
	 * 显示弹窗列表界面
	 */
	public void show(View view){
		
		
		view.getLocationOnScreen(mLocation);//获得点击屏幕的位置坐标
		//Log.i("myservice", "mLocation 1 2：" + mLocation[0] +","+ mLocation[1]);
		mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(),mLocation[1] + view.getHeight());//设置矩形的大小
		//Log.i("myservice", "mLocation[0] + view.getWidth()：" + (mLocation[0] + view.getWidth()));
		//Log.i("myservice", "mLocation[1] + view.getHeight()：" + (mLocation[1] + view.getHeight()));
		if(mIsDirty){//判断是否需要添加或更新列表子类项
			populateActions();
		}
		
		showAsDropDown(view);
		//showAtLocation(view, popupGravity, mScreenWidth - LIST_PADDING - (getWidth()/2), mRect.bottom);//显示弹窗的位置
	}
	
	/**
	 * 设置弹窗列表子项
	 */
	private void populateActions(){
		mIsDirty = false;
		
		//设置列表的适配器
		mListView.setAdapter(new BaseAdapter() {			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView textView = null;
				
				if(convertView == null){
					textView = new TextView(mContext);
					textView.setTextColor(mContext.getResources().getColor(android.R.color.white));
					textView.setTextSize(14);
					textView.setGravity(Gravity.CENTER);//文本居中
					textView.setPadding(0, 10, 0, 15);//设置文本域的范围
					textView.setSingleLine(true);
					//textView.setHeight(80);
					//textView.setHeight(LayoutParams.WRAP_CONTENT);
					//textView.setWidth(LayoutParams.WRAP_CONTENT);
					
				}else{
					textView = (TextView) convertView;
				}
				
				ActionItem item = mActionItems.get(position);
				
				
				textView.setText(item.mTitle);//设置文本文字
				//设置文字与图标的间隔
				//textView.setCompoundDrawablePadding(10);
				//设置在文字的左边放一个图标
				//textView.setCompoundDrawablesWithIntrinsicBounds(item.mDrawable, null , null, null);
				
                return textView;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return mActionItems.get(position);
			}
			
			@Override
			public int getCount() {
				return mActionItems.size();
			}
		}) ;
	}
	
	/**
	 * 添加子类项
	 */
	public void addAction(ActionItem action){
		if(action != null){
			mActionItems.add(action);
			mIsDirty = true;
		}
	}
	
	/**
	 * 清除子类项
	 */
	public void cleanAction(){
		if(mActionItems.isEmpty()){
			mActionItems.clear();
			mIsDirty = true;
		}
	}
	
	/**
	 * 根据位置得到子类项
	 */
	public ActionItem getAction(int position){
		if(position < 0 || position > mActionItems.size())
			return null;
		return mActionItems.get(position);
	}			
	
	/**
	 * 设置监听事件
	 */
	public void setItemOnClickListener(OnItemOnClickListener onItemOnClickListener){
		this.mItemOnClickListener = onItemOnClickListener;
	}
	
	/**
	 *	弹窗子类项按钮监听事件
	 */
	public static interface OnItemOnClickListener{
		public void onItemClick(ActionItem item , int position);
	}
	
	
}
