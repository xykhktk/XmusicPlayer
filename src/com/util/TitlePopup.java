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
 *	���ⰴť�ϵĵ����˵�
 */
public class TitlePopup extends PopupWindow {
	private Context mContext;

	protected final int LIST_PADDING = 10;//�б����ļ��
	private Rect mRect = new Rect();//һ������
	private final int[] mLocation = new int[2];//�����λ�ã�x��y��
	private int mScreenWidth,mScreenHeight;//��Ļ�Ŀ�Ⱥ͸߶�
	private boolean mIsDirty;//�ж��Ƿ���Ҫ��ӻ�����б�������
	private int popupGravity = Gravity.NO_GRAVITY;	//λ�ò�������
	private OnItemOnClickListener mItemOnClickListener;//����������ѡ��ʱ�ļ���
	private ListView mListView;	//�б����
	private ArrayList<ActionItem> mActionItems = new ArrayList<ActionItem>();	//���嵯���������б�		
	
	/**
	 * ���ֵĲ���
	 */
	public TitlePopup(Context context){
		
		this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}
	
	public TitlePopup(Context context, int width, int height){
		this.mContext = context;
		
		setFocusable(true);//��ý���
		setTouchable(true);	//���õ����ڿɵ��
		setOutsideTouchable(true);//���õ�����ɵ��
		mScreenWidth = PxUtil.getScreenWidth(mContext);//�����Ļ�Ŀ�Ⱥ͸߶�
		mScreenHeight = PxUtil.getScreenHeight(mContext);
		setWidth(width);//���õ����Ŀ�Ⱥ͸߶�
		setHeight(height);
		//setBackgroundDrawable(new BitmapDrawable());
		setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.pop_item_normal));
		setContentView(LayoutInflater.from(mContext).inflate(R.layout.list_pop_menu, null));//���õ����Ĳ��ֽ���
		
		initUI();
	}
		
	/**
	 * ��ʼ�������б�
	 */
	private void initUI(){
		mListView = (ListView) getContentView().findViewById(R.id.list_pop_menu_listview);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,long arg3) {
				//���������󣬵�����ʧ
				dismiss();
				
				if(mItemOnClickListener != null)
					mItemOnClickListener.onItemClick(mActionItems.get(index), index);
			}
		}); 
		mListView.setScrollingCacheEnabled(false);
	}
	
	/**
	 * ��ʾ�����б����
	 */
	public void show(View view){
		
		
		view.getLocationOnScreen(mLocation);//��õ����Ļ��λ������
		//Log.i("myservice", "mLocation 1 2��" + mLocation[0] +","+ mLocation[1]);
		mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(),mLocation[1] + view.getHeight());//���þ��εĴ�С
		//Log.i("myservice", "mLocation[0] + view.getWidth()��" + (mLocation[0] + view.getWidth()));
		//Log.i("myservice", "mLocation[1] + view.getHeight()��" + (mLocation[1] + view.getHeight()));
		if(mIsDirty){//�ж��Ƿ���Ҫ��ӻ�����б�������
			populateActions();
		}
		
		showAsDropDown(view);
		//showAtLocation(view, popupGravity, mScreenWidth - LIST_PADDING - (getWidth()/2), mRect.bottom);//��ʾ������λ��
	}
	
	/**
	 * ���õ����б�����
	 */
	private void populateActions(){
		mIsDirty = false;
		
		//�����б��������
		mListView.setAdapter(new BaseAdapter() {			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView textView = null;
				
				if(convertView == null){
					textView = new TextView(mContext);
					textView.setTextColor(mContext.getResources().getColor(android.R.color.white));
					textView.setTextSize(14);
					textView.setGravity(Gravity.CENTER);//�ı�����
					textView.setPadding(0, 10, 0, 15);//�����ı���ķ�Χ
					textView.setSingleLine(true);
					//textView.setHeight(80);
					//textView.setHeight(LayoutParams.WRAP_CONTENT);
					//textView.setWidth(LayoutParams.WRAP_CONTENT);
					
				}else{
					textView = (TextView) convertView;
				}
				
				ActionItem item = mActionItems.get(position);
				
				
				textView.setText(item.mTitle);//�����ı�����
				//����������ͼ��ļ��
				//textView.setCompoundDrawablePadding(10);
				//���������ֵ���߷�һ��ͼ��
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
	 * ���������
	 */
	public void addAction(ActionItem action){
		if(action != null){
			mActionItems.add(action);
			mIsDirty = true;
		}
	}
	
	/**
	 * ���������
	 */
	public void cleanAction(){
		if(mActionItems.isEmpty()){
			mActionItems.clear();
			mIsDirty = true;
		}
	}
	
	/**
	 * ����λ�õõ�������
	 */
	public ActionItem getAction(int position){
		if(position < 0 || position > mActionItems.size())
			return null;
		return mActionItems.get(position);
	}			
	
	/**
	 * ���ü����¼�
	 */
	public void setItemOnClickListener(OnItemOnClickListener onItemOnClickListener){
		this.mItemOnClickListener = onItemOnClickListener;
	}
	
	/**
	 *	���������ť�����¼�
	 */
	public static interface OnItemOnClickListener{
		public void onItemClick(ActionItem item , int position);
	}
	
	
}
