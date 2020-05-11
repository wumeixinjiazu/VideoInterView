package com.videocomm.VideoInterView.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.activity.base.BaseActivity;
import com.videocomm.VideoInterView.utils.DisplayUtil;
import com.videocomm.VideoInterView.view.LocalFullScreenCameraPreview;
import com.videocomm.mediasdk.VComMediaSDK;
import com.videocomm.mediasdk.VComSDKDefine;
import com.videocomm.mediasdk.VComSDKEvent;

import static com.videocomm.VideoInterView.Constant.mIntLocalAudioClose;
import static com.videocomm.VideoInterView.Constant.mIntLocalAudioOpen;
import static com.videocomm.VideoInterView.Constant.mIntLocalChannelIndex;
import static com.videocomm.VideoInterView.Constant.mIntLocalVideoClose;
import static com.videocomm.VideoInterView.Constant.mIntLocalVideoOpen;
import static com.videocomm.VideoInterView.Constant.mIntRemoteAudioClose;
import static com.videocomm.VideoInterView.Constant.mIntRemoteAudioOpen;
import static com.videocomm.VideoInterView.Constant.mIntRemoteChannelIndex;
import static com.videocomm.VideoInterView.Constant.mIntRemoteVideoClose;
import static com.videocomm.VideoInterView.Constant.mIntRemoteVideoOpen;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_CONFERENCE_ACTIONCODE_JOIN;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_SDK_PARAM_TYPE_WRITELOG;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/22 0022]
 * @function[功能简介]
 **/
public class RiskReportActivity extends BaseActivity implements VComSDKEvent {

    private SurfaceView mLocalSurface;
    private SurfaceView mRemoteSurface;

    private VComMediaSDK sdkUnit;

    String mUserSelfId;//自己的用户id
    private LinearLayout llLocal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_report);
        initView();
        initSDK();
        initLogin();
    }

    private void initLogin() {
        sdkUnit.VCOM_SetUserConfig("VideoInterView", "VideoInterView", "", "", "");
        sdkUnit.VCOM_Login("139.9.171.70:8080", 0, "");
    }

    private void initView() {
        llLocal = findViewById(R.id.ll_local);
        mRemoteSurface = findViewById(R.id.remote_surface);
    }

    /**
     * 初始化SDK
     */
    private void initSDK() {
        sdkUnit = VComMediaSDK.GetInstance();
        sdkUnit.SetSDKEvent(this);
    }

    /**
     * 初始化音视频交互
     */
    private void initAudioVideo() {
        //初始化分辨率
        sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--VCOM_SetVideoParamConfigure--0");
        sdkUnit.VCOM_SetVideoParamConfigure(0, 640, 480, 15, 450, 0);
        sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--VCOM_SetVideoParamConfigure--1");

        //必须在这里新建一个SurfaceView 不然不会出现画面
        mLocalSurface = new SurfaceView(this);
        int heigth = (int) (DisplayUtil.getScreenHeight() / 5);
        int width = heigth * 3 / 4;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mLocalSurface.setLayoutParams(params);
        llLocal.addView(mLocalSurface);

        LocalMediaShow mLocalMediaShow = new LocalMediaShow(mLocalSurface, mUserSelfId);
        mLocalSurface.getHolder().addCallback(mLocalMediaShow);
        mLocalSurface.setZOrderOnTop(true);
    }

    @Override
    public void OnLoginSystem(String lpUserCode, int iErrorCode, int iReConnect) {
        Log.i(tag, "OnLoginSystem--lpUserCode:" + lpUserCode + "--iErrorCode:" + iErrorCode + "--iReConnect" + iReConnect);
        if (iErrorCode == 0) {

            //保存自己的用户id
            mUserSelfId = lpUserCode;

            //进入会议
            sdkUnit.VCOM_JoinConference("123", "", "");

        }
    }

    @Override
    public void OnDisconnect(int i) {

    }

    @Override
    public void OnServerKickout(int i) {

    }

    @Override
    public void OnConferenceResult(int iAction, String lpConfId, int iErrorCode) {
        Log.i(tag, "OnConferenceResult--iAction:" + iAction + "--lpConfId:" + lpConfId + "--iErrorCode" + iErrorCode);
        if (VComSDKDefine.VCOM_CONFERENCE_ACTIONCODE_JOIN == iAction) {
            if (iErrorCode == 0) {
                //初始化 音视频交互
                initAudioVideo();

            }
        }
    }

    @Override
    public void OnConferenceUser(String s, int i, String s1) {

    }

    @Override
    public void OnRemoteVideoData(String s, int i, int i1, long l, byte[] bytes, int i2, int i3, int i4, int i5, int i6) {

    }

    @Override
    public void OnRemoteAudioData(String s, int i, long l, byte[] bytes, int i1, int i2) {

    }

    @Override
    public void OnRecordResult(String s, int i, int i1, String s1, int i2, int i3, String s2, String s3) {

    }

    @Override
    public void OnSnapShotResult(String s, int i, int i1, String s1, String s2, String s3) {

    }

    @Override
    public void OnSendFileStatus(int i, int i1, int i2, String s, long l, int i3, String s1) {

    }

    @Override
    public void OnReceiveMessage(String s, int i, String s1) {

    }

    // 发送消息回调（用于发送回执）
    @Override
    public void OnSendMessage(int iMsgId, int iErrorCode) {

    }

    // 媒体文件控制回调
    @Override
    public void OnMediaFileControlEvent(int iMediaFileId, int iEventType, int iParam, String lpParam) {

    }

    // 媒体资源回调
    @Override
    public void OnMediaResourceResult(String lpResourceGuid, int iErrorCode, int iResourceType, String lpBusinessParam, String lpMd5, String lpUserData) {

    }

    //排队回调
    @Override
    public void OnQueueEvent(int iEventType, int iErrorCode, String lpUserData) {
        Log.i(tag, "OnQueueEvent--iEventType:" + iEventType + "--iErrorCode:" + iErrorCode + "--lpUserData" + lpUserData);

    }

    @Override
    public void OnAIAbilityEvent(int iEventType, int iErrorCode, String lpUserData) {
        Log.i(tag, "OnAIAbilityEvent--iEventType:" + iEventType + "--iErrorCode:" + iErrorCode + "--lpUserData" + lpUserData);

    }

    public class LocalMediaShow implements SurfaceHolder.Callback {
        private final SurfaceView mLocalSurface;
        private final String strUserCode;

        LocalMediaShow(SurfaceView mLocalSurface, String strUserCode) {
            this.mLocalSurface = mLocalSurface;
            this.strUserCode = strUserCode;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            System.out.println("local surfaceChanged");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            System.out.println("local surfaceCreated");
            LocalMediaControl(strUserCode, mIntLocalVideoOpen, mIntLocalAudioOpen, mLocalSurface);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            System.out.println("local surfaceDestroyed");
        }
    }

    public class RemoteMediaShow implements SurfaceHolder.Callback {
        private final String strUserCode;
        private final SurfaceView mSurfaceSmall;

        public RemoteMediaShow(SurfaceView mSurfaceSmall, String strUserCode) {
            this.mSurfaceSmall = mSurfaceSmall;
            this.strUserCode = strUserCode;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            System.out.println("remote surfaceChanged");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            System.out.println("remote surfaceCreated");
            RemoteMediaControl(strUserCode, mIntRemoteVideoOpen, mIntRemoteAudioOpen, mSurfaceSmall);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            System.out.println("remote surfaceDestroyed");
        }
    }

    private void LocalMediaControl(String strUserCode, int iVideo, int iAudio, SurfaceView mSurfaceBig) {
        if ((mIntLocalVideoClose == iVideo) && (mIntLocalAudioClose == iAudio)) {
            sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchL_CloseLocalMediaStream--0");
            sdkUnit.VCOM_CloseLocalMediaStream(mIntLocalChannelIndex, "");
            sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchL_CloseLocalMediaStream--1");

        } else {
            if (iVideo == mIntLocalVideoOpen) {
                sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchL_SetViewHolder-0");
                sdkUnit.VCOM_SetViewHolder(strUserCode, mIntLocalChannelIndex, mSurfaceBig.getHolder());
                sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchL_SetViewHolder--1");
            }
            sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchL_OpenLocalMediaStream--0");
            sdkUnit.VCOM_OpenLocalMediaStream(mIntLocalChannelIndex, iVideo, iAudio, "");
            sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchL_OpenLocalMediaStream--1");
        }
    }

    private void RemoteMediaControl(String strUserCode, int iVideo, int iAudio, SurfaceView mSurfaceSmall) {
        if ((mIntRemoteVideoClose == iVideo) && (mIntRemoteAudioClose == iAudio)) {
            sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchR_CloseRemoteMediaStream--0");
            sdkUnit.VCOM_CloseRemoteMediaStream(strUserCode, mIntRemoteChannelIndex, "");
            sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchR_CloseRemoteMediaStream--1");

        } else {
            if (iVideo == mIntRemoteVideoOpen) {
                sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchR_SetViewHolder--0");
                sdkUnit.VCOM_SetViewHolder(strUserCode, mIntRemoteChannelIndex, mSurfaceSmall.getHolder());
                sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchR_SetViewHolder--1");
            }

            sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchR_GetRemoteMediaStream--0");
            int remoteResult = sdkUnit.VCOM_GetRemoteMediaStream(strUserCode, mIntRemoteChannelIndex, iVideo, iAudio, "");
            sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchR_GetRemoteMediaStream--1");

            Log.i(tag, "remoteResult:" + remoteResult + "-userCode:" + strUserCode);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sdkUnit.VCOM_LeaveConference();
        sdkUnit.VCOM_Logout();
        sdkUnit.RemoveSDKEvent(this);
    }
}
