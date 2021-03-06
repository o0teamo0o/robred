package com.jiahehongye.robred.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.jiahehongye.robred.AppHelper;
import com.jiahehongye.robred.BaseActivity;
import com.jiahehongye.robred.Constant;
import com.jiahehongye.robred.R;
import com.jiahehongye.robred.application.BaseApplication;
import com.jiahehongye.robred.bean.LoginResult;
import com.jiahehongye.robred.cook.CookieJarImpl;
import com.jiahehongye.robred.cook.PersistentCookieStore;
import com.jiahehongye.robred.utils.DesUtil;
import com.jiahehongye.robred.utils.SPUtils;
import com.jiahehongye.robred.utils.ThreadManager;
import com.jiahehongye.robred.utils.UIUtils;
import com.jiahehongye.robred.view.MyProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by huangjunhui on 2017/1/5.13:06
 */
public class ThridBindActivity extends BaseActivity implements View.OnClickListener {

    private static final int LOGIN_SUCCESS = 200;
    private static final int REGISTER_ID_OK = 201;
    private static final int BIND_SUCCESS = 202;
    private EditText mEtPhone;
    private EditText mEtPassword;
    private MyProgressDialog animDialog;
    private Call call;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BIND_SUCCESS:
                    String result = (String) msg.obj;
                    String type;
                    try {
                        JSONObject json = new JSONObject(result);
                        type = json.getString("result");
                        if (type.equals("success")) {
                            login();
                        } else if (type.equals("fail")) {
                            animDialog.dismiss();
                            Toast.makeText(ThridBindActivity.this, json.getString("errorMsg"), Toast.LENGTH_SHORT).show();
                        } else {
                            animDialog.dismiss();
                            Toast.makeText(ThridBindActivity.this, "请输入正确的号码或密码", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        animDialog.dismiss();
                    }

                    break;
                case REGISTER_ID_OK:
                    String mobilecode = (String) msg.obj;
                    if (mobilecode != null) {
                        Log.i("registerid发送成功：", mobilecode);
                        System.out.println(mobilecode);
                    } else {
                        Toast.makeText(ThridBindActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case LOGIN_SUCCESS:

                    String login = (String) msg.obj;
                    LoginResult loginResult = new Gson().fromJson(login, LoginResult.class);
                    if (loginResult.getResult().equals(Constant.SUCCESS)) {
                        saveInfor(loginResult);
                        String regId = JPushInterface.getRegistrationID(UIUtils.getContext());
                        SPUtils.put(UIUtils.getContext(),Constant.IS_LOGIN,true);
                        registerId(BaseApplication.registrationId);
                        loginChat();
                    } else {
                        animDialog.dismiss();
                        Toast.makeText(UIUtils.getContext(), "登录失败！", Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }
    };
    private String password;
    private PersistentCookieStore persistentCookieStore;
    private String userName;
    private String openId;
    private String mediaType;
    private String encryptPassword;


    /**
     * 保存用户信息
     *
     * @param loginResult
     */
    private void saveInfor(LoginResult loginResult) {
        String id = loginResult.getData().getId(); //唯一标示
        String memberRank = loginResult.getData().getMemberRank();//会员等级
        String memberType = loginResult.getData().getMemberType();//会员类型
        String mobile = loginResult.getData().getMobile();//会员手机
        String redIntegral = loginResult.getData().getRedIntegral();//积分

        SPUtils.put(UIUtils.getContext(), Constant.LOGIN_ID, id);
        SPUtils.put(UIUtils.getContext(), Constant.LOGIN_MEMBERRANK, memberRank);
        SPUtils.put(UIUtils.getContext(), Constant.LOGIN_MEMBERTYPE, memberType);
        SPUtils.put(UIUtils.getContext(), Constant.LOGIN_MOBILE, mobile);
        SPUtils.put(UIUtils.getContext(), Constant.LOGIN_REDINTEGRAL, redIntegral);

        SPUtils.put(UIUtils.getContext(), Constant.LOGIN_PASSWORD, password);

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyKitKatTranslucency();
        setContentView(R.layout.activity_thrid_bind);

        Intent intent = getIntent();
        openId = intent.getStringExtra("openId");
        mediaType = intent.getStringExtra("mediaType");



        initView();
    }

    private void initView() {
        ImageView mIvClose= (ImageView) findViewById(R.id.third_bind_iv_back);

        mEtPhone = (EditText) findViewById(R.id.third_bind_et_phone);
        mEtPassword = (EditText) findViewById(R.id.third_bind_et_password);
        Button mBtnLogin = (Button) findViewById(R.id.third_bind_bt_login);
        TextView mTvForgetPassword = (TextView) findViewById(R.id.third_bind_forget_password);



        mIvClose.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        mTvForgetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.third_bind_iv_back:
                finish();
                break;
            case R.id.third_bind_bt_login:
               bindAccount();
                break;
            case R.id.third_bind_forget_password:
                Intent intent = new Intent(this,ForgetPasswordActivity.class);
                startActivity(intent);
                break;

        }
    }

    /**
     * 绑定账号
     */
    private void bindAccount() {
        userName = mEtPhone.getText().toString().trim();
        password = mEtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "请输入用户名！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码！", Toast.LENGTH_SHORT).show();
            return;
        }

        showMyDialog();
        encryptPassword = DesUtil.encrypt(password);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        persistentCookieStore = new PersistentCookieStore(getApplicationContext());
        CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
        builder.cookieJar(cookieJarImpl);
        OkHttpClient client = builder.build();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", userName);
            jsonObject.put("password", encryptPassword);
            jsonObject.put("mediaType", mediaType);
            jsonObject.put("openId", openId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(Constant.JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(Constant.BIND_ACCOUNT_LOGIN)
                .post(body)
                .build();

        Log.e("登陆请求：", "mobile=" + userName + ",password=" + encryptPassword);


        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ThridBindActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ThridBindActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                        animDialog.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.e("登陆返回：", result);

                Message msg = handler.obtainMessage();
                msg.what = BIND_SUCCESS;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 登录
     */
    private void login() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        persistentCookieStore = new PersistentCookieStore(getApplicationContext());
        CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
        builder.cookieJar(cookieJarImpl);
        OkHttpClient client = builder.build();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", userName);
            jsonObject.put("password", encryptPassword);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(Constant.JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(Constant.LOGIN_URL)
                .post(body)
                .build();

        Log.e("登陆请求：", "mobile=" + userName + ",password=" + encryptPassword);


        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ThridBindActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ThridBindActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                        animDialog.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Message msg = handler.obtainMessage();
                msg.what = LOGIN_SUCCESS;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 显示对话框
     */
    public void showMyDialog() {
        animDialog = new MyProgressDialog(this, "玩命加载中...", R.drawable.loading);
        animDialog.show();
        animDialog.setCancelable(true);
        animDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (call.isExecuted()) {
                    call.cancel();
                }
            }
        });
    }


    /**
     *  这个是用来发送jpush的注册id到服务器的，以方便后面单独设备推送消息。
     * @param regId
     */
    private void registerId(String regId) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        persistentCookieStore = new PersistentCookieStore(getApplicationContext());
        CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
        builder.cookieJar(cookieJarImpl);
        OkHttpClient client = builder.build();

        JSONObject json = new JSONObject();
        try {
            json.put("registrationId", regId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(Constant.JSON, json.toString());
        Request request = new Request.Builder()
                .url(Constant.DEVICEREGISTER)
                .post(body)
                .build();

        Log.e("jpush注册id请求：", "rid=" + regId);

        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                ThridBindActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(ThridBindActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();

//                        Toast.makeText(LoginActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                        Log.e("zzz", String.valueOf(e));
                        registerId(BaseApplication.registrationId);

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.e("jpush注册id返回：", result);
                animDialog.dismiss();
                Message msg = handler.obtainMessage();
                msg.what = REGISTER_ID_OK;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });

    }

    //登录环信
    private void loginChat() {
        ThreadManager.getLongPool().execute(new Runnable() {
            @Override
            public void run() {
                if (EMClient.getInstance().isLoggedInBefore() && EMClient.getInstance().getCurrentUser().equals(userName)) {
                    EMClient.getInstance().chatManager().loadAllConversations();
                    Log.d("main", "isLoggedInBefore登录聊天服务器成功！");
                    finish();
                    return;
                }

                EMClient.getInstance().login(userName, "admin123", new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        Log.d("main", "登录聊天服务器成功！");
                        if (animDialog != null) {
                            animDialog.dismiss();
                        }
                        finish();

                        return;
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(final int code, final String message) {
                        Log.d("main", "登录聊天服务器失败！");
                        AppHelper.getInstance().logoutHuanXin();
                        if (animDialog != null) {
                            animDialog.dismiss();
                        }
                    }
                });
            }
        });
    }
}
