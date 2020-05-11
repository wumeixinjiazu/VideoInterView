package com.videocomm.VideoInterView.camera;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.activity.CameraCaptureActivity;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/14 0014]
 * @function[功能简介 照相机Actiivty 的消息处理者]
 **/
public class CaptureActivityHandler extends Handler {
    @Override
    public void handleMessage(@NonNull Message message) {
        if (message.what == R.id.auto_focus) {
            // Log.d(TAG, "Got auto-focus message");
            // When one auto focus pass finishes, start another. This is the
            // closest thing tos
            // continuous AF. It does seem to hunt a bit, but I'm not sure what
            // else to do.
            if (CameraManager.get().getCurCamera() != null) {
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            }
        }
    }

    public CaptureActivityHandler() {
        restartPreviewAndDecode();
    }

    private void restartPreviewAndDecode() {
        CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
    }
}
