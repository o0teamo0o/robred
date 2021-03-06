package com.jiahehongye.robred.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jiahehongye.robred.Constant;
import com.jiahehongye.robred.R;
import com.jiahehongye.robred.activity.ReSendRedActivity;
import com.jiahehongye.robred.activity.RedDetailActivity;
import com.jiahehongye.robred.bean.FaSong;
import com.jiahehongye.robred.cook.CookieJarImpl;
import com.jiahehongye.robred.cook.PersistentCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by qianduan on 2016/12/29.
 */
public class SendRedFrag extends Fragment {
    private View view;
    private Call call;
    private PersistentCookieStore persistentCookieStore;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private TextView tv_return_midif;
    private Adapter adapter;
    private TextView middle_num;
    private ArrayList<FaSong> fsAll = new ArrayList<>();
    private int PAGENUMBER = 1;
    private String NUMBERS = "20";
    private static final int GET_ALL = 0000;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_ALL:
                    String ss = (String) msg.obj;
                    try {
                        JSONObject object = new JSONObject(ss);
                        if (object.getString("result").equals("success")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            String sendRedEnveAmount = data.getString("sendRedEnveAmount");
                            middle_num.setText("¥" + sendRedEnveAmount);
                            String sendRendEnveList = data.getString("sendRendEnveList");
                            ArrayList<FaSong> fs = (ArrayList<FaSong>) JSON.parseArray(sendRendEnveList, FaSong.class);
                            fsAll.addAll(fs);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.rob_red_frag, container, false);
        middle_num = (TextView) view.findViewById(R.id.middle_num);
        mListView = (ListView) view.findViewById(R.id.myred_lv);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.myred_swiperefreshlayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                PAGENUMBER = 1;
                fsAll.clear();
                getdata();
            }
        });

        //解决listview和swiperefreshlayout滑动冲突
//        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//                boolean enable = false;
//                if (mListView != null && mListView.getChildCount() > 0) {
//                    // check if the first item of the list is visible
//                    boolean firstItemVisible = mListView.getFirstVisiblePosition() == 0;
//                    // check if the top of the first item is visible
//                    boolean topOfFirstItemVisible = mListView.getChildAt(0).getTop() == 0;
//                    // enabling or disabling the refresh layout
//                    enable = firstItemVisible && topOfFirstItemVisible;
//                }
//                mSwipeRefreshLayout.setEnabled(enable);
//            }
//        });

        adapter = new Adapter();
        mListView.setAdapter(adapter);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        //加载更多功能的代码
                        PAGENUMBER = PAGENUMBER + 1;
                        getdata();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getdata();
    }

    private void getdata() {


        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        persistentCookieStore = new PersistentCookieStore(getActivity());
        CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
        builder.cookieJar(cookieJarImpl);
        OkHttpClient client = builder.build();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("pageNumber", PAGENUMBER + "");
            jsonObject.put("pageSize", NUMBERS);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(Constant.JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(Constant.SENDLISTURL)
                .post(body)
                .build();


        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();

                Message msg = handler.obtainMessage();
                msg.what = GET_ALL;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });

    }

    private class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return fsAll.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_account, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.red_name);
                holder.time = (TextView) convertView.findViewById(R.id.red_time);
                holder.price = (TextView) convertView.findViewById(R.id.red_price);
                holder.sendPrice = (TextView) convertView.findViewById(R.id.red_send_price);
                holder.status = (TextView) convertView.findViewById(R.id.red_status);
                holder.send_layout = (LinearLayout) convertView.findViewById(R.id.send_layout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.send_layout.setVisibility(View.VISIBLE);
            holder.price.setVisibility(View.GONE);
            holder.name.setText(fsAll.get(position).getNickName());
            holder.time.setText(fsAll.get(position).getCreateDate());
            holder.sendPrice.setText(fsAll.get(position).getRedEnveMoney());
            if (fsAll.get(position).getAuditStatus().equals("1")) {
                holder.status.setText("待支付");
            }
            if (fsAll.get(position).getAuditStatus().equals("2")) {
                holder.status.setText("审核中");
                holder.status.setTextColor(Color.parseColor("#1dcb2d"));
            }
            if (fsAll.get(position).getAuditStatus().equals("3")) {
                holder.status.setText("已发放");
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    intent.putExtra("redEnveMark","3");
//                    intent.putExtra("type","0");



                       /* Intent intent = new Intent(getActivity(), MySendRedDetailActivity.class);
                        intent.putExtra("redEnveCode", fsAll.get(position).getRedEnveCode());
                        startActivity(intent);*/
                        String type = "0";
                        // TODO: 2017/6/2 修改为红包详情页
                        if (fsAll.get(position).isRedEnveType()==false){
                            type = "0";
                        }else {
                            type = "1";
                        }
                        Intent intent = new Intent(getActivity(), RedDetailActivity.class);
                        intent.putExtra("redEnveCode",fsAll.get(position).getRedEnveCode());
                        intent.putExtra("redEnveMark","2");
                        intent.putExtra("type",type);
                        startActivity(intent);
                    }
                });
            }
            if (fsAll.get(position).getAuditStatus().equals("4")) {
                holder.status.setText("驳回");
                holder.status.setTextColor(Color.parseColor("#ff1943"));
                final ViewHolder finalHolder = holder;
                holder.status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //这里可以多传一个参数，就是条目值。
                        showPopupWindow(finalHolder.status, fsAll.get(position));
                        //获取到当前被点击的条目的所有内容，用来传递到下个一个界面去。
                    }
                });
            }


            return convertView;
        }

        class ViewHolder {
            TextView name, time, price, sendPrice, status;
            LinearLayout send_layout;
        }
    }

    public void showPopupWindow(View view, final FaSong data) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.bohuistate, null);
        tv_return_midif = (TextView) contentView.findViewById(R.id.tv_return_midif);
        TextView reason = (TextView) contentView.findViewById(R.id.textView1);
        reason.setText(data.getRejectReason());
        final PopupWindow popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // 让Popupwindow获取焦点
        popupWindow.setFocusable(true);
        // 给Popupwindow设置背景，为了点击其他地方时，Popupwindow自动消失
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 显示Popupwindow,让Popupwindow的左上点挨着号码框的左下点
        popupWindow.showAsDropDown(view, -90, -5);

        tv_return_midif.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//					//要跳转的页面,
                Intent intent = new Intent(getActivity(), ReSendRedActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bean", data);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

}
