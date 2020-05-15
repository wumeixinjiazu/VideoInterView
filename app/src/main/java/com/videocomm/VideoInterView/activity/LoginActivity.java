package com.videocomm.VideoInterView.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.activity.base.TitleActivity;
import com.videocomm.VideoInterView.bean.CodeBean;
import com.videocomm.VideoInterView.bean.LoginBean;
import com.videocomm.VideoInterView.simpleListener.SimpleTextWatcher;
import com.videocomm.VideoInterView.utils.AppUtil;
import com.videocomm.VideoInterView.utils.DialogUtil;
import com.videocomm.VideoInterView.utils.HttpUtil;
import com.videocomm.VideoInterView.utils.JsonUtil;
import com.videocomm.VideoInterView.utils.LoginUtil;
import com.videocomm.VideoInterView.utils.PermissionUtil;
import com.videocomm.VideoInterView.utils.SpUtil;
import com.videocomm.VideoInterView.utils.ToastUtil;
import com.videocomm.mediasdk.VComMediaSDK;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

import static com.videocomm.VideoInterView.Constant.LOGIN_PERMISSION_CODE;

public class LoginActivity extends TitleActivity implements View.OnClickListener {
    /**
     * app需要用到的动态添加权限 6.0以上才会去申请
     */
    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    private EditText etPhone;
    private EditText etImageCode;
    private EditText etCode;
    private ImageView ivPhoneClean;
    private ImageView ivImageClean;
    private ImageView ivCodeClean;
    private Button btnLogin;
    private ImageView ivImageCodePic;
    private TextView tvSendCode;

    private static String ssionId;//保存Session

    private static final int HANDLER_SEND_MSG_SUCCESS = 1001;
    private static final int HANDLER_GET_IMAGE_CODE_SUCCESS = 1002;
    private static final int HANDLER_GET_IMAGE_CODE_FAILD = 1003;
    private static final int HANDLER_LOGIN_SUCCESS = 1004;
    private static final int HANDLER_LOGIN_FAILD = 1005;

    private Handler mHandler = new LoginHandler(this);

    private static long time;//记录当前时间
    private Dialog mLoadingDialog;
    private TextView tvAppVersion;
    private VComMediaSDK sdkUnit;

    static class LoginHandler extends Handler {
        private int startTime = 30;
        private LoginActivity activity;

        LoginHandler(LoginActivity loginActivity) {
            activity = loginActivity;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case HANDLER_SEND_MSG_SUCCESS:
                    activity.tvSendCode.setText(startTime + "秒后重发");
                    startTime--;
                    if (startTime != 0) {
                        sendEmptyMessageDelayed(HANDLER_SEND_MSG_SUCCESS, 1000);
                    } else {
                        activity.tvSendCode.setText(activity.getString(R.string.send_code));
                        activity.tvSendCode.setEnabled(true);
                        activity.tvSendCode.setTextColor(Color.parseColor("#F34141"));
                        activity.tvSendCode.setBackground(activity.getResources().getDrawable(R.drawable.bg_red_rect));
                    }
                    break;
                case HANDLER_GET_IMAGE_CODE_SUCCESS:
                    byte[] bytes = (byte[]) msg.obj;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    activity.ivImageCodePic.setImageBitmap(bitmap);
                    break;
                case HANDLER_GET_IMAGE_CODE_FAILD:
                    ToastUtil.show(activity.getString(R.string.refresh_fail_retry));
                    activity.ivImageCodePic.setBackgroundResource(R.drawable.image_code);
                    break;
                case HANDLER_LOGIN_SUCCESS:
                    String content = (String) msg.obj;
                    if (!content.contains("登录成功")) {
                        sendEmptyMessage(HANDLER_LOGIN_FAILD);
                        return;
                    }
                    LoginBean bean = JsonUtil.jsonToBean(content, LoginBean.class);
                    if (bean.getErrorcode() != 0) {
                        ToastUtil.show(bean.getMsg());
                    } else if (bean.getErrorcode() == 0) {
                        SpUtil.getInstance().saveString(SpUtil.TOKEN, bean.getContent().getToken());
                        SpUtil.getInstance().saveString(SpUtil.USERPHONE, bean.getContent().getPhoneNumber());
                        SpUtil.getInstance().saveString(SpUtil.APPID, bean.getContent().getAppId());
                        //清除验证码
                        activity.etImageCode.setText("");
                        activity.etCode.setText("");
                        activity.refreshImageCode();
                        //4.启动页面
                        activity.startActivity(new Intent(activity, ChooseNetworkActivity.class));
                        ToastUtil.show(activity.getString(R.string.login_success));
                    }
                    activity.mLoadingDialog.dismiss();
                    break;
                case HANDLER_LOGIN_FAILD:
                    activity.mLoadingDialog.dismiss();
                    ToastUtil.show(activity.getString(R.string.login_fail_retry));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initView();
        refreshImageCode();
        sdkUnit = VComMediaSDK.GetInstance();
    }

    private void initView() {
        etPhone = findViewById(R.id.et_phone);
        etImageCode = findViewById(R.id.et_image_code);
        etCode = findViewById(R.id.et_code);
        ivPhoneClean = findViewById(R.id.iv_phone_clean);
        ivImageClean = findViewById(R.id.iv_image_clean);
        ivCodeClean = findViewById(R.id.iv_code_clean);
        btnLogin = findViewById(R.id.btn_login);
        tvSendCode = findViewById(R.id.tv_send_code);
        ivImageCodePic = findViewById(R.id.iv_image_code_pic);
        tvAppVersion = findViewById(R.id.tv_app_version);
        etPhone.setOnClickListener(this);
        etImageCode.setOnClickListener(this);
        etCode.setOnClickListener(this);
        ivPhoneClean.setOnClickListener(this);
        ivPhoneClean.setOnClickListener(this);
        ivImageClean.setOnClickListener(this);
        ivCodeClean.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvSendCode.setOnClickListener(this);
        ivImageCodePic.setOnClickListener(this);
        setEditListener();

        //初始化数值
        etPhone.setText(SpUtil.getInstance().getString(SpUtil.USER_MOBILE));
        tvAppVersion.setText("VideoComm V" + AppUtil.getVersionName(this));
    }

    /**
     * 设置EditText监听
     */
    private void setEditListener() {
        //etPhone 文本监听
        etPhone.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                ivPhoneClean.setVisibility(0 == s.length() ? View.GONE : View.VISIBLE);
                btnLoginState();
            }
        });

        etImageCode.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                ivImageClean.setVisibility(0 == s.length() ? View.GONE : View.VISIBLE);
                btnLoginState();
            }
        });

        etCode.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                ivCodeClean.setVisibility(0 == s.length() ? View.GONE : View.VISIBLE);
                btnLoginState();
            }
        });

        //etPhone 焦点监听
        etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ivPhoneClean.setVisibility(hasFocus && etPhone.getText().length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        etImageCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ivImageClean.setVisibility(hasFocus && etImageCode.getText().length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        etCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ivCodeClean.setVisibility(hasFocus && etCode.getText().length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        //监听软键盘操作
        etCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_GO:
                        clearEditFocus();
                        startActivity(new Intent(LoginActivity.this, ChooseNetworkActivity.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 根据三个输入框的字符长度来决定登录按钮的状态
     */
    private void btnLoginState() {
        if (etPhone.getText().length() > 0 && etImageCode.getText().length() > 0 && etCode.getText().length() > 0) {
            btnLogin.setEnabled(true);
        } else {
            btnLogin.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title_right://进入设置界面
                clearEditFocus();
                startActivity(new Intent(LoginActivity.this, SettingActivity.class));
                break;
            case R.id.btn_login://登录
                if (PermissionUtil.checkPermission(this, permissions, LOGIN_PERMISSION_CODE)) {
                    //成功
                    clearEditFocus();
                    checkDataAndLogin();
                }
                break;
            case R.id.tv_send_code://验证码请求
                checkDataAndSend();
                break;
            case R.id.iv_image_code_pic://图形验证码请求
                refreshImageCode();
                break;
            case R.id.iv_phone_clean://手机号清除
                etPhone.setText("");
                break;
            case R.id.iv_image_clean://图形码清除
                etImageCode.setText("");
                break;
            case R.id.iv_code_clean://验证码清除
                etCode.setText("");
                break;
            default:
                break;
        }
    }

    //权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(tag, "requestCode" + requestCode);
        switch (requestCode) {
            case LOGIN_PERMISSION_CODE:
                if (permissions.length == this.permissions.length && grantResults[0] == 0) {
                    //成功 调用相机
                    clearEditFocus();
                    checkDataAndLogin();
                } else {
                    //某个权限拒绝
                    for (String permission : this.permissions) {
                        //判断用户拒绝权限是是否勾选don't ask again选项，若勾选需要客户手动打开权限
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permission)) {
                            showWaringDialog();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 提示用户自行打开权限
     */
    private void showWaringDialog() {
        new AlertDialog.Builder(this)
                .setTitle("警告！")
                .setMessage("请前往设置->应用->VideoTalk->权限中打开相关权限，否则功能无法正常运行！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }).show();
    }

    /**
     * 刷新图形验证码
     */
    private void refreshImageCode() {
        //1.向服务器请求图形验证码
        HttpUtil.requestImageCaptcha(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(HANDLER_GET_IMAGE_CODE_FAILD);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200) {
                    mHandler.sendEmptyMessage(HANDLER_GET_IMAGE_CODE_FAILD);
                    return;
                }
                //保存session
                Headers headers = response.headers();
                Log.d("info_headers", "header " + headers);
                List<String> cookies = headers.values("Set-Cookie");
                String session = cookies.get(0);
                Log.d("info_cookies", "onResponse-size: " + cookies);

                ssionId = session.substring(0, session.indexOf(";"));
                Log.i("info_s", "session is  :" + ssionId);
                //获取流数据转换为BItmap
                byte[] bytes = response.body().bytes();
                Message obtain = Message.obtain();
                obtain.what = HANDLER_GET_IMAGE_CODE_SUCCESS;
                obtain.obj = bytes;
                mHandler.sendMessage(obtain);
            }
        });
    }

    /**
     * 检查输入的数据并发送验证码
     */
    private void checkDataAndSend() {
        //1.检查手机号是否正确
        if (!LoginUtil.checkPhone(etPhone.getText().toString())) {
            return;
        }
        //2.检查图形码是否正确
        if (!LoginUtil.checkImageCode(etImageCode.getText().toString())) {
            return;
        }


        //3.向服务器请求给对应的手机号发送验证码
        HttpUtil.requestSendSMSForUser(etPhone.getText().toString(), etImageCode.getText().toString(), ssionId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    ToastUtil.show(getString(R.string.send_faild_retry));
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.code() != 200) {
                        ToastUtil.show(getString(R.string.send_faild_retry));
                        return;
                    }
                    CodeBean bean = null;
                    try {
                        bean = JsonUtil.jsonToBean(response.body().string(), CodeBean.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bean.getErrorcode() != 0) {
                        ToastUtil.show(getString(R.string.send_faild_retry));
                        return;
                    } else if (bean.getErrorcode() == 0) {
                        //发送成功 通知handler更新UI
                        tvSendCode.setEnabled(false);
                        tvSendCode.setTextColor(Color.parseColor("#cccccc"));
                        tvSendCode.setBackground(getResources().getDrawable(R.drawable.bg_gray_rect_r));
                        ToastUtil.show(getString(R.string.send_success));
                        mHandler.sendEmptyMessage(HANDLER_SEND_MSG_SUCCESS);
                    }
                });

            }
        });
    }

    /**
     * 检查输入的数据并登录
     */
    private void checkDataAndLogin() {
        //1.检查手机号是否正确
        if (!LoginUtil.checkPhone(etPhone.getText().toString())) {
            return;
        }
        //2.检查图形码是否正确
        if (!LoginUtil.checkImageCode(etImageCode.getText().toString())) {
            return;
        }
        //3.检查验证码是否正确
        if (!LoginUtil.checkCode(etCode.getText().toString())) {
            return;
        }

        //保存手机号
        SpUtil.getInstance().saveString(SpUtil.USER_MOBILE, etPhone.getText().toString());

        mLoadingDialog = DialogUtil.createLoadingDialog(this, getString(R.string.login_loading));

        HttpUtil.requestLogin(etPhone.getText().toString(), etImageCode.getText().toString(), etCode.getText().toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(tag, e.getMessage());
                mHandler.sendEmptyMessage(HANDLER_LOGIN_FAILD);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                Log.d(tag, content);
                Message obtain = Message.obtain();
                obtain.what = HANDLER_LOGIN_SUCCESS;
                obtain.obj = content;
                mHandler.sendMessage(obtain);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //修复由于内存不足导致页面的文字清除会展示BUG
        //初始化标题布局
        mTitleLayoutManager.setTitle(getString(R.string.login)).showRight(true).setRightIcon(R.drawable.ic_setting).setRightListener(this);

        clearEditFocus();
        ivPhoneClean.setVisibility(View.INVISIBLE);
        ivImageClean.setVisibility(View.INVISIBLE);
        ivCodeClean.setVisibility(View.INVISIBLE);
    }

    /**
     * 清除EditView的焦点
     */
    private void clearEditFocus() {
        etPhone.clearFocus();
        etImageCode.clearFocus();
        etCode.clearFocus();
    }

    @Override
    public void onBackPressed() {
        Log.i(tag, "onBackPressed");
        long currentTime = System.currentTimeMillis();
        if (currentTime - time > 2000) {
            time = currentTime;
            ToastUtil.show(getString(R.string.press_again));
            return;
        }
        ToastUtil.cancle();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sdkUnit != null) {
            sdkUnit.VCOM_Release();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
