package com.jiahehongye.robred.adapter;

import java.util.List;

import com.jiahehongye.robred.R;
import com.jiahehongye.robred.bean.OneyuanMyRobListBean;
import com.jiahehongye.robred.bean.OneyuanMyRobListBean.ParticipationRecordList;
import com.jiahehongye.robred.oneyuan.OneyuanProductDetailActivity;
import com.jiahehongye.robred.oneyuan.WinActivity;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MyRobListAdapter extends BaseAdapter {
	
	private Context context;
	private List<ParticipationRecordList> list;
	private LayoutInflater inflater;
	private int mScreenWidth;
	private int mScreenHeight;
	
	public MyRobListAdapter(Context context, List<ParticipationRecordList> list,int mScreenWidth,int mScreenHeight) {
		super();
		this.context = context;
		this.list = list;
		this.mScreenWidth = mScreenWidth;
		this.mScreenHeight = mScreenHeight;

	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
			return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.from(context).inflate(R.layout.item_oneyuan_myroblist, null);
			holder = new ViewHolder();
			holder.createTime = (TextView) convertView.findViewById(R.id.tv_oneyuan_myroblist_time);
			holder.ing = (TextView) convertView.findViewById(R.id.tv_oneyuan_myroblist_ing);
			holder.ed = (TextView) convertView.findViewById(R.id.tv_oneyuan_myroblist_ed);
			holder.name = (TextView) convertView.findViewById(R.id.tv_oneyuan_myroblist_name);
			holder.nostage = (TextView) convertView.findViewById(R.id.tv_oneyuan_myroblist_number);
			holder.code = (TextView) convertView.findViewById(R.id.tv_oneyuan_myroblist_code);
			holder.jiang = (ImageView) convertView.findViewById(R.id.iv_oneyuan_myroblist_jiang);
			holder.image = (ImageView) convertView.findViewById(R.id.iv_oneyuan_myroblist_image);
			holder.wait = (TextView) convertView.findViewById(R.id.tv_oneyuan_myroblist_wait);
			holder.iv_oneyuan_myroblist_goto = (ImageView) convertView.findViewById(R.id.iv_oneyuan_myroblist_goto);
			convertView.setTag(R.id.tag_holder,holder);
		}else {
			holder = (ViewHolder) convertView.getTag(R.id.tag_holder);
		}
		
		holder.nostage.setText("["+list.get(position).getNoStage()+"]");
		holder.createTime.setText(list.get(position).getCreateDate());
		holder.name.setText(list.get(position).getName());
		Picasso.with(context).load(list.get(position).getImage()).into(holder.image);
		ParticipationRecordList bean  = list.get(position);
		//设置三种状态时右上角布局的显示与隐藏
		if(bean.getProductType().equals("0")){
			holder.wait.setVisibility(View.VISIBLE);
			holder.ing.setVisibility(View.GONE);
			holder.jiang.setVisibility(View.GONE);
			holder.ed.setVisibility(View.GONE);
			holder.code.setText("兑奖号："+bean.getSnList().get(0).getSn());
			holder.code.setTextColor(Color.parseColor("#249cff"));
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, OneyuanProductDetailActivity.class);
					String productId = list.get((Integer) v.getTag()).getProductId();
					intent.putExtra("productId", productId);
					context.startActivity(intent);
				}
			});
		}else if (bean.getProductType().equals("1")) {
			holder.wait.setVisibility(View.GONE);
			holder.ed.setVisibility(View.GONE);
			holder.jiang.setVisibility(View.GONE);
			holder.ing.setVisibility(View.VISIBLE);
			if (bean.getSnList().size()>0){

				holder.code.setText("兑奖号："+bean.getSnList().get(0).getSn());
			}
			holder.code.setTextColor(Color.parseColor("#249cff"));
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, OneyuanProductDetailActivity.class);
					String productId = list.get((Integer) v.getTag()).getProductId();
					intent.putExtra("productId", productId);
					context.startActivity(intent);
				}
			});
		}else if (bean.getProductType().equals("2")) {
			holder.ing.setVisibility(View.GONE);
			holder.wait.setVisibility(View.GONE);
			holder.ed.setVisibility(View.VISIBLE);
			holder.code.setText("兑奖号："+bean.getSnList().get(0).getSn());

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, OneyuanProductDetailActivity.class);
					String productId = list.get((Integer) v.getTag()).getProductId();
					intent.putExtra("productId", productId);
					context.startActivity(intent);
				}
			});
			for (int i = 0; i < bean.getSnList().size(); i++) {
				if (bean.getSnList().get(i).getType().equals("2")){
					holder.jiang.setVisibility(View.VISIBLE);
					holder.ed.setVisibility(View.GONE);
					holder.code.setText("兑奖号："+bean.getSnList().get(i).getSn());
					holder.code.setTextColor(context.getResources().getColor(R.color.home_state_color));
					convertView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.setClass(context, WinActivity.class);
							intent.putExtra("productId", list.get((Integer) v.getTag()).getProductId());
							context.startActivity(intent);
						}
					});

				}
			}
		}
		
		convertView.setTag(position);
//		if(list.get(position).getType().equals("2")){
//			convertView.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent();
//					intent.setClass(context, WinActivity.class);
//					intent.putExtra("productId", list.get((Integer) v.getTag()).getProductId());
//					context.startActivity(intent);
//				}
//			});
//		}else {
//			convertView.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(context, OneyuanProductDetailActivity.class);
//					String productId = list.get((Integer) v.getTag()).getProductId();
//					Log.e("传过去产品的ID：", productId);
//					intent.putExtra("productId", productId);
//					context.startActivity(intent);
//				}
//			});
//		}
		final ViewHolder finalHolder = holder;
		holder.code.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopupWindow(finalHolder.code,list.get(position).getSnList());
			}
		});
		return convertView;
	}
	
	static class ViewHolder{
		TextView wait;
		TextView createTime,ing,ed,name,nostage,code;
		ImageView jiang,image,iv_oneyuan_myroblist_goto;
	}


	public void showPopupWindow(View view, final List<OneyuanMyRobListBean.SnList> a) {
		// 一个自定义的布局，作为显示的内容
		final View contentView = LayoutInflater.from(context).inflate( R.layout.popstate, null);
		GridView duijiang_grid = (GridView) contentView.findViewById(R.id.duijiang_grid);
		NumAdapter numAdapter = new NumAdapter(a);
		duijiang_grid.setAdapter(numAdapter);
		final PopupWindow popupWindow = new PopupWindow(contentView, mScreenWidth-250, mScreenHeight/2);
		// 让Popupwindow获取焦点
		popupWindow.setFocusable(true);
		// 给Popupwindow设置背景，为了点击其他地方时，Popupwindow自动消失
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 显示Popupwindow,让Popupwindow的左上点挨着号码框的左下点
		popupWindow.showAtLocation(view, Gravity.CENTER,0,0);



	}
	class NumAdapter extends BaseAdapter{

		private List<OneyuanMyRobListBean.SnList> a;
		public NumAdapter(List<OneyuanMyRobListBean.SnList> a) {
			this.a= a;
		}

		@Override
		public int getCount() {
			return a.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view1, ViewGroup parent) {
			ViewHolder holder = null;
			if(view1 == null){
				view1 = inflater.from(context).inflate(R.layout.item_text, null);
				holder = new ViewHolder();
				holder.num = (TextView) view1.findViewById(R.id.djnum);
				view1.setTag(holder);
			}else {
				holder = (ViewHolder) view1.getTag();
			}
			holder.num.setText(a.get(position).getSn());
			if (a.get(position).getType().equals("2")){
				holder.num.setTextColor(Color.parseColor("#ff1943"));
			}else {
				holder.num.setTextColor(Color.parseColor("#000000"));
			}
			return view1;
		}
		class ViewHolder{
			TextView num;
		}
	}
}
