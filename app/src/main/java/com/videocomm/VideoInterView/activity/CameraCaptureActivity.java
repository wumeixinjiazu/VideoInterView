package com.videocomm.VideoInterView.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.Constant;
import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.activity.base.BaseActivity;
import com.videocomm.VideoInterView.camera.CameraManager;
import com.videocomm.VideoInterView.camera.CaptureActivityHandler;
import com.videocomm.VideoInterView.utils.DisplayUtil;
import com.videocomm.VideoInterView.view.CameraSurfaceView;
import com.yalantis.ucrop.UCrop;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/13 0013]
 * @function[功能简介]
 **/
public class CameraCaptureActivity extends BaseActivity {
    private boolean isLightState = false;
    private ImageView ivFlashLight;
    private CameraSurfaceView surfaceCamera;
    private int imageType;//Image类型 身份证的正面框 反面框

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);
        imageType = getIntent().getIntExtra(Constant.IDCARD_IMAGE_TYPE, Constant.FRONT_VIEW);
        initView();
    }

    private void initView() {
        ivFlashLight = findViewById(R.id.iv_flash_light);
        ImageView bgIdcard = findViewById(R.id.iv_camera_rect);
        surfaceCamera = findViewById(R.id.surface_camera);
        surfaceCamera.switchCameraId();

        switch (imageType){
            case Constant.FRONT_VIEW:
                bgIdcard.setBackground(getResources().getDrawable(R.drawable.ic_camera_rect));
                break;
            case Constant.BACK_VIEW:
                bgIdcard.setBackground(getResources().getDrawable(R.drawable.ic_camera_back));
                break;
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        Log.d(tag, "takePhoto");
        surfaceCamera.takePhoto(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 300, true);//指定大小压缩
                Intent intent = new Intent();
                intent.putExtra("bitmap", bitmap);
                setResult(RESULT_OK, intent);
                finish();
            }
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
                takePhoto();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (surfaceCamera.isHasSurface()) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            surfaceCamera.initCamera(surfaceCamera.getHolder());
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            Log.e("CaptureActivity", "onResume");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        surfaceCamera.closeCamera();
    }
}
