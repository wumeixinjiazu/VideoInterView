package com.videocomm.VideoInterView.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.videocomm.VideoInterView.Config;
import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.VideoApplication;
import com.videocomm.VideoInterView.activity.base.TitleActivity;
import com.videocomm.VideoInterView.bean.IdCardFrontBean;
import com.videocomm.VideoInterView.bean.IdCardBackBean;
import com.videocomm.VideoInterView.bean.TradeInfo;
import com.videocomm.VideoInterView.dlgfragment.PicChooseFragment;
import com.videocomm.VideoInterView.fragment.FaceDetectFragment;
import com.videocomm.VideoInterView.fragment.FaceRecoFragment;
import com.videocomm.VideoInterView.utils.BitmapUtil;
import com.videocomm.VideoInterView.utils.DialogUtil;
import com.videocomm.VideoInterView.utils.FileUtil;
import com.videocomm.VideoInterView.utils.HttpUtil;
import com.videocomm.VideoInterView.utils.JsonUtil;
import com.videocomm.VideoInterView.utils.SpUtil;
import com.videocomm.VideoInterView.utils.StringUtil;
import com.videocomm.VideoInterView.utils.ToastUtil;
import com.videocomm.VideoInterView.view.ProgressCustom;
import com.videocomm.ai.baidu.ui.FaceDetectActivity;
import com.videocomm.ai.baidu.ui.FaceLivenessActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.videocomm.VideoInterView.Constant.FACE_RECO_PIC_PATH;
import static com.videocomm.VideoInterView.Constant.OPEN_CAMERA;
import static com.videocomm.VideoInterView.Constant.PHOTO_BACK_PATH;
import static com.videocomm.VideoInterView.Constant.PHOTO_FRONT_PATH;
import static com.videocomm.VideoInterView.Constant.PHOTO_REQUEST_CODE;
import static com.videocomm.VideoInterView.Constant.RESULT_CODE_DETECT_ACT;
import static com.videocomm.VideoInterView.Constant.RESULT_CODE_IDENTITY_ACT;
import static com.videocomm.VideoInterView.Constant.TAKE_PIC_BACK_PATH;
import static com.videocomm.VideoInterView.Constant.TAKE_PIC_FRONT_PATH;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/3/30 0030]
 * @function[功能简介 身份验证界面]
 **/
public class IdentityVerifyActivity extends TitleActivity implements View.OnClickListener {

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

    /**
     * 身份验证显示步骤
     */
    private View stepOne;
    private View stepOneTwo;
    private View stepTwo;
    private View stepThree;

    /**
     * 存储由相册获取的前后身份证路径
     */
    private String frontPicPath = "";
    private String backPicPath = "";

    private Dialog mLoadingDialog;//提示框
    private ProgressCustom progressCustom;//自定义节点

    private Handler mHandler = new IdentityHandler(this);
    private static final int HANDLER_OCR_FRONT_SUCCESS = 1001;//身份证识别
    private static final int HANDLER_OCR_BACK_SUCCESS = 1002;
    private static final int HANDLER_OCR_FRONT_FAILD = 1003;
    private static final int HANDLER_OCR_BACK_FAILD = 1004;

    private TextView tvFaceName;
    private boolean flag = true;//记录是不是身份证正面
    private VideoApplication mApplication;
    private ImageView ivIdentityState;
    private TextView tvIdentityState;
    private FaceDetectFragment detectFragment;

    static class IdentityHandler extends Handler {

        private final IdentityVerifyActivity activity;

        IdentityHandler(IdentityVerifyActivity identityVerifyActivity) {
            this.activity = identityVerifyActivity;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            String content = (String) msg.obj;

            switch (msg.what) {
                case HANDLER_OCR_FRONT_SUCCESS://OCR识别正面成功
                    if (!content.contains("正面身份证上传成功")) {
                        sendEmptyMessage(HANDLER_OCR_FRONT_FAILD);
                        return;
                    }
                    activity.idCardFrontBean = JsonUtil.jsonToBean(content, IdCardFrontBean.class);
                    if (activity.idCardFrontBean != null && activity.idCardFrontBean.getMsg().contains("正面身份证上传成功")) {
                        activity.readBack();
                    } else {
                        ToastUtil.show("识别失败");
                        sendEmptyMessage(HANDLER_OCR_FRONT_FAILD);
                    }
                    break;
                case HANDLER_OCR_BACK_SUCCESS://OCR识别反面成功
                    if (!content.contains("反面身份证上传成功")) {
                        sendEmptyMessage(HANDLER_OCR_BACK_FAILD);
                        return;
                    }
                    activity.idCardBackBean = JsonUtil.jsonToBean(content, IdCardBackBean.class);
                    if (activity.idCardBackBean != null && activity.idCardBackBean.getMsg().contains("反面身份证上传成功")) {
                        activity.refreshData();
                        activity.mLoadingDialog.dismiss();
                    } else {
                        sendEmptyMessage(HANDLER_OCR_BACK_FAILD);
                    }
                    break;
                case HANDLER_OCR_FRONT_FAILD:
                case HANDLER_OCR_BACK_FAILD:
                    ToastUtil.show("识别失败");
                    activity.mLoadingDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_verify);
        mApplication = (VideoApplication) getApplication();
        mTitleLayoutManager.setTitle(R.string.identity_check);
        initView();
        initProgress();
        initLib();
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

        findViewById(R.id.iv_idcard_front_two);
        findViewById(R.id.iv_idcard_front_three);

        findViewById(R.id.iv_idcard_back_two);
        findViewById(R.id.iv_idcard_back_three);

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
        tvFaceName = findViewById(R.id.tv_face_name);

        /**
         *  step Three
         */
        stepThree = findViewById(R.id.step_three);
        ivIdentityState = findViewById(R.id.iv_identity_state);
        tvIdentityState = findViewById(R.id.tv_identity_state);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_recognition_next://识别身份证
                readFront();
//                stepOneTwo.setVisibility(View.VISIBLE);
//                stepOne.setVisibility(View.GONE);
                break;
            case R.id.btn_info_next://下一步
                checkData();
                break;
            case R.id.btn_success_next://身份验证成功
                startActivity(new Intent(IdentityVerifyActivity.this, ChooseBusinessActivity.class));
                finish();
                break;
            case R.id.iv_idcard_front_one:
            case R.id.iv_idcard_front_two:
            case R.id.iv_idcard_front_three:
                flag = true;
                selectFunction(flag);
                break;
            case R.id.iv_idcard_back_one:
            case R.id.iv_idcard_back_two:
            case R.id.iv_idcard_back_three:
                flag = false;
                selectFunction(flag);
                break;
            case R.id.btn_start_recognition://开始人脸识别
                stepTwo.setVisibility(View.GONE);
                setFaceConfig();
                detectFragment = new FaceDetectFragment(mApplication);
                getSupportFragmentManager().beginTransaction().add(R.id.content, detectFragment).show(detectFragment).commit();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化SDK
     */
    private void initLib() {
        // 应用上下文
        // 申请License取得的APPID
        // assets目录下License文件名
        FaceSDKManager.getInstance().initialize(this, Config.licenseID, Config.licenseFileName);
        // setFaceConfig();
    }

    private void setFaceConfig() {
        FaceConfig config = FaceSDKManager.getInstance().getFaceConfig();
        // SDK初始化已经设置完默认参数（推荐参数），您也根据实际需求进行数值调整
        //设置活体动作
        List<LivenessTypeEnum> actionList = new ArrayList<>();
        actionList.add(LivenessTypeEnum.Eye);
        actionList.add(LivenessTypeEnum.Mouth);
        actionList.add(LivenessTypeEnum.HeadDown);
        config.setLivenessTypeList(actionList);
        //设置活体动作是否随机
        config.setLivenessRandom(false);
        //设置模糊度范围 推荐小于0.7
        config.setBlurnessValue(FaceEnvironment.VALUE_BLURNESS);
        //设置光照范围
        config.setBrightnessValue(FaceEnvironment.VALUE_BRIGHTNESS);
        //设置裁剪人脸大小
        config.setCropFaceValue(FaceEnvironment.VALUE_CROP_FACE_SIZE);
        //设置人脸yaw，pitch，row 角度，范围（-45.，45）
        config.setHeadYawValue(FaceEnvironment.VALUE_HEAD_YAW);
        config.setHeadPitchValue(FaceEnvironment.VALUE_HEAD_PITCH);
        config.setHeadRollValue(FaceEnvironment.VALUE_HEAD_ROLL);
        //最小检测人脸（在图片人脸能够被检测到最小值）80-200，越小越耗性能 推荐120-200
        config.setMinFaceSize(FaceEnvironment.VALUE_MIN_FACE_SIZE);
        //
        config.setNotFaceValue(FaceEnvironment.VALUE_NOT_FACE_THRESHOLD);
        //设置人脸遮挡范围（0-1）推荐小于0.5
        config.setOcclusionValue(FaceEnvironment.VALUE_OCCLUSION);
        //设置是否检测人脸质量
        config.setCheckFaceQuality(true);
        //设置人脸检测使用线程数
        config.setFaceDecodeNumberOfThreads(2);
        //设置是否开启提示音
        config.setSound(true);

        //设置参数生效
        FaceSDKManager.getInstance().setFaceConfig(config);
    }

    /**
     * 选择功能
     *
     * @param flag 区分身份证是正面还是反面
     */
    private void selectFunction(boolean flag) {
        //推荐使用setArguments 来传递参数
        PicChooseFragment fragment = PicChooseFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFront", flag);
        fragment.setArguments(bundle);
        if (flag) {
            fragment.show(getSupportFragmentManager(), "front");
        } else {
            fragment.show(getSupportFragmentManager(), "back");
        }
    }

    /**
     * 检查数据
     */
    private void checkData() {
        if (TextUtils.isEmpty(etName.getText().toString()) ||
                TextUtils.isEmpty(etIdcard.getText().toString()) ||
                TextUtils.isEmpty(etSex.getText().toString()) ||
                TextUtils.isEmpty(etBirth.getText().toString()) ||
                TextUtils.isEmpty(etNation.getText().toString()) ||
                TextUtils.isEmpty(etAddress.getText().toString()) ||
                TextUtils.isEmpty(etSignOffice.getText().toString()) ||
                TextUtils.isEmpty(etStartTime.getText().toString()) ||
                TextUtils.isEmpty(etEndTime.getText().toString())) {
            ToastUtil.show("信息不能为空！");
        } else {
            //正确
            //保存身份证信息
            mApplication.setUserName(etName.getText().toString());
            mApplication.setIdcardNum(etIdcard.getText().toString());
            if (etSex.getText().toString().contains("男")) {
                mApplication.setUserSex(0);
            } else {
                mApplication.setUserSex(1);
            }
            mApplication.setIdcardBirth(etBirth.getText().toString());
            mApplication.setIdcardNation(etNation.getText().toString());
            mApplication.setIdcardAddress(etAddress.getText().toString());
            mApplication.setIdcardSignOrganization(etSignOffice.getText().toString());
            mApplication.setIdcardVaildTime(etStartTime.getText().toString());
            mApplication.setIdcardInvaildTime(etEndTime.getText().toString());

            progressCustom.setSelectIndex(1);
            stepTwo.setVisibility(View.VISIBLE);
            tvFaceName.setText(Html.fromHtml(getResources().getString(R.string.black_red_black, "请确保是 ", StringUtil.replaceStr(mApplication.getUserName()), " 本人操作")));
            stepOneTwo.setVisibility(View.GONE);
        }
    }

    /**
     * 下一步 根据活体检测是否打开
     */
    public void next() {
        getSupportFragmentManager().beginTransaction().remove(detectFragment).commit();

        boolean livingState = SpUtil.getInstance().getBoolean(SpUtil.LIVINGCHECKSTATE, true);
        if (livingState) {
            //开始活体检测
            initLivenessAI();
        } else {
            //验证成功
            stepThree.setVisibility(View.VISIBLE);
            progressCustom.setSelectIndex(2);
        }
        saveData();
    }

    /**
     * 初始化AI
     */
    private void initLivenessAI() {
        boolean livingState = SpUtil.getInstance().getBoolean(SpUtil.LIVINGCHECKSTATE, true);
        if (!livingState) {
            return;
        }
        setFaceConfig();
        startActivityForResult(new Intent(this, FaceLivenessActivity.class), RESULT_CODE_IDENTITY_ACT);
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
        File file = new File(frontPicPath);
        if (!file.exists()) {
            return;
        }
        mLoadingDialog = DialogUtil.createLoadingDialog(this, getString(R.string.upload_id_card));

        //先识别正面
        HttpUtil.requestOcrPost(HttpUtil.OCR_SIZE_FACE, file, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(HANDLER_OCR_FRONT_FAILD);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String content = response.body().string();
                Log.d(tag, content);
                Message obtain = Message.obtain();
                obtain.what = HANDLER_OCR_FRONT_SUCCESS;
                obtain.obj = content;
                if (mHandler != null) {
                    mHandler.sendMessage(obtain);
                }
            }
        });
    }

    /**
     * 读取身份证背面
     */
    private void readBack() {
        File file = new File(backPicPath);
        if (!file.exists()) {
            return;
        }
        //识别背面
        HttpUtil.requestOcrPost(HttpUtil.OCR_SIZE_BACK, file, new Callback() {
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
    private void refreshData() {
        runOnUiThread(() -> {
            //隐藏第一步 显示第二步
            stepOne.setVisibility(View.GONE);
            stepOneTwo.setVisibility(View.VISIBLE);
            //获取数据列表
            IdCardFrontBean.ContentBean frontBeanContent = idCardFrontBean.getContent();
            IdCardBackBean.ContentBean backBeanContent = idCardBackBean.getContent();
            //设置数据
            etName.setText(frontBeanContent.getName());
            etIdcard.setText(frontBeanContent.getIdCardNo());
            etSex.setText(frontBeanContent.getSex());
            etBirth.setText(frontBeanContent.getBirth());
            etNation.setText(frontBeanContent.getNation());
            etAddress.setText(frontBeanContent.getAddress());
            etSignOffice.setText(backBeanContent.getIssueOrganiz());
            etStartTime.setText(backBeanContent.getIssueDate());
            etEndTime.setText(backBeanContent.getExpiryDate());
        });
    }

    /**
     * 保存数据
     */
    private void saveData() {
        if (idCardBackBean == null || idCardFrontBean == null) {
            return;
        }
        //获取数据
        IdCardFrontBean.ContentBean frontBeanContent = idCardFrontBean.getContent();
        IdCardBackBean.ContentBean backBeanContent = idCardBackBean.getContent();
        //保存数据
        TradeInfo.PicListBean picListBean = new TradeInfo.PicListBean();
        picListBean.setPic(frontBeanContent.getFrontIdCardUrl());
        picListBean.setType(15);
        TradeInfo.PicListBean picListBean1 = new TradeInfo.PicListBean();
        picListBean1.setPic(backBeanContent.getBackIdCardUrl());
        picListBean1.setType(16);

        mApplication.getPicList().add(picListBean);
        mApplication.getPicList().add(picListBean1);
    }

    @Override
    public void onBackPressed() {
        //以下仅测试调用
//        if (stepOne.isShown()) {
//            finish();
//        } else if (stepOneTwo.isShown()) {
//            stepOneTwo.setVisibility(View.INVISIBLE);
//            stepOne.setVisibility(View.VISIBLE);
//        } else if (stepTwo.isShown()) {
//            stepTwo.setVisibility(View.INVISIBLE);
//            stepOneTwo.setVisibility(View.VISIBLE);
//            progressCustom.setSelectIndex(0);
//        } else if (stepTwoOne.isShown()) {
//            stepTwoOne.setVisibility(View.GONE);
//            surfaceFaceReco.closeCamera();
//            stepTwo.setVisibility(View.VISIBLE);
//        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(tag, "requestCode" + requestCode);
        switch (requestCode) {
//            case UCrop.RESULT_ERROR:
//                final Throwable cropError = UCrop.getError(data);
//                ToastUtil.show(cropError.getLocalizedMessage());
//                Log.d(tag, cropError.getMessage());
//                break;
//            case Constant.FRONT_VIEW_BY_PHOTO://裁剪之后的正面图片
//                if (data == null) {
//                    return;
//                }
//                final Uri resultUri = UCrop.getOutput(data);
//                try {
//                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri));
//                    ivFrontIdCard.setImageBitmap(bitmap);
//                    //存储图片地址
//                    frontPicPath = RealPathFromUriUtils.getRealPathFromUri(this, resultUri);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                break;
//            case Constant.BACK_VIEW_BY_PHOTO://裁剪之后的背面图片
//                if (data == null) {
//                    return;
//                }
//                final Uri resultUri1 = UCrop.getOutput(data);
//                try {
//                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri1));
//                    ivBackIdCard.setImageBitmap(bitmap);
//                    //存储图片地址
//                    backPicPath = RealPathFromUriUtils.getRealPathFromUri(this, resultUri1);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                break;
            case RESULT_CODE_IDENTITY_ACT://活体检测结果返回
                if (data == null) {
                    return;
                }
                boolean isSuccess = data.getBooleanExtra("isSuccess", false);
                stepThree.setVisibility(View.VISIBLE);
                progressCustom.setSelectIndex(2);
                if (isSuccess) {
                    //验证成功
                    ivIdentityState.setBackgroundResource(R.drawable.ic_result_true);
                    tvIdentityState.setText("活体检测成功");
                } else {
                    //活体失败
                    ivIdentityState.setBackgroundResource(R.drawable.ic_result_false);
                    tvIdentityState.setText("活体检测失败");
                    finish();
                }
                break;
            case RESULT_CODE_DETECT_ACT://人脸识别结果返回
                if (data == null) {
                    return;
                }
                boolean isDetectSuccess = data.getBooleanExtra("isSuccess", false);
                if (isDetectSuccess) {
                    boolean livingState = SpUtil.getInstance().getBoolean(SpUtil.LIVINGCHECKSTATE, true);
                    if (livingState) {
                        initLivenessAI();
                    } else {
                        //验证成功
                        stepThree.setVisibility(View.VISIBLE);
                        ivIdentityState.setBackgroundResource(R.drawable.ic_result_true);
                        tvIdentityState.setText("验证成功");
                        progressCustom.setSelectIndex(2);
                    }
                } else {
                    //活体失败
                    ivIdentityState.setBackgroundResource(R.drawable.ic_result_false);
                    tvIdentityState.setText("活体检测失败");
                    finish();
                }
                break;
            case OPEN_CAMERA://拍照返回
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        return;
                    }
                    imgShowTake(data);

                } else {
                    ToastUtil.show("取消");
                }
                break;
            case PHOTO_REQUEST_CODE://打开系统相册回调
                if (resultCode == Activity.RESULT_OK) {
                    if (null != data.getData()) {

                        Uri uri = data.getData();

                        //设置图片显示
                        imgShow(uri);

                        //图片裁剪
//                        destinationUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "croppedImage" + this.requestCode + ".jpg"));
//                        UCrop.of(uri, destinationUri)
//                                .withAspectRatio(16, 9)
//                                .withMaxResultSize(400, 300)
//                                .start(this, this.requestCode + Constant.OPEN_PHOTO);
                    } else {
                        ToastUtil.show("图片损坏，请重新选择");
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 展示拍照的照片
     *
     * @param data
     */
    private void imgShowTake(Intent data) {
        if (flag) {
            Bitmap bitmap = data.getParcelableExtra("bitmap");
            BitmapUtil.saveBitmap2file(bitmap, TAKE_PIC_FRONT_PATH);
            ivFrontIdCard.setImageBitmap(bitmap);
            //保存相片的地址
            frontPicPath = BitmapUtil.getPath(TAKE_PIC_FRONT_PATH);
        } else {
            Bitmap bitmap = data.getParcelableExtra("bitmap");
            BitmapUtil.saveBitmap2file(bitmap, TAKE_PIC_BACK_PATH);
            ivBackIdCard.setImageBitmap(bitmap);
            //保存相片的地址
            backPicPath = BitmapUtil.getPath(TAKE_PIC_BACK_PATH);
        }
    }

    /**
     * 图片展示
     *
     * @param uri
     */
    private void imgShow(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapUtil.getBitmapFormUri(this, uri);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Log.d(tag, width + "");
            Log.d(tag, height + "");
            if (bitmap == null) {
                return;
            }
            if (flag) {
                //正面
                //保存图片到本地
                BitmapUtil.saveBitmap2file(bitmap, PHOTO_FRONT_PATH);
                //存储图片地址
                frontPicPath = BitmapUtil.getPath(PHOTO_FRONT_PATH);
                ivFrontIdCard.setImageBitmap(bitmap);
            } else {
                //反面
                //保存图片到本地
                BitmapUtil.saveBitmap2file(bitmap, PHOTO_BACK_PATH);
                //存储图片地址
                backPicPath = BitmapUtil.getPath(PHOTO_BACK_PATH);
                ivBackIdCard.setImageBitmap(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        deletePic();
    }

    /**
     * 删除图片(删除之前 上传身份证的图片或者拍照压缩的图片)
     */
    private void deletePic() {
        FileUtil.deleteFile(BitmapUtil.getPath(PHOTO_FRONT_PATH));
        FileUtil.deleteFile(BitmapUtil.getPath(PHOTO_BACK_PATH));
        FileUtil.deleteFile(BitmapUtil.getPath(TAKE_PIC_FRONT_PATH));
        FileUtil.deleteFile(BitmapUtil.getPath(TAKE_PIC_BACK_PATH));
        FileUtil.deleteFile(BitmapUtil.getPath(FACE_RECO_PIC_PATH));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(tag, "onSaveInstanceState");
        //保存数据 提供给activity意外销毁时恢复数据
        outState.putBoolean("flag", flag);
        outState.putString("front", frontPicPath);
        outState.putString("back", backPicPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(tag, "onRestoreInstanceState");
        //恢复由于activity意外销毁的数据
        flag = savedInstanceState.getBoolean("flag");
        frontPicPath = savedInstanceState.getString("front");
        backPicPath = savedInstanceState.getString("back");
        Log.d(tag, "frontPicPath" + frontPicPath);
        Log.d(tag, "backPicPath" + frontPicPath);

        if (frontPicPath.length() > 0) {
            Bitmap frontBitmap = BitmapUtil.getBitmapFromFile(frontPicPath);
            ivFrontIdCard.setImageBitmap(frontBitmap);
        }

        if (backPicPath.length() > 0) {
            Bitmap backBitmap = BitmapUtil.getBitmapFromFile(backPicPath);
            ivBackIdCard.setImageBitmap(backBitmap);
        }
    }
}
