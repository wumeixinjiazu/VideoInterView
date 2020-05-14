package com.videocomm.VideoInterView.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.VideoApplication;
import com.videocomm.VideoInterView.activity.IdentityVerifyActivity;
import com.videocomm.VideoInterView.bean.IdentityFaceBean;
import com.videocomm.VideoInterView.bean.TradeInfo;
import com.videocomm.VideoInterView.camera.CameraManager;
import com.videocomm.VideoInterView.utils.BitmapUtil;
import com.videocomm.VideoInterView.utils.DisplayUtil;
import com.videocomm.VideoInterView.utils.HttpUtil;
import com.videocomm.VideoInterView.utils.JsonUtil;
import com.videocomm.VideoInterView.utils.StringUtil;
import com.videocomm.VideoInterView.utils.ToastUtil;
import com.videocomm.VideoInterView.view.CameraSurfaceView;
import com.videocomm.VideoInterView.view.FaceScanView;
import com.videocomm.VideoInterView.view.ProgressCustom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.videocomm.VideoInterView.Constant.FACE_RECO_PIC_PATH;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/5/7 0007]
 * @function[功能简介]
 **/
public class FaceRecoFragment extends Fragment {
    private String tag = getClass().getSimpleName();

    private CameraSurfaceView surfaceFaceReco;//人脸识别控件
    private VideoApplication mApplication;
    private boolean isTakePicture;//记录是否拍照
    private ProgressCustom progressCustom;//自定义节点
    private static volatile int mProcessCount = 0;
    private byte[] mImageData;

    private static final int HANDLER_FACE_RECO = 1005;//脸部识别
    private static final int HANDLER_FACE_SUCCESS = 1006;
    private static final int HANDLER_FACE_FAILD = 1007;
    private Handler mFaceRecoHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_FACE_RECO://开始人脸识别
                    if (!isTakePicture) {
                        startFaceReco();
                        isTakePicture = true;
                    }
                    break;
                case HANDLER_FACE_SUCCESS://人脸识别成功返回
                    String content = (String) msg.obj;
                    isTakePicture = false;

                    if (!content.contains("人脸识别成功")) {
                        ToastUtil.show("识别失败,请将脸放置在取景框内");
                        sendEmptyMessageDelayed(HANDLER_FACE_RECO, 1000);
                        return;
                    }
                    IdentityFaceBean faceBean = JsonUtil.jsonToBean(content, IdentityFaceBean.class);
                    IdentityFaceBean.ContentBean faceBeanContent = faceBean.getContent();
                    int completeness = faceBeanContent.getFaceList().get(0).getCompleteness();
                    double blur = faceBeanContent.getFaceList().get(0).getBlur();
                    double occlusion = faceBeanContent.getFaceList().get(0).getOcclusion();
                    if (completeness == 0 || blur > 0.7 || occlusion > 0.6) {
                        ToastUtil.show("识别失败,请将脸放置在取景框内");
                        sendEmptyMessageDelayed(HANDLER_FACE_RECO, 1000);

                    } else {
                        //识别成功
                        //保存数据
                        List<TradeInfo.PicListBean> picList = new ArrayList<>();

                        TradeInfo.PicListBean picListBean = new TradeInfo.PicListBean();
                        picListBean.setPic(faceBeanContent.getFaceImageUrl());
                        picListBean.setType(17);
                        picList.add(picListBean);
                        mApplication.setPicList(picList);

                        IdentityVerifyActivity activity = (IdentityVerifyActivity) getActivity();
                        activity.next();
                    }
                    break;
                case HANDLER_FACE_FAILD://人脸识别失败返回
                    isTakePicture = false;
                    ToastUtil.show("识别失败,请将脸放置在取景框内");
                    break;
                default:
                    break;
            }
        }
    };
    private FaceScanView mFaceScanView;

    public FaceRecoFragment(VideoApplication mApplication) {
        this.mApplication = mApplication;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.actiivty_face_reco, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        surfaceFaceReco = view.findViewById(R.id.surface_face_reco);
        mFaceScanView = view.findViewById(R.id.face_scan_view);
        progressCustom = view.findViewById(R.id.progress_custom);
        TextView tvFaceNameReco = view.findViewById(R.id.tv_face_name_reco);
        tvFaceNameReco.setText(Html.fromHtml(getResources().getString(R.string.black_red_black, "请确保是 ", StringUtil.replaceStr(mApplication.getUserName()), " 本人操作")));

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

    /**
     * 开始人脸识别
     */
    private void startFaceReco() {
        if (surfaceFaceReco == null) {
            return;
        }

        surfaceFaceReco.takePhoto((data, camera) -> {
            Log.d(tag, "onPictureTaken");
            CameraManager.get().getCurCamera().stopPreview();

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Rect scanRect = mFaceScanView.getScanRect();

            int x = scanRect.left;
            int y = scanRect.top;
            int width = scanRect.right - scanRect.left;
            int height = scanRect.bottom - scanRect.top;
            //旋转
            bitmap = BitmapUtil.setRotateAngle(-90, bitmap);
            //指定大小压缩
            bitmap = Bitmap.createScaledBitmap(bitmap, DisplayUtil.getScreenWidth(), surfaceFaceReco.getHeight(), true);
            //截图图片
            bitmap = Bitmap.createBitmap(bitmap, x, y, width, height);

            BitmapUtil.saveBitmap2file(bitmap, FACE_RECO_PIC_PATH);

            CameraManager.get().getCurCamera().startPreview();
            File file = new File(BitmapUtil.getPath(FACE_RECO_PIC_PATH));
            if (!file.exists()) {
                return;
            }
            HttpUtil.requestFaceReco(file, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(tag, e.getMessage());
                    if (mFaceRecoHandler != null) {
                        mFaceRecoHandler.sendEmptyMessage(HANDLER_FACE_FAILD);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String content = response.body().string();
                    Log.d(tag, content);
                    Message obtain = Message.obtain();
                    obtain.obj = content;
                    obtain.what = HANDLER_FACE_SUCCESS;
                    if (mFaceRecoHandler != null) {
                        mFaceRecoHandler.sendMessage(obtain);
                    }
                }
            });
        });
    }

    /**
     * 处理数据
     *
     * @param data
     */
    private void progress(byte[] data) {
        if (mProcessCount > 0)
            return;
        mImageData = data;
//        new FaceProcessRunnable().run();
        ++mProcessCount;
    }

    @Override
    public void onPause() {
        super.onPause();
        surfaceFaceReco.closeCamera();

    }


    @Override
    public void onResume() {
        super.onResume();
        mFaceRecoHandler.sendEmptyMessageDelayed(HANDLER_FACE_RECO, 2000);
        isTakePicture = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        surfaceFaceReco.closeCamera();
        mFaceRecoHandler.removeCallbacksAndMessages(null);
        mFaceRecoHandler = null;
    }

//    private class FaceProcessRunnable implements Runnable {
//
//        @Override
//        public void run() {
//            Log.d(tag, "FaceProcessRunnable");
//            //处理data
//            Camera.Size previewSize = CameraManager.get().getCurCamera().getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
//            YuvImage yuvimage = new YuvImage(
//                    mImageData,
//                    ImageFormat.NV21,
//                    previewSize.width,
//                    previewSize.height,
//                    null);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);// 80--JPG图片的质量[0-100],100最高
//            byte[] rawImage = baos.toByteArray();
//            //将rawImage转换成bitmap
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.RGB_565;
//            Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
//
//            BitmapUtil.saveBitmap2file(bitmap, FACE_RECO_PIC_PATH);
//            File file = new File(BitmapUtil.getPath(FACE_RECO_PIC_PATH));
//            if (!file.exists()) {
//                return;
//            }
//
//            HttpUtil.requestFaceReco(file, new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    Log.d(tag, e.getMessage());
//                    if (mFaceRecoHandler != null) {
//                        mFaceRecoHandler.sendEmptyMessage(HANDLER_FACE_FAILD);
//                    }
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    String content = response.body().string();
//                    Log.d(tag, content);
//                    Message obtain = Message.obtain();
//                    obtain.obj = content;
//                    obtain.what = HANDLER_FACE_SUCCESS;
//                    if (mFaceRecoHandler != null) {
//                        mFaceRecoHandler.sendMessage(obtain);
//                    }
//                }
//            });
//            --mProcessCount;
//        }
//    }

}
