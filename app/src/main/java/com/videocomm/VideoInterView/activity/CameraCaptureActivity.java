package com.videocomm.VideoInterView.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.Constant;
import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.activity.base.BaseActivity;
import com.videocomm.VideoInterView.camera.CameraManager;
import com.videocomm.VideoInterView.utils.BitmapUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/13 0013]
 * @function[功能简介]
 **/
public class CameraCaptureActivity extends BaseActivity implements SurfaceHolder.Callback {
    private boolean isLightState = false;
    private ImageView ivFlashLight;
    private boolean isFront;//Image类型 身份证的正面框 反面框
    private boolean isTakePicture;//记录是否拍照
    private boolean hasSurface;//记录Surface画面是否开启
    public static final int CAMERA_BACK = 0;//后置
    public static final int CAMERA_FRONT = 1;//前置

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);
        initView();
    }

    private void initView() {
        if (getIntent() != null) {
            isFront = getIntent().getBooleanExtra(Constant.IDCARD_IMAGE_TYPE, true);
        }

        ivFlashLight = findViewById(R.id.iv_flash_light);
        ImageView bgIdcard = findViewById(R.id.iv_camera_rect);

        if (isFront) {
            bgIdcard.setBackground(getResources().getDrawable(R.drawable.ic_camera_rect));
        } else {
            bgIdcard.setBackground(getResources().getDrawable(R.drawable.ic_camera_back));
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        Log.d(tag, "takePhoto");
        CameraManager.get().takePicture(null, null, (data, camera) -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            bitmap = Bitmap.createScaledBitmap(bitmap, 400, 300, true);//指定大小压缩
            isTakePicture = false;
            Intent intent = new Intent();
            intent.putExtra("bitmap", bitmap);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_flash_light:
                if (!isLightState) {
                    CameraManager.get().turnLightOn();
                    ivFlashLight.setBackgroundResource(R.drawable.ic_flash_on);
                } else {
                    CameraManager.get().turnLightOff();
                    ivFlashLight.setBackgroundResource(R.drawable.ic_flash_off);
                }
                isLightState = !isLightState;
                break;
            case R.id.iv_red_rect:
            case R.id.iv_capture:
                if (!isTakePicture) {
                    takePhoto();
                    isTakePicture = true;
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * 初始化camera
         */
        CameraManager.init();

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();

        if (hasSurface) {
            // activity在paused时但不会stopped,因此surface仍旧存在；
            // surfaceCreated()不会调用，因此在这里初始化camera
            initCamera(surfaceHolder);
        } else {
            // 重置callback，等待surfaceCreated()来初始化camera
            surfaceHolder.addCallback(this);
        }
    }

    @Override
    protected void onPause() {
        CameraManager.get().stopPreview();
        CameraManager.get().closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();

    }

    public void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            return;
        }
        try {
            /**
             * use a CameraManager to manager the camera's life cycles
             */
            CameraManager.get().openDriver(surfaceHolder, CAMERA_BACK);
            CameraManager.get().setCameraDisplayOrientation(CAMERA_BACK, CameraManager.get().getCurCamera());
            int numberOfCameras = Camera.getNumberOfCameras();
            Log.d(tag, "numberOfCameras" + numberOfCameras);
        } catch (IOException ioe) {
            Log.w(tag, ioe);
            return;
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.e(tag, "Unexpected error initializating camera", e);
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

}
