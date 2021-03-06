package com.jiahehongye.robred;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.jiahehongye.robred.activity.LoginActivity;
import com.jiahehongye.robred.fragment.HomeFragment;
import com.jiahehongye.robred.fragment.HomeSingleFragment;
import com.jiahehongye.robred.fragment.MessageFragment;
import com.jiahehongye.robred.fragment.PersonalFragment;
import com.jiahehongye.robred.fragment.VedioFragment;
import com.jiahehongye.robred.jiecao.JCVideoPlayer;
import com.jiahehongye.robred.utils.FragmentFactory;
import com.jiahehongye.robred.utils.LogUtil;
import com.jiahehongye.robred.utils.NoticeTrigger;
import com.jiahehongye.robred.utils.NoticeTriggerID;
import com.jiahehongye.robred.utils.NoticeTrigger_MGR;
import com.jiahehongye.robred.utils.SPUtils;
import com.jiahehongye.robred.utils.TUtils;
import com.jiahehongye.robred.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    /* 下载中 */
    private static final int DOWNLOAD = 111;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 222;
    private RadioGroup mRgRoot;
    private BaseFragment mTempFragment;
    private FragmentFactory instances;
    private long exitTime = 0;
    private boolean isLogin;
    private String downLoadURL;
    private TextView mTvUnreadNumber;
    /**
     * 当收到消息的时候更新数据
     */
    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            boolean b = mTempFragment instanceof MessageFragment;
            if (!b) {
                refreshUIWithMessage();
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {

        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
        }
    };
    private boolean flag = true;
    private LocalBroadcastManager broadcastManager;
    private HomeFragment homeFragment;
    private BaseFragment fragment;
    private int lastClickId = R.id.main_rb_home;
    private String oldVersionCode;
    private String newVersionCode;
    /**
     * 显示软件下载对话框
     */
    /* 更新进度条 */
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;
    /**
     * 下载文件线程
     *
     * @author dingliping
     * @date 2016-2-22
     */
    /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case -1:
                    Toast.makeText(MainActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    try {
                        // 获取到服务器返回的时间列表
                        String data = (String) msg.obj;
                        Log.e("自动更新数据:", data);

                        JSONObject jsonObject = new JSONObject(data);
                        String result = jsonObject.getString("result");
                        if (result.equals("success")) {
                            JSONObject object = jsonObject.getJSONObject("data");
                            downLoadURL = object.getString("file");
                            newVersionCode = object.getString("versioncode");
                            // 判断版本号是否一致
                            Log.e("新版本号", newVersionCode);
                            Log.e("老版本号", oldVersionCode + "");
                            Log.e("软件地址", downLoadURL);

                            if (!newVersionCode.equals(oldVersionCode + "")) {
                                Log.e("进入", "===");
                                showNoticeDialog();
                            } else {
//                                Toast.makeText(MainActivity.this, "当前已是最新版本", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;

                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
                    mProgress.setProgress(progress);
                    break;

                case DOWNLOAD_FINISH:
                    // 安装文件
                    installApk();
                    break;

                default:
//                    Log.i(TAG, "Unhandled msg - " + msg.what);
                    break;
            }
        }

        ;
    };
    private String FRAGMNET_INDEX = "FRAGMNET_INDEX";
    private int mCurrentIndex = 0;
    private String appMetaData;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(FRAGMNET_INDEX, 2);
        }
        appMetaData = getAppMetaData(this);
//        Toast.makeText(MainActivity.this, appMetaData, Toast.LENGTH_SHORT).show();
        LogUtil.LogShitou("appchannleid :",appMetaData);
        setContentView(R.layout.activity_main);
        findViews();



    }


//    @Override
//    public void onConfigurationChanged (Configuration newConfig){
//        super.onConfigurationChanged(newConfig);
//        setContentView(R.layout.activity_main);
//        //注意，这里删除了init()，否则又初始化了，状态就丢失
//        findViews();
//
//        setListensers();
//
//    }

    private void setListensers() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        //onSaveInstanceState()
        int x = 1;
        if (mTempFragment instanceof VedioFragment) {
            x = 2;
        } else if (mTempFragment instanceof HomeSingleFragment) {
            x = 1;
        }
        outState.putInt(FRAGMNET_INDEX, x);

    }

    //
    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        super.setRequestedOrientation(requestedOrientation);

    }

    private void findViews() {
        applyKitKatTranslucency();
        SPUtils.put(UIUtils.getContext(), Constant.FIRST_COMEIN, false);
        isLogin = (boolean) SPUtils.get(UIUtils.getContext(), Constant.IS_LOGIN, false);


        instances = FragmentFactory.getInstances();
        fragment = instances.createFragment(mCurrentIndex);

        getSupportFragmentManager().beginTransaction().add(R.id.main_fl_container, fragment).commit();
        mTempFragment = fragment;
        initView();

        checkUpdate();
        registerBroadcastReceiver();

        if (ActivityCompat.checkSelfPermission(UIUtils.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(UIUtils.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String[] perms = {"android.permission.ACCESS_FINE_LOCATION"};
            boolean b = UIUtils.hasPermission("android.permission.ACCESS_FINE_LOCATION");
            if (!b) {
                requestPermissions(perms, 200);
            }

            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                try {

                } catch (RuntimeException e) {

                }
                break;

        }
    }

    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_AVATAR_CHANAGED);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (mTempFragment instanceof PersonalFragment) {
                    PersonalFragment mTempFragment = (PersonalFragment) MainActivity.this.mTempFragment;
                    mTempFragment.refresh();
                }

            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册消息的监听
        MobclickAgent.onResume(this);

        isLogin = (boolean) SPUtils.get(UIUtils.getContext(), Constant.IS_LOGIN, false);
        if (isLogin) {
            EMClient.getInstance().chatManager().addMessageListener(messageListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isLogin) {
            EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        }
    }

    private void refreshUIWithMessage() {
        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                // refresh unread count
                updateUnreadLabel();

            }
        });
    }

    /**
     * update unread message count
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
            mTvUnreadNumber.setText(String.valueOf(count));
            mTvUnreadNumber.setVisibility(View.VISIBLE);
        } else {
            mTvUnreadNumber.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * get unread message count
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        return unreadMsgCountTotal;
    }

    private void initView() {
        mRgRoot = (RadioGroup) findViewById(R.id.main_rg_container);
        mRgRoot.setOnCheckedChangeListener(this);
        mTvUnreadNumber = (TextView) findViewById(R.id.message_tv_unreade_number);

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        BaseFragment fragment = null;

        switch (checkedId) {
            case R.id.main_rb_home:
                fragment = instances.createFragment(0);
                lastClickId = R.id.main_rb_home;
                break;
            case R.id.main_rb_hot:
                fragment = instances.createFragment(1);
                lastClickId = R.id.main_rb_hot;
                break;
            case R.id.main_rb_message:
                if (!isLogin) {
                    startLogin();
                } else {
                    fragment = instances.createFragment(2);
                    mTvUnreadNumber.setText("");
                    mTvUnreadNumber.setVisibility(View.GONE);
                }

                break;
            case R.id.main_rb_address:
                if (!isLogin) {
                    startLogin();
                } else {
                    fragment = instances.createFragment(3);
                }
                break;
            case R.id.main_rb_personal:
                if (!isLogin) {
                    startLogin();
                } else {
                    fragment = instances.createFragment(4);
                }
                break;
        }

        if (fragment != null && fragment != mTempFragment) {
            if (!fragment.isAdded()) {
                getSupportFragmentManager().beginTransaction().hide(mTempFragment)
                        .add(R.id.main_fl_container, fragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().hide(mTempFragment)
                        .show(fragment).commit();
            }
            mTempFragment = fragment;
            lastClickId = checkedId;
        }

        if (fragment == null) {
            mRgRoot.check(lastClickId);
        }
    }

    public void startLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SPUtils.put(UIUtils.getContext(), Constant.IS_LOGIN, false);
//        new PersistentCookieStore(UIUtils.getContext()).removeAll();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//
//
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//            builder.setMessage("确认退出吗？");
//            builder.setTitle("提示");
//            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
////                    SPUtils.put(UIUtils.getContext(), Constant.IS_LOGIN, false);
////                    new PersistentCookieStore(UIUtils.getContext()).removeCookies();
//                    UIUtils.exit();
//                }
//            });
//            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            builder.create().show();5
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                TUtils.showToast(UIUtils.getContext(), "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                MobclickAgent.onKillProcess(MainActivity.this);
                finish();
                UIUtils.exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }


    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    private String getVersionCode(Context context) {
        String versionCode = "";
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo("com.jiahehongye.robred", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    private void checkUpdate() {
        oldVersionCode = getVersionCode(getApplicationContext()) + "";
        if(appMetaData.equals("jhb_android")){
            appMetaData = "";
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.build();

//        JSONObject jsonObject = new JSONObject();
//
//            try {
//                jsonObject.put("type", appMetaData);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        RequestBody body = RequestBody.create(Constant.JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(Constant.AUTOUPDATE)
                .get()
                .addHeader("type",appMetaData)
                .build();



       Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.LogShitou("checkUpdate",e.toString());
                Message message = handler.obtainMessage();
                message.what = -1;
                handler.sendMessage(message);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                LogUtil.LogShitou("checkUpdate",data.toString());

                if (data != null) {
                    Message message = handler.obtainMessage();
                    message.obj = data;
                    message.what = 0;
                    handler.sendMessage(message);

                }
            }
        });










//        HttpUtils httpUtils = new HttpUtils();
//        httpUtils.send(HttpRequest.HttpMethod.GET, Constant.AUTOUPDATE, new RequestCallBack<String>() {
//
//            @Override
//            public void onFailure(HttpException arg0, String arg1) {
//                // TODO Auto-generated method stub
//                Message message = handler.obtainMessage();
//                message.what = -1;
//                handler.sendMessage(message);
//            }
//
//            @Override
//            public void onSuccess(ResponseInfo<String> arg0) {
//                // TODO Auto-generated method stub
//                String data = arg0.result;
//                if (data != null) {
//                    Message message = handler.obtainMessage();
//                    message.obj = data;
//                    message.what = 0;
//                    handler.sendMessage(message);
//
//                }
//            }
//        });

    }
//APP有重大更新，如安装不上，请卸载当前版本，并去各大应用商店下载，会有惊喜，
    /**
     * 显示软件更新对话框
     */
    private void showNoticeDialog() {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("软件更新");
        builder.setMessage("立即更新吗？");
        // 更新
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 显示下载对话框
                showDownloadDialog();
            }
        });
        // 稍后更新
        builder.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                UIUtils.exit();
                finish();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.setCanceledOnTouchOutside(false);
        noticeDialog.setCancelable(false);
        noticeDialog.show();
    }

    private void showDownloadDialog() {
        // 构造软件下载对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("正在更新");
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        builder.setView(v);
        // 取消更新
//        builder.setNegativeButton("取消更新", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
////                dialog.dismiss();
//            }
//        });
        mDownloadDialog = builder.create();
        mDownloadDialog.setCanceledOnTouchOutside(false);
        mDownloadDialog.setCancelable(false);
        mDownloadDialog.show();
        // 下载文件
        downloadApk();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
        new downloadApkThread().start();
    }

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(mSavePath, "jinhoubao" + newVersionCode + ".apk");
        if (!apkfile.exists()) {
            return;
        }

        NoticeTrigger trigger = new NoticeTrigger();
        trigger.setTriggerID(NoticeTriggerID.JUMP_TO_MAIN);
        NoticeTrigger_MGR.Instance().notifyTopicbserver(trigger);


        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        MainActivity.this.startActivity(i);
    }

    ;

    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "download";
                    URL url = new URL(downLoadURL);
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, "jinhoubao" + newVersionCode + ".apk");
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        handler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0) {
                            // 下载完成
                            handler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 取消下载对话框显示
            mDownloadDialog.dismiss();
        }
    }




    /**
     * 获取application中指定的meta-data
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx) {
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString("UMENG_CHANNEL");
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }
}
