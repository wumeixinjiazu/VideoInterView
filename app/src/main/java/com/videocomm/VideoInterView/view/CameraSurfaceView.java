package com.videocomm.VideoInterView.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.videocomm.VideoInterView.camera.CameraManager;
import com.videocomm.VideoInterView.camera.CaptureActivityHandler;
import com.videocomm.VideoInterView.utils.DisplayUtil;

import java.io.IOException;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/16 0016]
 * @function[功能简介 集成了照相机功能的SurfaceView]
 **/
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private boolean hasSurface;//记录Surface画面是否开启
    private String tag = getClass().getSimpleName();
    private CaptureActivityHandler handler;
    public static final int CAMERA_BACK = 0;//后置
    public static final int CAMERA_FRONT = 1;//前置

    private int curCameraId = CAMERA_FRONT;
    private IImageDataCallback mImageDataCallback;

    public CameraSurfaceView(Context context) {
        super(context);
        init();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 切换摄像头 默认前置
     */
    public void switchCameraId() {
        curCameraId = CAMERA_BACK;
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Log.d(tag, "init");
        this.getHolder().addCallback(this);
        hasSurface = false;
        CameraManager.init();
    }

    /**
     * 拍照
     */
    public void takePhoto(Camera.PictureCallback callback) {
        if (CameraManager.get().getCurCamera() != null) {
            CameraManager.get().getCurCamera().takePicture(null, null, callback);
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(tag, "surfaceCreated");
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

    public void initCamera(SurfaceHolder surfaceHolder) {
        try {
            /**
             * use a CameraManager to manager the camera's life cycles
             */
            CameraManager.get().openDriver(surfaceHolder, curCameraId);
            CameraManager.get().setCameraDisplayOrientation(curCameraId, CameraManager.get().getCurCamera());
//            CameraManager.get().getCurCamera().setPreviewCallback(this);
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

    public boolean isHasSurface() {
        return hasSurface;
    }

    public void closeCamera() {
        CameraManager.get().stopPreview();
        CameraManager.get().closeDriver();
    }

    /**
     * onDetachedFromWindow 当activity销毁之后，view会从window上抽离，此时view销毁。
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(tag, "onDetachedFromWindow");
        closeCamera();//关闭相机
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mImageDataCallback != null) {
            mImageDataCallback.onImageData(data, camera);
        }
    }

    public void setImageDataCallback(IImageDataCallback mImageDataCallback) {
        this.mImageDataCallback = mImageDataCallback;
    }

    public interface IImageDataCallback {
        void onImageData(byte[] data, Camera camera);
    }


}
