package com.jiahehongye.robred.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jiahehongye.robred.Constant;
import com.jiahehongye.robred.R;
import com.jiahehongye.robred.activity.ContanctDetailActivity;
import com.jiahehongye.robred.activity.RelationActivity;
import com.jiahehongye.robred.bean.AttentionListResult;
import com.jiahehongye.robred.bean.AttentionResult;
import com.jiahehongye.robred.cook.CookieJarImpl;
import com.jiahehongye.robred.cook.PersistentCookieStore;
import com.jiahehongye.robred.interfaces.MyItemClickListener;
import com.jiahehongye.robred.utils.LogUtil;
import com.jiahehongye.robred.utils.SPUtils;
import com.jiahehongye.robred.utils.UIUtils;
import com.jiahehongye.robred.view.GlideCircleTransform;

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
 * Created by huangjunhui on 2017/3/29.13:55
 */
public class RelationFansRecycleViewAdpatre extends BaseRecycleViewAdapter  {
    private RelationActivity mActivity;
    private ArrayList<AttentionListResult.DataBean.ListBean> fatherArrayList;
    private View footView;
    private final String id;
    public int SCROLL_STATE_CURRENT = Constant.SCROLL_STATE_IN;


    public RelationFansRecycleViewAdpatre(RelationActivity mActivity, ArrayList<AttentionListResult.DataBean.ListBean> fatherArrayList) {
        this.mActivity = mActivity;
        this.fatherArrayList = fatherArrayList;
        id = (String) SPUtils.get(UIUtils.getContext(), Constant.LOGIN_ID, "");


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case ITEM_TYPE_CONTENT:
                View contentView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_relation_frans_content, null);
                return new RelationContentRecycleViewHolder(contentView, mItemClickListener);
            case ITEM_TYPE_FOOT:
                footView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_home_foot, null);
                hintFootView();
                return new RelationFootRecycleViewHolder(footView);
            case ITEM_TYPE_EMPTY:
                View ddView = LayoutInflater.from(mActivity).inflate(R.layout.application_empty, null);
                return new HomeTabChildEmptyViewViewHolder(ddView);
        }

        return null;
    }

    private class HomeTabChildEmptyViewViewHolder extends RecyclerView.ViewHolder {
        public HomeTabChildEmptyViewViewHolder(View footView) {
            super(footView);
        }
    }

    public void showFootView() {
        if (footView != null) {
            footView.setVisibility(View.VISIBLE);
        }
    }

    public void hintFootView() {
        if (footView != null) {
            footView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof RelationContentRecycleViewHolder) {
            final RelationContentRecycleViewHolder contentHolder = (RelationContentRecycleViewHolder) holder;
            contentHolder.mTvName.setText(fatherArrayList.get(position).getNickName());
            Glide.with(UIUtils.getContext()).load(fatherArrayList.get(position).getUserPhoto()).transform(new GlideCircleTransform(UIUtils.getContext())).placeholder(R.mipmap.avatar)
                    .into(contentHolder.mIvAvatar);

            contentHolder.mIvAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ContanctDetailActivity.class);
                    intent.putExtra("mobile",fatherArrayList.get(position).getMobile());
                    mActivity.startActivity(intent);
                }
            });

//            contentHolder.mIvAvatar.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(mActivity, PhotoviewActivity.class);
//                    Bundle bundle = new Bundle();
//                    List<String> thumb = new ArrayList<>();
//                    thumb.add(fatherArrayList.get(position).getTx());
//                    bundle.putSerializable("infoList", (Serializable) thumb);
//                    //intent.putExtra(PHOTO_POSITION,3);
//                    intent.putExtras(bundle);
//                    mActivity.startActivity(intent);
//                }
//            });

//            String wenzhang;
//            String fensi;
//            if (TextUtils.isEmpty(fatherArrayList.get(position).getWenzhang())) {
//                wenzhang = "0";
//            } else {
//                wenzhang = fatherArrayList.get(position).getWenzhang();
//            }
//
//            if (TextUtils.isEmpty(fatherArrayList.get(position).getFans())) {
//                fensi = "0";
//            } else {
//                fensi = fatherArrayList.get(position).getFans();
//            }

            contentHolder.mTvContent.setText(fatherArrayList.get(position).getFansNumber()+"粉丝");

            String isgz = fatherArrayList.get(position).getFansCount();



            if (isgz.equals("0")) {
                contentHolder.mTvAttenion.setText("关注");
                contentHolder.mTvAttenion.setTextColor(Color.parseColor("#B2AFAA"));
                contentHolder.mTvAttenion.setBackgroundResource(R.drawable.guanzhu);
            } else {
                contentHolder.mTvAttenion.setText("已关注");
                contentHolder.mTvAttenion.setTextColor(UIUtils.getContext().getResources().getColor(R.color.red));
                contentHolder.mTvAttenion.setBackgroundResource(R.drawable.yiguanzhu);
            }


            contentHolder.mTvAttenion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.showMyDialog();
                    String string = contentHolder.mTvAttenion.getText().toString();
                    if (string.equals("关注")) {
                        contentHolder.mTvAttenion.setText("已关注");
                        contentHolder.mTvAttenion.setTextColor(UIUtils.getContext().getResources().getColor(R.color.red));
                        contentHolder.mTvAttenion.setBackgroundResource(R.drawable.yiguanzhu);
                        requestAttention(contentHolder.mTvAttenion, fatherArrayList.get(position).getMemId()); //ATTENTION_URL
                    } else {
                        contentHolder.mTvAttenion.setText("关注");
                        contentHolder.mTvAttenion.setTextColor(Color.parseColor("#B2AFAA"));
                        contentHolder.mTvAttenion.setBackgroundResource(R.drawable.guanzhu);
                        requestAttention(contentHolder.mTvAttenion, fatherArrayList.get(position).getMemId());//ATTENTION_URL

                    }
                }
            });

        }
    }
    private void requestAttention(final TextView mTvAttenion, String memId) {//ATTENTION_URL

        LogUtil.LogShitou("联系人id:",id);

        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new CookieJarImpl(new PersistentCookieStore(mActivity)))
                .build();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("coverMemId", memId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(Constant.JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(Constant.ATTENTION_URL)
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mActivity.dismissMyDialog();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
//                LogUtil.LogShitou("请求关注接口返回数据：",result.toString());
//                Message msg = handler.obtainMessage();
//                msg.what = REQUEST_ATTENTION;
//                msg.obj = result;
//                handler.sendMessage(msg);

                mActivity.dismissMyDialog();

                AttentionResult attentionResult = new Gson().fromJson(result, AttentionResult.class);
                if (attentionResult.getResult().equals("success")) {
                    final String errorMsg = attentionResult.getData().getMsg();
                    final int fansCount = attentionResult.getData().getFansCount();


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity, errorMsg, Toast.LENGTH_SHORT).show();
                            if(fansCount==1){
                                mTvAttenion.setText("已关注");
                                mTvAttenion.setTextColor(UIUtils.getContext().getResources().getColor(R.color.red));
                                mTvAttenion.setBackgroundResource(R.drawable.yiguanzhu);
                            }else {
                                mTvAttenion.setText("关注");
                                mTvAttenion.setTextColor(Color.parseColor("#B2AFAA"));
                                mTvAttenion.setBackgroundResource(R.drawable.guanzhu);

                            }
                        }
                    });
                } else if (attentionResult.getResult().equals("fail")) {
//                    String errorMsg = attentionResult.getData().getErrorMsg();
//                    Toast.makeText(mActivity, "", Toast.LENGTH_SHORT).show();
                } else {

                }

            }
        });


    }
    @Override
    public int getItemViewType(int position) {
        if (fatherArrayList.size() == 0) {
            return ITEM_TYPE_EMPTY;
        }
        if (fatherArrayList.size() < 1) {
            return ITEM_TYPE_FOOT;
        }
        if (position == getItemCount() - 1) {
            return ITEM_TYPE_FOOT;
        }
        return ITEM_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return fatherArrayList.size() + 1;
    }



    private class RelationContentRecycleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MyItemClickListener mItemClickListener;
        private TextView mTvAttenion;
        private TextView mTvContent;
        private TextView mTvName;
        private ImageView mIvAvatar;

        public RelationContentRecycleViewHolder(View contentView, MyItemClickListener mItemClickListener) {
            super(contentView);
            this.mItemClickListener = mItemClickListener;
            contentView.setOnClickListener(this);
            mTvAttenion = (TextView) contentView.findViewById(R.id.relation_attenion);
            mTvContent = (TextView) contentView.findViewById(R.id.relation_content);
            mTvName = (TextView) contentView.findViewById(R.id.relation_name);
            mIvAvatar = (ImageView) contentView.findViewById(R.id.relation_avatar);

        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    private class RelationFootRecycleViewHolder extends RecyclerView.ViewHolder {


        public RelationFootRecycleViewHolder(View footView) {
            super(footView);

        }
    }
}
