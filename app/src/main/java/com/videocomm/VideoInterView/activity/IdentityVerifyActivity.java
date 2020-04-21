package com.videocomm.VideoInterView.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.Constant;
import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.activity.base.TitleActivity;
import com.videocomm.VideoInterView.bean.IdCardFrontBean;
import com.videocomm.VideoInterView.bean.IdCardBackBean;
import com.videocomm.VideoInterView.dlgfragment.PicChooseFragment;
import com.videocomm.VideoInterView.utils.BitmapUtil;
import com.videocomm.VideoInterView.utils.DialogUtil;
import com.videocomm.VideoInterView.utils.HttpUtil;
import com.videocomm.VideoInterView.utils.JsonUtil;
import com.videocomm.VideoInterView.utils.RealPathFromUriUtils;
import com.videocomm.VideoInterView.utils.ToastUtil;
import com.videocomm.VideoInterView.view.ProgressCustom;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/3/30 0030]
 * @function[功能简介 身份验证界面]
 **/
public class IdentityVerifyActivity extends TitleActivity implements View.OnClickListener {

    /**
     * 身份验证显示步骤
     */
    private View stepOne;
    private View stepOneTwo;
    private View stepTwo;

    /**
     * 身份证正反面显示View
     */
    private ImageView ivFrontIdCard;
    private ImageView ivBackIdCard;
    /**
     * 身份证信息Bean
     */
    private IdCardBackBean idCardBackBean;
    private IdCardFrontBean idCardFrontBean;

    /**
     * 身份证信息输入框
     */
    private EditText etName;
    private EditText etIdcard;
    private EditText etSex;
    private EditText etBirth;
    private EditText etNation;
    private EditText etAddress;
    private EditText etSignOffice;
    private EditText etStartTime;
    private EditText etEndTime;
    //自定义节点
    private ProgressCustom progressCustom;


    /**
     * 开户步骤
     */
    private View stepTwoOne;
    private View stepThree;
    private View stepTwoTwo;

    /**
     * 存储由相册获取的前后身份证路径
     */
    private String frontPicPath = "";
    private String backPicPath = "";

    private Dialog mLoadingDialog;//提示框

    private static final int HANDLER_OCR_FRONT_SUCCESS = 1001;
    private static final int HANDLER_OCR_BACK_SUCCESS = 1002;
    private static final int HANDLER_OCR_FRONT_FAILD = 1003;
    private static final int HANDLER_OCR_BACK_FAILD = 1004;
    private Handler mHandler = new IdentityHandler(this);

    class IdentityHandler extends Handler {

        private final IdentityVerifyActivity identityVerifyActivity;

        public IdentityHandler(IdentityVerifyActivity identityVerifyActivity) {
            this.identityVerifyActivity = identityVerifyActivity;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            String content = (String) msg.obj;
            if (content == null) {
                return;
            }
            switch (msg.what) {
                case HANDLER_OCR_FRONT_SUCCESS://OCR识别正面成功
                    if (!content.equalsIgnoreCase("正面身份证上传成功")) {
                        ToastUtil.show("识别失败");
                        mLoadingDialog.dismiss();
                        return;
                    }
                    idCardFrontBean = JsonUtil.jsonToBean(content, IdCardFrontBean.class);
                    if (idCardFrontBean != null && idCardFrontBean.getMsg().equalsIgnoreCase("正面身份证上传成功")) {
                        readBack();
                    } else {
                        ToastUtil.show("识别失败");
                    }
                    break;
                case HANDLER_OCR_BACK_SUCCESS://OCR识别反面成功
                    if (!content.equalsIgnoreCase("反面身份证上传成功")) {
                        ToastUtil.show("识别失败");
                        return;
                    }
                    idCardBackBean = JsonUtil.jsonToBean(content, IdCardBackBean.class);
                    if (idCardBackBean != null && idCardBackBean.getMsg().equalsIgnoreCase("反面身份证上传成功")) {
                        refreshData(HttpUtil.OCR_SIZE_BACK);
                    } else {
                        ToastUtil.show("识别失败");
                    }
                    mLoadingDialog.dismiss();
                    break;
                case HANDLER_OCR_FRONT_FAILD:
                    ToastUtil.show("识别失败");
                    mLoadingDialog.dismiss();
                    break;
                case HANDLER_OCR_BACK_FAILD:
                    ToastUtil.show("识别失败");
                    mLoadingDialog.dismiss();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_verify);
        mTitleLayoutManager.setTitle(R.string.identity_check);
        initView();
        initProgress();
    }

    /**
     * 初始化进度条
     */
    private void initProgress() {
        ArrayList<String> stringData = new ArrayList<>();
        stringData.add("信息采集");
        stringData.add("人脸识别");
        stringData.add("验证完成");
        ArrayList<String> numberData = new ArrayList<>();
        numberData.add("1");
        numberData.add("2");
        numberData.add("3");
        progressCustom = findViewById(R.id.progress_custom);
        progressCustom.setNodeList(stringData);//设置字符数据
        progressCustom.setNodeInList(numberData);//设置圆点中设置
        progressCustom.setSelectIndex(0);//设置选择节点
    }


    /**
     *
     */
    private void initView() {
        /**
         *  step One
         */
        stepOne = findViewById(R.id.step_one);
        ivFrontIdCard = findViewById(R.id.iv_idcard_front_one);
        ivBackIdCard = findViewById(R.id.iv_idcard_back_one);

        /**
         *  step One_Two
         */
        stepOneTwo = findViewById(R.id.step_one_two);
        etName = findViewById(R.id.et_name);
        etIdcard = findViewById(R.id.et_id_card);
        etSex = findViewById(R.id.et_sex);
        etBirth = findViewById(R.id.et_birth);
        etNation = findViewById(R.id.et_nation);
        etAddress = findViewById(R.id.et_id_address);
        etSignOffice = findViewById(R.id.et_sign_office);
        etStartTime = findViewById(R.id.et_start_time);
        etEndTime = findViewById(R.id.et_end_time);

        /**
         *  step Two
         */
        stepTwo = findViewById(R.id.step_two);
        /**
         *  step Two-One
         */
        stepTwoOne = findViewById(R.id.step_two_one);
        /**
         *  step Two-One
         */
        stepTwoTwo = findViewById(R.id.step_two_two);
        /**
         *  step Three
         */
        stepThree = findViewById(R.id.step_three);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_recognition_next://识别身份证
                readFront();
//                stepOne.setVisibility(View.GONE);
//                stepOneTwo.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_info_next://下一步
                progressCustom.setSelectIndex(1);
                stepOneTwo.setVisibility(View.INVISIBLE);
                stepTwo.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_success_next:
//                stepThree.setVisibility(View.GONE);
//                stepTwoTwo.setVisibility(View.VISIBLE);
                startActivity(new Intent(IdentityVerifyActivity.this, ChooseBusinessActivity.class));
                break;
            case R.id.iv_idcard_front_one:
            case R.id.iv_idcard_front_two:
            case R.id.iv_idcard_front_three:
                new PicChooseFragment(Constant.FRONT_VIEW).show(getSupportFragmentManager(), "PicChooseFragment");
                break;
            case R.id.iv_idcard_back_one:
            case R.id.iv_idcard_back_two:
            case R.id.iv_idcard_back_three:
                new PicChooseFragment(Constant.BACK_VIEW).show(getSupportFragmentManager(), "PicChooseFragment");
                break;
            case R.id.btn_start_recognition:
                stepThree.setVisibility(View.VISIBLE);
                stepTwo.setVisibility(View.INVISIBLE);
                break;
        }
    }

    /**
     * 读取身份证正面
     */
    private void readFront() {
        if (frontPicPath.isEmpty() || backPicPath.isEmpty()) {
            ToastUtil.show("请上传图片");
            return;
        }
        Log.d(tag, frontPicPath);
        Log.d(tag, backPicPath);

        mLoadingDialog = DialogUtil.createLoadingDialog(this, getString(R.string.upload_id_card));

        //先识别正面
        HttpUtil.requestOcrPost(HttpUtil.OCR_SIZE_FACE, new File(BitmapUtil.compressImage(frontPicPath)), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(HANDLER_OCR_FRONT_FAILD);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String content = response.body().string();
                Log.d(tag, content);
                Message obtain = Message.obtain();
                obtain.what = HANDLER_OCR_FRONT_SUCCESS;
                obtain.obj = content;
                mHandler.sendMessage(obtain);


            }
        });
    }

    /**
     * 读取身份证背面
     */
    private void readBack() {
        //识别背面
        HttpUtil.requestOcrPost(HttpUtil.OCR_SIZE_BACK, new File(BitmapUtil.compressImage(backPicPath)), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(HANDLER_OCR_BACK_FAILD);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                Log.d(tag, content);
                Message obtain = Message.obtain();
                obtain.what = HANDLER_OCR_BACK_SUCCESS;
                obtain.obj = content;
                mHandler.sendMessage(obtain);
            }
        });
    }

    /**
     * 刷新数据 对用户身份证信息列表进行填充
     */
    private void refreshData(final String type) {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case HttpUtil.OCR_SIZE_FACE://身份证正面
                        //隐藏第一步 显示第二步
                        stepOne.setVisibility(View.GONE);
                        stepOneTwo.setVisibility(View.VISIBLE);
                        etName.setText(idCardFrontBean.getContent().getName());
                        etIdcard.setText(idCardFrontBean.getContent().getIdCardNo());
                        etSex.setText(idCardFrontBean.getContent().getSex());
                        etBirth.setText(idCardFrontBean.getContent().getBirth());
                        etNation.setText(idCardFrontBean.getContent().getNation());
                        etAddress.setText(idCardFrontBean.getContent().getAddress());
                        break;
                    case HttpUtil.OCR_SIZE_BACK://身份证反面
                        etSignOffice.setText(idCardBackBean.getContent().getIssueOrganiz());
                        etStartTime.setText(idCardBackBean.getContent().getIssueDate());
                        etEndTime.setText(idCardBackBean.getContent().getExpiryDate());
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (stepOne.isShown()) {
            finish();
        } else if (stepOneTwo.isShown()) {
            stepOneTwo.setVisibility(View.INVISIBLE);
            stepOne.setVisibility(View.VISIBLE);
        } else if (stepTwo.isShown()) {
            stepTwo.setVisibility(View.INVISIBLE);
            stepOneTwo.setVisibility(View.VISIBLE);
            progressCustom.setSelectIndex(0);
        } else if (stepTwoOne.isShown()) {
            stepTwoOne.setVisibility(View.GONE);
            stepTwo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(tag, "requestCode" + requestCode);
        switch (requestCode) {
            case UCrop.RESULT_ERROR:
                final Throwable cropError = UCrop.getError(data);
                ToastUtil.show(cropError.getLocalizedMessage());
                Log.d(tag, cropError.getMessage());
                break;
            case Constant.FRONT_VIEW_BY_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        return;
                    }
                    Bitmap bitmap = data.getParcelableExtra("bitmap");
                    BitmapUtil.saveBitmap2file(bitmap);
                    ivFrontIdCard.setImageBitmap(bitmap);
                    //保存相片的地址
                    frontPicPath = BitmapUtil.cameraPicPath;
                } else {
                    ToastUtil.show("取消");
                }
                break;
            case Constant.BACK_VIEW_BY_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        return;
                    }
                    Bitmap bitmap = data.getParcelableExtra("bitmap");
                    BitmapUtil.saveBitmap2file(bitmap);
                    ivBackIdCard.setImageBitmap(bitmap);
                    //保存相片的地址
                    backPicPath = BitmapUtil.cameraPicPath;
                } else {
                    ToastUtil.show("取消");
                }
                break;
            case Constant.FRONT_VIEW_BY_PHOTO:
                if (data == null) {
                    return;
                }
                final Uri resultUri = UCrop.getOutput(data);
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri));
                    ivFrontIdCard.setImageBitmap(bitmap);
                    //存储图片地址
                    frontPicPath = RealPathFromUriUtils.getRealPathFromUri(this, resultUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case Constant.BACK_VIEW_BY_PHOTO:
                if (data == null) {
                    return;
                }
                final Uri resultUri1 = UCrop.getOutput(data);
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri1));
                    ivBackIdCard.setImageBitmap(bitmap);
                    //存储图片地址
                    backPicPath = RealPathFromUriUtils.getRealPathFromUri(this, resultUri1);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }
}
