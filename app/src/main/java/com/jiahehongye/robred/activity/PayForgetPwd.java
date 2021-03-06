package com.jiahehongye.robred.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jiahehongye.robred.BaseActivity;
import com.jiahehongye.robred.Constant;
import com.jiahehongye.robred.R;
import com.jiahehongye.robred.cook.CookieJarImpl;
import com.jiahehongye.robred.cook.PersistentCookieStore;
import com.jiahehongye.robred.utils.DesUtil;
import com.jiahehongye.robred.view.TimeButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PayForgetPwd extends BaseActivity implements OnClickListener {

    private EditText pay_inputel;
    private EditText pay_fcode;
    private EditText pay_fpwd1;
    private EditText pay_fpwd2;
    private TimeButton pay_code;
    private Button pay_ok;

    private PersistentCookieStore persistentCookieStore;
    private Call call;
    private static final int GET_ALL = 0000;
    private static final int GET_FORGET = 0001;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_ALL:
                    String login = (String) msg.obj;
                    try {
                        JSONObject object = new JSONObject(login);
                        if (object.getString("result").equals("success")) {
                            Toast.makeText(PayForgetPwd.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PayForgetPwd.this, "网络繁忙", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case GET_FORGET:
                    String forget = (String) msg.obj;
                    try {
                        JSONObject object = new JSONObject(forget);

                        if (object.getString("result").equals("success")) {
                            Toast.makeText(PayForgetPwd.this, "修改成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(PayForgetPwd.this, "修改失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyKitKatTranslucency();
        mTintManager.setStatusBarTintResource(R.color.white);
        setContentView(R.layout.pay_forget_pwd);

        pay_inputel = (EditText) findViewById(R.id.pay_inputel);
        pay_fcode = (EditText) findViewById(R.id.pay_fcode);
        pay_fpwd1 = (EditText) findViewById(R.id.pay_fpwd1);
        pay_fpwd2 = (EditText) findViewById(R.id.pay_fpwd2);
        pay_code = (TimeButton) findViewById(R.id.pay_code);
        pay_ok = (Button) findViewById(R.id.pay_ok);
        RelativeLayout account_safe_rl_back = (RelativeLayout) findViewById(R.id.wjpay_back);

        account_safe_rl_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pay_code.setOnClickListener(this);
        pay_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_code:
                //这里要判断是否是电话号码
                String tel = pay_inputel.getText().toString().trim();
                if (tel.length() != 11 && !(BaseActivity.isPhone(tel))) {

                    Toast.makeText(this, "输入的手机号码不正确", Toast.LENGTH_SHORT).show();
                    pay_code.setTextAfter("s").setLenght(1 * 1000);
                } else {
                    pay_code.setTextAfter("s").setLenght(60 * 1000);
                    rCode(tel);
                }
                break;
            case R.id.pay_ok:

                String tel1 = pay_inputel.getText().toString().trim();
                String code = pay_fcode.getText().toString().trim();
                String pay_fpwd11 = pay_fpwd1.getText().toString().trim();
                String pay_fpwd22 = pay_fpwd2.getText().toString().trim();

                String efpwd1 = DesUtil.encrypt(pay_fpwd11);
                String efpwd2 = DesUtil.encrypt(pay_fpwd22);

                if (tel1.length() != 11 && !(BaseActivity.isPhone(tel1))) {
                    Toast.makeText(this, "输入的手机号码不正确", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(code)) {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(pay_fpwd11) && !BaseActivity.isPassword(pay_fpwd11)) {
                    Toast.makeText(this, "请输入6位数字密码1", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(pay_fpwd22) && !BaseActivity.isPassword(pay_fpwd22)) {
                    Toast.makeText(this, "请输入6位数字密码2", Toast.LENGTH_SHORT).show();

                } else if (!pay_fpwd22.equals(pay_fpwd11)) {
                    Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();

                } else {
                    rNext(tel1, code, efpwd1);
                }


//								startActivity(new Intent(PayForgetPwd.this,Login_regrist_Activity.class));


                break;

            default:
                break;
        }

    }

    protected void rCode(String tel) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        persistentCookieStore = new PersistentCookieStore(PayForgetPwd.this);
        CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
        builder.cookieJar(cookieJarImpl);
        OkHttpClient client = builder.build();

        JSONObject json = new JSONObject();


        try {
            json.put("mobile", tel);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(Constant.JSON, json.toString());
        Request request = new Request.Builder()
                .url(Constant.MOBILECODE_URL)
                .post(body)
                .build();


        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PayForgetPwd.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PayForgetPwd.this, "网络连接失败", Toast.LENGTH_SHORT).show();
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

    private void rNext(String inputel, String fcode, String fpwd1) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        persistentCookieStore = new PersistentCookieStore(PayForgetPwd.this);
        CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
        builder.cookieJar(cookieJarImpl);
        OkHttpClient client = builder.build();

        JSONObject json = new JSONObject();


        try {
            json.put("mobile", inputel);
            json.put("checkCode", fcode);
            json.put("payPassword", fpwd1);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(Constant.JSON, json.toString());
        Request request = new Request.Builder()
                .url(Constant.FORGETPAYPASSWORD)
                .post(body)
                .build();


        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PayForgetPwd.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PayForgetPwd.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();

                Message msg = handler.obtainMessage();
                msg.what = GET_FORGET;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });

    }
}
