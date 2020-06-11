package com.videocomm.VideoInterView.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baidu.aip.face.stat.Ast;
import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.IDetectStrategy;
import com.baidu.idl.face.platform.IDetectStrategyCallback;
import com.baidu.idl.face.platform.utils.APIUtils;
import com.baidu.idl.face.platform.utils.Base64Utils;
import com.baidu.idl.face.platform.utils.CameraPreviewUtils;
import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.VideoApplication;
import com.videocomm.VideoInterView.activity.IdentityVerifyActivity;
import com.videocomm.VideoInterView.activity.LoginActivity;
import com.videocomm.VideoInterView.bean.IdentityFaceBean;
import com.videocomm.VideoInterView.bean.TradeInfo;
import com.videocomm.VideoInterView.utils.BitmapUtil;
import com.videocomm.VideoInterView.utils.HttpUtil;
import com.videocomm.VideoInterView.utils.JsonUtil;
import com.videocomm.VideoInterView.utils.StringUtil;
import com.videocomm.VideoInterView.view.ProgressCustom;
import com.videocomm.ai.baidu.ui.utils.CameraUtils;
import com.videocomm.ai.baidu.ui.utils.VolumeUtils;
import com.videocomm.ai.baidu.ui.widget.FaceDetectRoundView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.videocomm.VideoInterView.Constant.FACE_RECO_PIC_PATH;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/5/7 0007]
 * @function[功能简介 人脸识别界面]
 **/
public class FaceDetectFragment extends Fragment implements SurfaceHolder.Callback, VolumeUtils.VolumeCallback, IDetectStrategyCallback, Camera.ErrorCallback, Camera.PreviewCallback {
    private String tag = getClass().getSimpleName();

    private VideoApplication mApplication;
    public static final String DETECT_CONFIG = "FaceOptions";

    // View
    protected View mRootView;
    protected FrameLayout mFrameLayout;
    protected SurfaceView mSurfaceView;
    protected SurfaceHolder mSurfaceHolder;
    protected ImageView mCloseView;
    protected ImageView mSoundView;
    protected ImageView mSuccessView;
    protected TextView mTipsTopView;
    protected TextView mTipsBottomView;
    protected FaceDetectRoundView mFaceDetectRoundView;
    // 人脸信息
    protected FaceConfig mFaceConfig;
    protected IDetectStrategy mIDetectStrategy;
    // 显示Size
    private Rect mPreviewRect = new Rect();
    protected int mDisplayWidth = 0;
    protected int mDisplayHeight = 0;
    protected int mSurfaceWidth = 0;
    protected int mSurfaceHeight = 0;
    protected Drawable mTipsIcon;
    // 状态标识
    protected volatile boolean mIsEnableSound = true;
    protected HashMap<String, String> mBase64ImageMap = new HashMap<String, String>();
    protected boolean mIsCreateSurface = false;
    protected volatile boolean mIsCompletion = false;
    // 相机
    protected Camera mCamera;
    protected Camera.Parameters mCameraParam;
    protected int mCameraId;
    protected int mPreviewWidth;
    protected int mPreviewHight;
    protected int mPreviewDegree;
    // 监听系统音量广播
    protected BroadcastReceiver mVolumeReceiver;
    private ProgressCustom progressCustom;
    protected LinearLayout mImageLayout;

    public FaceDetectFragment(VideoApplication mApplication) {
        this.mApplication = mApplication;
    }

    public FaceDetectFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_face_detect, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        display.getMetrics(dm);
        mDisplayWidth = dm.widthPixels;
        mDisplayHeight = dm.heightPixels;

        com.videocomm.ai.baidu.ui.FaceSDKResSettings.initializeResId();
        mFaceConfig = FaceSDKManager.getInstance().getFaceConfig();

        AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        int vol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        mIsEnableSound = vol > 0 ? mFaceConfig.isSound : false;

        mRootView = view.findViewById(R.id.face_detect_layout);
        mFrameLayout = (FrameLayout) mRootView.findViewById(R.id.detect_surface_layout);

        mSurfaceView = new SurfaceView(getActivity());
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setSizeFromLayout();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        int w = mDisplayWidth;
        int h = mDisplayHeight;

        FrameLayout.LayoutParams cameraFL = new FrameLayout.LayoutParams(
                (int) (w * FaceDetectRoundView.SURFACE_RATIO), (int) (h * FaceDetectRoundView.SURFACE_RATIO),
                Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        mSurfaceView.setLayoutParams(cameraFL);
        mFrameLayout.addView(mSurfaceView);

        mRootView.findViewById(R.id.detect_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mFaceDetectRoundView = (FaceDetectRoundView) mRootView.findViewById(R.id.detect_face_round);
        mCloseView = (ImageView) mRootView.findViewById(R.id.detect_close);
        mSoundView = (ImageView) mRootView.findViewById(R.id.detect_sound);
        mSoundView.setImageResource(mIsEnableSound ?
                R.mipmap.ic_enable_sound_ext : R.mipmap.ic_disable_sound_ext);
        mSoundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsEnableSound = !mIsEnableSound;
                mSoundView.setImageResource(mIsEnableSound ?
                        R.mipmap.ic_enable_sound_ext : R.mipmap.ic_disable_sound_ext);
                if (mIDetectStrategy != null) {
                    mIDetectStrategy.setDetectStrategySoundEnable(mIsEnableSound);
                }
            }
        });
        mTipsTopView = (TextView) mRootView.findViewById(R.id.detect_top_tips);
        mTipsBottomView = (TextView) mRootView.findViewById(R.id.detect_bottom_tips);
        mSuccessView = (ImageView) mRootView.findViewById(R.id.detect_success_image);
        mImageLayout = (LinearLayout) mRootView.findViewById(R.id.detect_result_image_layout);

        if (mBase64ImageMap != null) {
            mBase64ImageMap.clear();
        }
        progressCustom = view.findViewById(R.id.progress_custom);

        TextView tvFaceNameReco = view.findViewById(R.id.tv_face_name_reco);
        String userName = mApplication.getUserName();
        tvFaceNameReco.setText(Html.fromHtml(getResources().getString(R.string.white_red_white, "请确保是 ", StringUtil.replaceStr(userName), " 本人操作")));
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
        progressCustom.setNodeList(stringData);//设置字符数据
        progressCustom.setNodeInList(numberData);//设置圆点中设置
        progressCustom.setSelectIndex(1);//设置选择节点
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPreview();
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mVolumeReceiver = VolumeUtils.registerVolumeReceiver(getActivity(), this);
        if (mTipsTopView != null) {
            mTipsTopView.setText(R.string.detect_face_in);
        }
        startPreview();
    }

    @Override
    public void onStop() {
        super.onStop();
        VolumeUtils.unRegisterVolumeReceiver(getActivity(), mVolumeReceiver);
        mVolumeReceiver = null;
        if (mIDetectStrategy != null) {
            mIDetectStrategy.reset();
        }
        stopPreview();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Camera open() {
        Camera camera;
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            return null;
        }

        int index = 0;
        while (index < numCameras) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(index, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                break;
            }
            index++;
        }

        if (index < numCameras) {
            camera = Camera.open(index);
            mCameraId = index;
        } else {
            camera = Camera.open(0);
            mCameraId = 0;
        }
        return camera;
    }

    protected void startPreview() {
        if (mSurfaceView != null && mSurfaceView.getHolder() != null) {
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(this);
        }

        if (mCamera == null) {
            try {
                mCamera = open();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mCamera == null) {
            return;
        }
        if (mCameraParam == null) {
            mCameraParam = mCamera.getParameters();
        }

        mCameraParam.setPictureFormat(PixelFormat.JPEG);
        int degree = displayOrientation(getActivity());
        mCamera.setDisplayOrientation(degree);
        // 设置后无效，camera.setDisplayOrientation方法有效
        mCameraParam.set("rotation", degree);
        mPreviewDegree = degree;
        if (mIDetectStrategy != null) {
            mIDetectStrategy.setPreviewDegree(degree);
        }

        Point point = CameraPreviewUtils.getBestPreview(mCameraParam,
                new Point(mDisplayWidth, mDisplayHeight));
        mPreviewWidth = point.x;
        mPreviewHight = point.y;
        // Preview 768,432
        mPreviewRect.set(0, 0, mPreviewHight, mPreviewWidth);

        mCameraParam.setPreviewSize(mPreviewWidth, mPreviewHight);
        mCamera.setParameters(mCameraParam);

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.stopPreview();
            mCamera.setErrorCallback(this);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (RuntimeException e) {
            e.printStackTrace();
            CameraUtils.releaseCamera(mCamera);
            mCamera = null;
        } catch (Exception e) {
            e.printStackTrace();
            CameraUtils.releaseCamera(mCamera);
            mCamera = null;
        }

    }

    protected void stopPreview() {
        if (mCamera != null) {
            try {
                mCamera.setErrorCallback(null);
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CameraUtils.releaseCamera(mCamera);
                mCamera = null;
            }
        }
        if (mSurfaceHolder != null) {
            mSurfaceHolder.removeCallback(this);
        }
        if (mIDetectStrategy != null) {
            mIDetectStrategy = null;
        }
    }

    private int displayOrientation(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                degrees = 0;
                break;
        }
        int result = (0 - degrees + 360) % 360;
        if (APIUtils.hasGingerbread()) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }
        }
        return result;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsCreateSurface = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format,
                               int width,
                               int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        if (holder.getSurface() == null) {
            return;
        }
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsCreateSurface = false;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        if (mIsCompletion) {
            return;
        }

        if (mIDetectStrategy == null && mFaceDetectRoundView != null && mFaceDetectRoundView.getRound() > 0) {
            mIDetectStrategy = FaceSDKManager.getInstance().getDetectStrategyModule();
            mIDetectStrategy.setPreviewDegree(mPreviewDegree);
            mIDetectStrategy.setDetectStrategySoundEnable(mIsEnableSound);

            Rect detectRect = FaceDetectRoundView.getPreviewDetectRect(mDisplayWidth, mPreviewHight, mPreviewWidth);
            mIDetectStrategy.setDetectStrategyConfig(mPreviewRect, detectRect, this);
        }
        if (mIDetectStrategy != null) {
            mIDetectStrategy.detectStrategy(data);
        }
    }

    @Override
    public void onError(int error, Camera camera) {
    }

    @Override
    public void onDetectCompletion(FaceStatusEnum status, String message,
                                   HashMap<String, String> base64ImageMap) {
        if (mIsCompletion) {
            return;
        }

        onRefreshView(status, message);

        if (status == FaceStatusEnum.OK) {
            mIsCompletion = true;
            saveImage(base64ImageMap);
        }
        Ast.getInstance().faceHit("detect");
    }

    private void onRefreshView(FaceStatusEnum status, String message) {
        switch (status) {
            case OK:
                onRefreshTipsView(false, message);
                mTipsBottomView.setText("");
                mFaceDetectRoundView.processDrawState(false);
                onRefreshSuccessView(true);
                break;
            case Detect_PitchOutOfUpMaxRange:
            case Detect_PitchOutOfDownMaxRange:
            case Detect_PitchOutOfLeftMaxRange:
            case Detect_PitchOutOfRightMaxRange:
                onRefreshTipsView(true, message);
                mTipsBottomView.setText(message);
                mFaceDetectRoundView.processDrawState(true);
                onRefreshSuccessView(false);
                break;
            case Error_DetectTimeout:
                //检测超时
                showDialog();
                break;
            default:
                onRefreshTipsView(false, message);
                mTipsBottomView.setText("");
                mFaceDetectRoundView.processDrawState(true);
                onRefreshSuccessView(false);
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("检测超时，请问是否重试?")
                .setCancelable(false)
                .setPositiveButton("确定", (dialog, which) -> {
                    IdentityVerifyActivity activity = (IdentityVerifyActivity) getActivity();
                    assert activity != null;
                    activity.restartDetect();
                    dialog.dismiss();
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                    dialog.dismiss();
                });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void onRefreshTipsView(boolean isAlert, String message) {
        if (isAlert) {
            if (mTipsIcon == null) {
                mTipsIcon = getResources().getDrawable(R.mipmap.ic_warning);
                mTipsIcon.setBounds(0, 0, (int) (mTipsIcon.getMinimumWidth() * 0.7f),
                        (int) (mTipsIcon.getMinimumHeight() * 0.7f));
                mTipsTopView.setCompoundDrawablePadding(15);
            }
            mTipsTopView.setBackgroundResource(R.drawable.bg_tips);
            mTipsTopView.setText(R.string.detect_standard);
            mTipsTopView.setCompoundDrawables(mTipsIcon, null, null, null);
        } else {
            mTipsTopView.setBackgroundResource(R.drawable.bg_tips_no);
            mTipsTopView.setCompoundDrawables(null, null, null, null);
            if (!TextUtils.isEmpty(message)) {
                mTipsTopView.setText(message);
            }
        }
    }

    private void onRefreshSuccessView(boolean isShow) {
        if (mSuccessView.getTag() == null) {
            Rect rect = mFaceDetectRoundView.getFaceRoundRect();
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mSuccessView.getLayoutParams();
            rlp.setMargins(
                    rect.centerX() - (mSuccessView.getWidth() / 2),
                    rect.top - (mSuccessView.getHeight() / 2),
                    0,
                    0);
            mSuccessView.setLayoutParams(rlp);
            mSuccessView.setTag("setlayout");
        }
        mSuccessView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    private void saveImage(HashMap<String, String> imageMap) {
        Set<Map.Entry<String, String>> sets = imageMap.entrySet();
        Bitmap bmp = null;
        mImageLayout.removeAllViews();
        for (Map.Entry<String, String> entry : sets) {
            bmp = base64ToBitmap(entry.getValue());
//            ImageView iv = new ImageView(getActivity());
//            iv.setImageBitmap(bmp);
//            mImageLayout.addView(iv, new LinearLayout.LayoutParams(300, 300));
        }
//        IdentityVerifyActivity activity = (IdentityVerifyActivity) getActivity();
//        activity.next(true);
        //人脸识别图片上传到服务器
        BitmapUtil.saveBitmap2file(bmp, FACE_RECO_PIC_PATH);
        File file = new File(BitmapUtil.getPath(FACE_RECO_PIC_PATH));
        if (!file.exists()) {
            return;
        }
        HttpUtil.requestFaceReco(file, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(tag, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                Log.d(tag, content);
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    if (content.contains("人脸识别成功")) {
                        IdentityFaceBean faceBean = JsonUtil.jsonToBean(content, IdentityFaceBean.class);
                        IdentityFaceBean.ContentBean faceBeanContent = faceBean.getContent();
                        //保存数据
                        List<TradeInfo.PicListBean> picList = new ArrayList<>();
                        TradeInfo.PicListBean picListBean = new TradeInfo.PicListBean();
                        picListBean.setPic(faceBeanContent.getFaceImageUrl());
                        picListBean.setType(17);
                        picList.add(picListBean);
                        mApplication.setPicList(picList);
                        IdentityVerifyActivity activity = (IdentityVerifyActivity) getActivity();
                        activity.next(true);
                    }else {
                        IdentityVerifyActivity activity = (IdentityVerifyActivity) getActivity();
                        activity.next(true);
                    }
                });
            }
        });
    }

    private static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64Utils.decode(base64Data, Base64Utils.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public void volumeChanged() {
        try {
            AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {
                int cv = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                mIsEnableSound = cv > 0;
                mSoundView.setImageResource(mIsEnableSound
                        ? R.mipmap.ic_enable_sound_ext : R.mipmap.ic_disable_sound_ext);
                if (mIDetectStrategy != null) {
                    mIDetectStrategy.setDetectStrategySoundEnable(mIsEnableSound);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
