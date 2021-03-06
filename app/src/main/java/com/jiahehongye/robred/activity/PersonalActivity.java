package com.jiahehongye.robred.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.hyphenate.easeui.domain.EaseUser;
import com.jiahehongye.robred.BaseActivity;
import com.jiahehongye.robred.Constant;
import com.jiahehongye.robred.R;
import com.jiahehongye.robred.bean.PhotoBean;
import com.jiahehongye.robred.cook.CookieJarImpl;
import com.jiahehongye.robred.cook.PersistentCookieStore;
import com.jiahehongye.robred.db.Model;
import com.jiahehongye.robred.utils.ActionSheetDialog;
import com.jiahehongye.robred.utils.PUtil;
import com.jiahehongye.robred.utils.SPUtils;
import com.jiahehongye.robred.utils.UIUtils;
import com.jiahehongye.robred.view.GlideCircleTransform;
import com.jiahehongye.robred.view.MyGridView;
import com.jiahehongye.robred.view.MyProgressDialog;
import com.lling.photopicker.PhotoPickerActivity;
import com.lling.photopicker.utils.OtherUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonalActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUESTCODE_CUTTING = 202; // 图片裁切标记

    private PersistentCookieStore persistentCookieStore;
    private ArrayList<PhotoBean> photos = new ArrayList<>();
    private static final int PICK_PHOTO = 1;
    private ArrayList<String> xz = new ArrayList<>();
    private MyGridView photo_grid;
    private MyProgressDialog animDialog;
    private Call call;
    private static final int GET_ALL = 0000;
    private String nickname;
    private List<String> mResults;
    private String job;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA

    };
    private RelativeLayout mBack;
    private RelativeLayout mCommit;
    private ImageView mHead;
    private TextView mPhoneNumber, mSex;
    private TextView et_name, et_age, et_sign, et_job, et_school, et_more, et_hobby, qinggan, mudi, xueli, xingzuo;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private GridAdapter mAdapter;
    private String schoolRecord;
    private String datingPurpose;
    private String personalDescription;
    private String hobbies;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_ALL:
                    String s = (String) msg.obj;
                    try {
                        JSONObject object = new JSONObject(s);
                        if (object.getString("result").equals("success")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            Glide.with(PersonalActivity.this).load(data.getString("userPhoto")).transform(new GlideCircleTransform(UIUtils.getContext())).into(mHead);
                            /**------------------------------------------------*/
                            //设置用户的头像和昵称
                            String  mobile= (String) SPUtils.get(UIUtils.getContext(), Constant.LOGIN_MOBILE, "");
                            Model model = new Model(UIUtils.getContext());
                            EaseUser user = new EaseUser(mobile);
                            user.setAvatar(data.getString("userPhoto"));
                            user.setNick(data.getString("nickName"));

                            model.saveContact(user);
                            broadcastManager = LocalBroadcastManager.getInstance(UIUtils.getContext());
                            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_AVATAR_CHANAGED));
                            /**------------------------------------------------*/

                            mPhoneNumber.setText(data.getString("mobile"));

                            nickname = data.getString("nickName");
                            job = data.getString("profession");
                            et_name.setText(data.getString("nickName"));
                            xingzuo.setText(data.getString("constellation"));

                            if (data.getString("gender").equals("0")) {
                                mSex.setText("保密");
                            } else if (data.getString("gender").equals("1")) {
                                mSex.setText("男");
                            } else if (data.getString("gender").equals("2")) {
                                mSex.setText("女");
                            }

                            datingPurpose = data.getString("datingPurpose");
                            schoolRecord = data.getString("schoolRecord");
                            personalDescription = data.getString("personalDescription");
                            hobbies = data.getString("hobbies");
                            qinggan.setText(data.getString("maritalStatus"));
                            et_job.setText(data.getString("profession"));
                            xueli.setText(data.getString("schoolRecord"));
                            mudi.setText(data.getString("datingPurpose"));
                            et_sign.setText(data.getString("personalDescription"));
                            et_hobby.setText(data.getString("hobbies"));

                            String resList = data.getString("resList");
                            photos = (ArrayList<PhotoBean>) JSON.parseArray(resList, PhotoBean.class);


                            mAdapter = new GridAdapter(photos);
                            photo_grid.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };
    private String sexStr;
    private String ganqingStr;
    private String xzStr;
    private int mColumnWidth;
    private TextView personal_ok;
    private ArrayList<String> result;
    private ArrayList<String> head;
    private LocalBroadcastManager broadcastManager;
    private Uri uritempFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyKitKatTranslucency();
        setContentView(R.layout.activity_personal);
        int screenWidth = OtherUtils.getWidthInPx(act);
        mColumnWidth = (screenWidth - OtherUtils.dip2px(act, 12)) / 4;
        xz.add("白羊座");
        xz.add("金牛座");
        xz.add("双子座");
        xz.add("巨蟹座");
        xz.add("狮子座");
        xz.add("处女座");
        xz.add("天秤座");
        xz.add("天蝎座");
        xz.add("射手座");
        xz.add("摩羯座");
        xz.add("水瓶座");
        xz.add("双鱼座");
        initView();


        int permission = ActivityCompat.checkSelfPermission(PersonalActivity.this, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    PersonalActivity.this,
                    PERMISSIONS_STORAGE,//需要请求的所有权限，这是个数组String[]
                    REQUEST_EXTERNAL_STORAGE//请求码
            );
        }

        photo_grid.setFocusable(false);

    }



    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){

            case REQUEST_EXTERNAL_STORAGE:
            try {

            }catch (RuntimeException e){

            }
            break;

        }

    }






    private void initView() {
        photo_grid = (MyGridView) findViewById(R.id.photo_grid);
        personal_ok = (TextView) findViewById(R.id.personal_ok);
        mBack = (RelativeLayout) findViewById(R.id.personal_rl_back);
        mCommit = (RelativeLayout) findViewById(R.id.personal_rl_commit);
        mHead = (ImageView) findViewById(R.id.personal_iv_head);
        mPhoneNumber = (TextView) findViewById(R.id.personal_tv_phone_number);
        mSex = (TextView) findViewById(R.id.personal_tv_sex);
        et_name = (TextView) findViewById(R.id.personal_et_name);
        et_sign = (TextView) findViewById(R.id.personal_et_perosonal_sign);
        et_job = (TextView) findViewById(R.id.personal_et_job);
        et_hobby = (TextView) findViewById(R.id.personal_et_hobby);
        qinggan = (TextView) findViewById(R.id.personal_et_perosonal_qinggan);
        mudi = (TextView) findViewById(R.id.personal_et_mudi);
        xueli = (TextView) findViewById(R.id.personal_et_xueli);
        xingzuo = (TextView) findViewById(R.id.personal_tv_xingzuo);

        xingzuo.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mCommit.setOnClickListener(this);
        mHead.setOnClickListener(this);
        mSex.setOnClickListener(this);
        qinggan.setOnClickListener(this);
        personal_ok.setOnClickListener(this);

        et_hobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeHobby.class);
                intent.putExtra("hobby", hobbies);
                startActivity(intent);
            }
        });

        et_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeGexing.class);
                intent.putExtra("gexing", personalDescription);
                startActivity(intent);
            }
        });
        mudi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeMudi.class);
                intent.putExtra("mudi", datingPurpose);
                startActivity(intent);
            }
        });
        xueli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeXueli.class);
                intent.putExtra("xueli", schoolRecord);
                startActivity(intent);
            }
        });
        et_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeNickName.class);
                intent.putExtra("nickname", nickname);
                startActivity(intent);
            }
        });
        et_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeJob.class);
                intent.putExtra("job", job);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.personal_rl_back:
                finish();
                break;

            case R.id.personal_ok:
                break;
            case R.id.personal_tv_xingzuo:
                PUtil.alertBottomWheelOption(PersonalActivity.this, xz, new PUtil.OnWheelViewClick() {
                    @Override
                    public void onClick(View view, int postion) {
                        xzStr = xz.get(postion).toString();
                        changeXz();
                    }
                });
                break;

            case R.id.personal_et_perosonal_qinggan:
                new ActionSheetDialog(PersonalActivity.this).builder().setCancelable(false)
                        .addSheetItem("保密", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                ganqingStr = "保密";
                                changeQinggan();
                            }
                        }).addSheetItem("单身", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        ganqingStr = "单身";
                        changeQinggan();
                    }
                }).addSheetItem("恋爱中", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        ganqingStr = "恋爱中";
                        changeQinggan();
                    }
                }).addSheetItem("已婚", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        ganqingStr = "已婚";
                        changeQinggan();
                    }
                }).show();
                break;

            case R.id.personal_rl_commit:
                break;
            case R.id.personal_iv_head:
                int permission = ActivityCompat.checkSelfPermission(PersonalActivity.this, Manifest.permission.CAMERA);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    Toast.makeText(PersonalActivity.this, "请检查文件读取和拍照权限", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(PersonalActivity.this, PhotoPickerActivity.class);
                    intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, true);
                    intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, 0);
                    intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, 1);
                    startActivityForResult(intent, 6);
                }


                break;
            case R.id.personal_tv_sex:
                new ActionSheetDialog(PersonalActivity.this).builder().setCancelable(false)
                        .addSheetItem("保密", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                sexStr = "0";
                                changeSex();
                            }
                        }).addSheetItem("男", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        sexStr = "1";
                        changeSex();
                    }
                }).addSheetItem("女", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        sexStr = "2";
                        changeSex();
                    }
                }).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO) {
            if (resultCode == RESULT_OK) {
                if (data!=null){
                    result = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
                    if (result.size()>0) {
                        uploadImageToServer();
                    }
                }

            }
        }else if (requestCode == 6){
            if(data!=null){
                head = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);

                if (head.size()>0){
                    File f = new File(head.get(0));
                    startPhotoZoom(Uri.fromFile(f));

                }
            }

        }else if (requestCode ==REQUESTCODE_CUTTING){

            if(data!=null&&data.getData()!=null&&data.getData().getPath()!=null){
                File f =  new File(data.getData().getPath());
                uploadhead(f);
            }else {
                if (head.size()>0){
                    File f = new File(head.get(0));
                    uploadhead(f);
                }
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 25);
        intent.putExtra("aspectY", 25);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        intent.putExtra("return-data", false);
        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis()+"small.jpg");
        String urlpath = uritempFile.getPath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private void uploadImageToServer() {

        showMyDialog();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                File f = new File(result.get(i));
                builder.addFormDataPart("userBatchPhoto", f.getName(), RequestBody.create(MEDIA_TYPE_PNG, f));
            }
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(Constant.CHANGE_MEMBER_INFO)
                .post(requestBody)
                .build();

        OkHttpClient.Builder builder1 = new OkHttpClient.Builder();
        persistentCookieStore = new PersistentCookieStore(PersonalActivity.this);
        CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
        builder1.cookieJar(cookieJarImpl);
        OkHttpClient client = builder1.build();

        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                animDialog.dismiss();
                PersonalActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PersonalActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                animDialog.dismiss();
                String result = response.body().string();
                Message msg = handler.obtainMessage();
                msg.what = GET_ALL;
                msg.obj = result;
                handler.sendMessage(msg);

            }
        });
    }

    private void uploadhead(File file) {

        showMyDialog();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

//        if (head.size() > 0) {
//            for (int i = 0; i < head.size(); i++) {
//                File f = new File(head.get(i));
                builder.addFormDataPart("userPhoto", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
//            }
//        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(Constant.CHANGE_MEMBER_INFO)
                .post(requestBody)
                .build();

        OkHttpClient.Builder builder1 = new OkHttpClient.Builder();
        persistentCookieStore = new PersistentCookieStore(PersonalActivity.this);
        CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
        builder1.cookieJar(cookieJarImpl);
        OkHttpClient client = builder1.build();

        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                animDialog.dismiss();
                PersonalActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PersonalActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                animDialog.dismiss();
                String result = response.body().string();
                Message msg = handler.obtainMessage();
                msg.what = GET_ALL;
                msg.obj = result;
                handler.sendMessage(msg);

            }
        });
    }

    /**
     * t
     */

    private void getdata() {


        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        persistentCookieStore = new PersistentCookieStore(PersonalActivity.this);
        CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
        builder.cookieJar(cookieJarImpl);
        OkHttpClient client = builder.build();

        JSONObject jsonObject = new JSONObject();

        RequestBody body = RequestBody.create(Constant.JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(Constant.MEMBERINFO)
                .post(body)
                .build();


        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PersonalActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PersonalActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onResume() {
        super.onResume();
        getdata();
    }


    private void changeSex() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        persistentCookieStore = new PersistentCookieStore(PersonalActivity.this);
        CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
        builder.cookieJar(cookieJarImpl);
        OkHttpClient client = builder.build();


        RequestBody formBody = new FormBody.Builder()
                .add("gender", sexStr).build();

        Request request = new Request.Builder()
                .url(Constant.CHANGE_MEMBER_INFO)

                .post(formBody)
                .build();


        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PersonalActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PersonalActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject object = new JSONObject(result);
                    String s = object.getString("result");
                    if (s.equals("success")) {
                        PersonalActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                if (sexStr.equals("0")) {
                                    mSex.setText("保密");
                                } else if (sexStr.equals("1")) {
                                    mSex.setText("男");
                                } else if (sexStr.equals("2")) {
                                    mSex.setText("女");
                                }
                            }
                        });

                    } else {
                        PersonalActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    private void changeXz() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        persistentCookieStore = new PersistentCookieStore(PersonalActivity.this);
        CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
        builder.cookieJar(cookieJarImpl);
        OkHttpClient client = builder.build();


        RequestBody formBody = new FormBody.Builder()
                .add("constellation", xzStr).build();

        Request request = new Request.Builder()
                .url(Constant.CHANGE_MEMBER_INFO)

                .post(formBody)
                .build();


        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PersonalActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PersonalActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject object = new JSONObject(result);
                    String s = object.getString("result");
                    if (s.equals("success")) {
                        PersonalActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                xingzuo.setText(xzStr);
                            }
                        });

                    } else {
                        PersonalActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void changeQinggan() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        persistentCookieStore = new PersistentCookieStore(PersonalActivity.this);
        CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
        builder.cookieJar(cookieJarImpl);
        OkHttpClient client = builder.build();


        RequestBody formBody = new FormBody.Builder()
                .add("maritalStatus", ganqingStr).build();

        Request request = new Request.Builder()
                .url(Constant.CHANGE_MEMBER_INFO)

                .post(formBody)
                .build();


        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PersonalActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PersonalActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject object = new JSONObject(result);
                    String s = object.getString("result");
                    if (s.equals("success")) {
                        PersonalActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                qinggan.setText(ganqingStr);
                            }
                        });

                    } else {
                        PersonalActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private class GridAdapter extends BaseAdapter {
        private List<PhotoBean> pathList;

        public GridAdapter(List<PhotoBean> listUrls) {
            this.pathList = listUrls;
        }

        @Override
        public int getCount() {
            return pathList.size()+1;
        }

        @Override
        public PhotoBean getItem(int position) {
            return pathList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setPathList(List<PhotoBean> pathList) {
            this.pathList = pathList;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_image, null);
                imageView = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(imageView);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mColumnWidth, mColumnWidth);
                imageView.setLayoutParams(params);
            } else {
                imageView = (ImageView) convertView.getTag();
            }

            if(position<getCount()-1){
                displayNetImage(pathList.get(position).getPhoto(),imageView);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(PersonalActivity.this)
                                .setTitle("删除这张照片")
                                .setMessage("确定吗？")
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        OkHttpClient.Builder builder = new OkHttpClient.Builder();
                                        persistentCookieStore = new PersistentCookieStore(PersonalActivity.this);
                                        CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
                                        builder.cookieJar(cookieJarImpl);
                                        OkHttpClient client = builder.build();

                                        RequestBody formBody = new FormBody.Builder()
                                                .add("batchPhotoId", pathList.get(position).getBatchPhotoId()).build();

                                        Request request = new Request.Builder()
                                                .url(Constant.DELETE_PHOTO)

                                                .post(formBody)
                                                .build();


                                        call = client.newCall(request);
                                        call.enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                PersonalActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(PersonalActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                                                        animDialog.dismiss();
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
                                })
                                .setNegativeButton("否", null)
                                .show();


                    }
                });
            }else{
                if(getCount()>8) {
                    imageView.setVisibility(View.GONE);
                }else {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.mipmap.add_photo);
                }
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int permission = ActivityCompat.checkSelfPermission(PersonalActivity.this, Manifest.permission.CAMERA);
                        if (permission != PackageManager.PERMISSION_GRANTED) {
                            // We don't have permission so prompt the user
                            Toast.makeText(PersonalActivity.this, "请检查文件读取和拍照权限", Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent = new Intent(PersonalActivity.this, PhotoPickerActivity.class);
                            intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, true);
                            intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, 1);
                            intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, 8-pathList.size());
                            startActivityForResult(intent, PICK_PHOTO);
                        }

                    }
                });
            }

            return convertView;
        }
    }
}
