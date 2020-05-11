package com.videocomm.VideoInterView.camera;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.videocomm.VideoInterView.utils.AppUtil;
import com.videocomm.VideoInterView.utils.Rom;

import java.io.IOException;
import java.util.List;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/14 0014]
 * @function[功能简介 照相机管理者]
 **/
public class CameraManager {

    private static final String TAG = CameraManager.class.getSimpleName();
    private final AutoFocusCallback autoFocusCallback;
    private Camera camera;
    private static CameraManager cameraManager;
    private AutoFocusManager autoFocusManager;
    private boolean previewing;

    private CameraManager() {
        autoFocusCallback = new AutoFocusCallback();
    }

    /**
     * Gets the CameraManager singleton instance.
     *
     * @return A reference to the CameraManager singleton.
     */
    public static CameraManager get() {
        return cameraManager;
    }

    /**
     * Initializes this static object with the Context of the calling Activity.
     */
    public static void init() {
        if (cameraManager == null) {
            cameraManager = new CameraManager();
        }
    }

    /**
     * Opens the camera driver and initializes the hardware parameters.
     *
     * @param holder   The surface object which the camera will draw preview frames
     *                 into.
     * @param cameraId 摄像头的id  0  后置 1  前置
     * @throws IOException Indicates the camera driver failed to open.
     */
    public void openDriver(SurfaceHolder holder, int cameraId) throws IOException {
        camera = null;
        //判断摄像的id是否大于摄像头的数量 如果有前置 后置  getNumberOfCameras ==2
        if (cameraId >= Camera.getNumberOfCameras()) {
            cameraId = 0;
        }
        camera = Camera.open(cameraId);
        if (camera == null) {
            throw new IOException();
        }
        camera.setPreviewDisplay(holder);
        startPreview();
        Camera.Parameters parameters = camera.getParameters();
        if (!Rom.isEmui()) {
            parameters.setPictureSize(800, 600);
        } else {
            float reqRatioPicture = ((float) 600) / 800;
            float curRatioPicture, deltaRatioPicture;
            float deltaRatioMinPicture = Float.MAX_VALUE;
            List<Camera.Size> mPictureSizes = null;
            mPictureSizes = parameters.getSupportedPictureSizes();
            Camera.Size pictureSize = null;
            for (Camera.Size size : mPictureSizes) {
                curRatioPicture = ((float) size.width) / size.height;
                deltaRatioPicture = Math.abs(reqRatioPicture - curRatioPicture);
                if (deltaRatioPicture < deltaRatioMinPicture) {
                    deltaRatioMinPicture = deltaRatioPicture;
                    pictureSize = size;
                }
            }
            parameters.setPictureSize(pictureSize.width, pictureSize.height);
        }
        camera.setParameters(parameters);
    }

    /**
     * Closes the camera driver if still in use.
     */
    public void closeDriver() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    /**
     * Asks the camera hardware to begin drawing preview frames to the screen.
     */
    public void startPreview() {
        if (camera != null && !previewing) {
            camera.startPreview();
            previewing = true;
            autoFocusManager = new AutoFocusManager(camera);
        }
    }

    /**
     * Tells the camera to stop drawing preview frames.
     */
    public void stopPreview() {
        if (autoFocusManager != null) {
            autoFocusManager.stop();
            autoFocusManager = null;
        }

        if (camera != null && previewing) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            previewing = false;
        }
    }

    /**
     * Asks the camera hardware to perform an autofocus.
     *
     * @param handler The Handler to notify when the autofocus completes.
     * @param message The message to deliver.
     */
    public void requestAutoFocus(Handler handler, int message) {

        if (camera != null) {
            autoFocusCallback.setHandler(handler, message);
            // Log.d(TAG, "Requesting auto-focus callback");
            camera.autoFocus(autoFocusCallback);
        }
    }

    public Camera getCurCamera() {
        return camera;
    }

    /**
     * 通过设置Camera打开闪光灯
     */
    public void turnLightOn() {
        if (camera == null) {
            return;
        }
        Camera.Parameters parameters = camera.getParameters();
        if (parameters == null) {
            return;
        }

        List<String> flashModes = parameters.getSupportedFlashModes();
        if (flashModes == null) {
            return;
        }
        String flashMode = parameters.getFlashMode();
        Log.i(TAG, "Flash mode: " + flashMode);
        Log.i(TAG, "Flash modes: " + flashModes);
        // 闪光灯关闭状态
        if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
            // Turn on the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();
            } else {

            }
        }
    }

    /**
     * 通过设置Camera关闭闪光灯
     */
    public void turnLightOff() {
        if (camera == null) {
            return;
        }
        Camera.Parameters parameters = camera.getParameters();
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        // Check if camera flash exists
        if (flashModes == null) {
            return;
        }
        // 闪光灯打开状态
        if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            // Turn off the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
            } else {
                Log.e(TAG, "FLASH_MODE_OFF not supported");
            }
        }
    }

    /**
     * 保证预览方向正确
     *
     * @param cameraId cameraId
     * @param camera   camera
     */
    public void setCameraDisplayOrientation(
            int cameraId, Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
//        int rotation = activity.getWindowManager().getDefaultDisplay()
//                .getRotation();

        WindowManager wm = (WindowManager) AppUtil.getApp().getSystemService("window");
        int rotation = wm.getDefaultDisplay().getRotation();
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
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        //设置角度
        camera.setDisplayOrientation(result);
    }

    /**
     * 拍照
     *
     * @param shutter ShutterCallback
     * @param raw     PictureCallback
     * @param jpeg    PictureCallback
     */
    public synchronized void takePicture(final Camera.ShutterCallback shutter, final Camera.PictureCallback raw,
                                         final Camera.PictureCallback jpeg) {

        camera.takePicture(shutter, raw, jpeg);


    }
}
