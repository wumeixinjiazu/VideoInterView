package com.videocomm.VideoInterView.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.VideoApplication;
import com.videocomm.VideoInterView.activity.base.BaseActivity;
import com.videocomm.VideoInterView.utils.DialogFactory;
import com.videocomm.VideoInterView.utils.JsonUtil;
import com.videocomm.VideoInterView.utils.StringUtil;
import com.videocomm.VideoInterView.utils.ToastUtil;
import com.videocomm.mediasdk.VComMediaSDK;
import com.videocomm.mediasdk.VComSDKEvent;

import java.util.Timer;
import java.util.TimerTask;

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
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_CLIP_MODE_MAXIMUMAREA;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_CLIP_MODE_SHRINK;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_CONFERENCE_ACTIONCODE_EXIT;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_CONFERENCE_ACTIONCODE_JOIN;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_MEDIAFILE_EVENT_STOP;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUECTRL_HANGUPVIDEO;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUEEVENT_HANGUPVIDEO;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_SDK_PARAM_TYPE_CLIPMODE;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_SDK_PARAM_TYPE_WRITELOG;


public class VideoActivity extends BaseActivity implements
        OnClickListener, OnTouchListener, VComSDKEvent {

    private String tag = getClass().getSimpleName();

    private Dialog dialog;
    private TextView mTxtTime;
    private Button mBtnEndSession;
    private SurfaceView mSurfaceSelf;
    private SurfaceView mSurfaceOther;
    private LinearLayout llSurfaceOther;
    private LinearLayout llSurfaceRemote;
    private SurfaceView mSurfaceRemote;

    private Handler mHandler;
    private TimerTask mTimerTask;
    private Timer mTimerShowVideoTime;

    private VComMediaSDK sdkUnit;
    private VideoApplication mVideoApplication;

    private int videocallSeconds = 0;
    private int iChannle;//风险播放流（第三人）
    private static final int MAX_PEOPLE = 1;//最大人数
    private int currentPeople = 0;//当前人数
    private String targetUserName = "";//对方用户名
    private boolean isHasOther = false;//是否有其他人（第三人）
    private static final int MSG_TIMEUPDATE = 2;//更新消息
    private boolean isConnectRemote = false;//记录是否连接远程成功(第二个人)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //去掉标题栏；
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //初始化sdk
        initSdk();
    }

    private void initSdk() {
        String confid = getIntent().getStringExtra("confid");
        if (confid == null) {
            finish();
        }

        if (sdkUnit == null) {
            sdkUnit = VComMediaSDK.GetInstance();
        }
        sdkUnit.SetSDKEvent(this);
        //加入会议
        sdkUnit.VCOM_JoinConference(confid, "", "");
    }

    /**
     * 初始化视频 本地和远程
     */
    private void initLocalVideo() {
        //设置裁剪
        sdkUnit.VCOM_SetSDKParamInt(VCOM_SDK_PARAM_TYPE_CLIPMODE, VCOM_CLIP_MODE_MAXIMUMAREA);

        //设置分辨率 默认 640 * 480
        sdkUnit.VCOM_SetVideoParamConfigure(0, 640, 480, 15, 450, 0);

        //设置本地视频
        LocalMediaShow mLocalMediaShow = new LocalMediaShow(mSurfaceSelf, mVideoApplication.getUserCode());
        mSurfaceSelf.getHolder().addCallback(mLocalMediaShow);
        mSurfaceSelf.setZOrderOnTop(true);
    }

    /**
     * 初始化远程视频
     */
    private void initRemoteVideo() {
        if (currentPeople < MAX_PEOPLE) {
            mSurfaceRemote = new SurfaceView(this);
            llSurfaceRemote.addView(mSurfaceRemote);
            //设置远程视频
            targetUserName = mVideoApplication.getTargetUserName();
            RemoteMediaShow mRemoteMediaShow = new RemoteMediaShow(mSurfaceRemote, targetUserName, mIntRemoteChannelIndex, mIntRemoteVideoOpen, mIntRemoteAudioOpen);
            mSurfaceRemote.getHolder().addCallback(mRemoteMediaShow);

            isConnectRemote = true;
            currentPeople += 1;
        }


    }

    /**
     * 更新时间
     */
    private void updateTime() {

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_TIMEUPDATE:
                        mTxtTime.setText(StringUtil
                                .getTimeShowString(videocallSeconds++));
                        break;
                }
            }
        };
        initTimerShowTime();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //用户切回来的时候走这个方法
        //这时候要对用户的音视频进行打开操作
        Log.i(tag, "onRestart");

        //打开自己的音视频
        sdkUnit.VCOM_OpenLocalMediaStream(mIntLocalChannelIndex, mIntLocalVideoOpen, mIntLocalAudioOpen, "");

        if (isConnectRemote) {
            //打开其他其他用户的音视频
            sdkUnit.VCOM_GetRemoteMediaStream(targetUserName, mIntRemoteChannelIndex, mIntRemoteVideoOpen, mIntRemoteVideoOpen, "");
        }

        if (isHasOther) {
            //关闭第三方流
            sdkUnit.VCOM_GetRemoteMediaStream(targetUserName, iChannle, mIntRemoteVideoOpen, mIntRemoteVideoOpen, "");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //关闭自己的音视频
        sdkUnit.VCOM_CloseLocalMediaStream(mIntLocalChannelIndex, "");
        if (isConnectRemote) {
            //关闭其他其他用户的音视频
            sdkUnit.VCOM_CloseRemoteMediaStream(targetUserName, mIntRemoteChannelIndex, "");
        }
        if (isHasOther) {
            //关闭第三方流
            sdkUnit.VCOM_CloseRemoteMediaStream(targetUserName, iChannle, "");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //挂断
        sdkUnit.VCOM_QueueControl(VCOM_QUEUECTRL_HANGUPVIDEO, "");
        sdkUnit.VCOM_LeaveConference();
        sdkUnit.VCOM_Logout();
        sdkUnit.RemoveSDKEvent(this);
        mVideoApplication.setTargetUserName("");//清空坐席的用户名
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        super.onDestroy();
    }

    private void initTimerShowTime() {
        if (mTimerShowVideoTime == null)
            mTimerShowVideoTime = new Timer();
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_TIMEUPDATE);
            }
        };
        mTimerShowVideoTime.schedule(mTimerTask, 100, 1000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            alertDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("WrongViewCast")
    private void initView() {
        this.setContentView(R.layout.activity_video);

        mVideoApplication = (VideoApplication) getApplication();
        llSurfaceOther = findViewById(R.id.ll_surface_other);
        llSurfaceRemote = findViewById(R.id.ll_surface_remote);
        mSurfaceSelf = findViewById(R.id.surface_local);
        mTxtTime = findViewById(R.id.txt_time);
        mBtnEndSession = findViewById(R.id.btn_endsession);

        mBtnEndSession.setOnClickListener(this);
        mSurfaceSelf.setOnClickListener(this);
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_endsession:
                alertDialog();
                break;
            case R.id.surface_local:
//                switchPreview(R.id.surface_local);
                break;
            default:
                break;
        }
    }

    private void alertDialog() {

        dialog = DialogFactory.getDialog(DialogFactory.DIALOGID_ENDCALL, this, v -> {
            startActivity(new Intent(VideoActivity.this, LoginActivity.class));
            finish();
            ToastUtil.show("正在结束视频通话...");
        });
        dialog.show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void OnLoginSystem(String lpUserCode, int iErrorCode, int iReConnect) {
        Log.i(tag, "OnLoginSystem--lpUserCode:" + lpUserCode + "--iErrorCode:" + iErrorCode + "--iReConnect" + iReConnect);
    }

    /**
     * 连接断开通知
     */
    @Override
    public void OnDisconnect(int iErrorCode) {
        Log.i(tag, "OnDisconnect--iErrorCode:" + iErrorCode);
    }

    /**
     * 服务器踢人通知
     */
    @Override
    public void OnServerKickout(int iErrorCode) {
        Log.i(tag, "OnServerKickout--iErrorCode:" + iErrorCode);
        ToastUtil.show("已在别处登录");
        startActivity(new Intent(VideoActivity.this, LoginActivity.class));
    }

    /**
     * 进出会议室通知
     */
    @Override
    public void OnConferenceResult(int iAction, String lpConfId, int iErrorCode) {
        Log.i(tag, "OnConferenceResult--iAction:" + iAction + "--lpConfId:" + lpConfId + "--iErrorCode" + iErrorCode);
        if (iAction == VCOM_CONFERENCE_ACTIONCODE_JOIN) {
            //自己加入会议成功后 开启本地流
            //初始化布局
            initView();
            //初始化视频
            initLocalVideo();
        }

    }

    /**
     * 其他用户进出会议室通知
     */
    @Override
    public void OnConferenceUser(String lpUserCode, int iAction, String lpConfId) {
        Log.i(tag, "OnConferenceUser--lpUserCode:" + lpUserCode + "--iAction:" + iAction + "--lpConfId" + lpConfId);
        if (iAction == VCOM_CONFERENCE_ACTIONCODE_EXIT) {
            ToastUtil.show(lpUserCode + "用户已退出");
        } else if (iAction == VCOM_CONFERENCE_ACTIONCODE_JOIN) {
            //其他用户进来后 打开远程用户流
            initRemoteVideo();
        }
    }

    @Override
    public void OnRemoteVideoData(String lpUserCode, int iChannelIndex, int iFrameType, long i64Timestamp, byte[] lpBuf, int iSizeInByte, int iWidth, int iHeight, int iFlags, int iRotation) {
        Log.i(tag, "OnRemoteVideoData--lpUserCode:" + lpUserCode + "--iChannelIndex:" + iChannelIndex + "--iFrameType" + iFrameType);
        Log.i(tag, "OnRemoteVideoData--i64Timestamp:" + i64Timestamp + "--lpBuf:" + lpBuf + "--iSizeInByte" + iSizeInByte);
        Log.i(tag, "OnRemoteVideoData--iWidth:" + iWidth + "--iHeight:" + iHeight + "--iFlags" + iFlags + "--iRotation:" + iRotation);
    }

    @Override
    public void OnRemoteAudioData(String lpUserCode, int iChannelIndex, long i64Timestamp, byte[] lpBuf, int iSizeInByte, int iFlags) {
        Log.i(tag, "OnRemoteAudioData--lpUserCode:" + lpUserCode + "--iChannelIndex:" + iChannelIndex + "--i64Timestamp" + i64Timestamp);
        Log.i(tag, "OnRemoteAudioData--lpBuf:" + lpBuf + "--iSizeInByte:" + iSizeInByte + "--iFlags" + iFlags);
    }

    @Override
    public void OnRecordResult(String lpUserCode, int iRecordId, int iErrorCode, String lpFileName, int iFileLength, int iDuration, String lpMD5, String lpBusinessParam) {
        Log.i(tag, "OnRecordResult--iRecordId:" + iRecordId + "--iErrorCode:" + iErrorCode + "--lpFileName" + lpFileName);
        Log.i(tag, "OnRecordResult--iFileLength:" + iFileLength + "--iDuration:" + iDuration + "--lpMD5" + lpMD5 + "--lpBusinessParam" + lpBusinessParam);
    }

    @Override
    public void OnSnapShotResult(String lpUserCode, int iChannelIndex, int iErrorCode, String lpFileName, String lpBusinessParam, String lpExtParam) {
        Log.i(tag, "OnSnapShotResult--lpUserCode:" + lpUserCode + "--iChannelIndex:" + iChannelIndex + "--iErrorCode" + iErrorCode + "--lpBusinessParam" + lpBusinessParam + "--lpExtParam" + lpExtParam);
    }

    @Override
    public void OnSendFileStatus(int iHandle, int iErrorCode, int iProgress, String lpFileName, long iFileLength, int iFlags, String lpParam) {
        Log.i(tag, "OnSendFileStatus--iHandle:" + iHandle + "--iErrorCode:" + iErrorCode + "--iProgress" + iProgress + "--iFileLength" + iFileLength + "--iFlags" + iFlags + "--lpParam" + lpParam);
    }

    @Override
    public void OnReceiveMessage(String lpUserCode, int iMsgType, String lpMessage) {
        Log.i(tag, "OnReceiveMessage--lpUserCode:" + lpUserCode + "--iMsgType:" + iMsgType + "--lpMessage" + lpMessage);
        //接收来自Web端的消息（消息自定义） 开始风险播报
        String status = JsonUtil.jsonToStr(lpMessage, "status");
        if (status.equals("0")) {
            String streamIndex = JsonUtil.jsonToStr(lpMessage, "mStreamIndex");
            if (streamIndex != null) {
                //设置裁剪
                sdkUnit.VCOM_SetSDKParamInt(VCOM_SDK_PARAM_TYPE_CLIPMODE, VCOM_CLIP_MODE_SHRINK);
                //设置横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                //设置裁剪模式
                iChannle = Integer.parseInt(streamIndex);
                //设置第三人开启
                isHasOther = true;
                //开启风险播放流
                if (mSurfaceOther == null) {
                    mSurfaceOther = new SurfaceView(VideoActivity.this);
                    llSurfaceOther.addView(mSurfaceOther, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                RemoteMediaControl(targetUserName, mIntRemoteVideoOpen, mIntRemoteAudioOpen, iChannle, mSurfaceOther);
                //切换布局
                switchParentPreview();
            }
        }
    }

    // 发送消息回调（用于发送回执）
    @Override
    public void OnSendMessage(int iMsgId, int iErrorCode) {

    }

    // 媒体文件控制回调
    @Override
    public void OnMediaFileControlEvent(int iMediaFileId, int iEventType, int iParam, String lpParam) {
        ToastUtil.show("播放完成");
        //风险播报完毕
        if (iEventType == VCOM_MEDIAFILE_EVENT_STOP) {
            ToastUtil.show("播放完成");
        }

    }

    // 媒体资源回调
    @Override
    public void OnMediaResourceResult(String lpResourceGuid, int iErrorCode, int iResourceType, String lpBusinessParam, String lpMd5, String lpUserData) {

    }

    @Override
    public void OnQueueEvent(int iEventType, int iErrorCode, String lpUserData) {
        Log.i(tag, "OnQueueEvent--iEventType:" + iEventType + "--iErrorCode:" + iErrorCode + "--lpUserData" + lpUserData);
        if (iEventType == VCOM_QUEUEEVENT_HANGUPVIDEO) {
            //用户 坐席挂断
            ToastUtil.show("视频通话已结束...");
            startActivity(new Intent(VideoActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void OnAIAbilityEvent(int i, int i1, String s) {

    }

    public class LocalMediaShow implements SurfaceHolder.Callback {
        private final SurfaceView mSurfaceBig;
        private final String strUserCode;

        LocalMediaShow(SurfaceView mSurfaceBig, String strUserCode) {
            this.mSurfaceBig = mSurfaceBig;
            this.strUserCode = strUserCode;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            System.out.println("local surfaceChanged");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            System.out.println("local surfaceCreated");
            LocalMediaControl(strUserCode, mIntLocalVideoOpen, mIntLocalAudioOpen, mSurfaceBig);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            System.out.println("local surfaceDestroyed");
        }
    }

    public class RemoteMediaShow implements SurfaceHolder.Callback {
        private final String strUserCode;
        private final SurfaceView mSurfaceView;
        private final int iChannle;
        private final int iVideo;
        private final int iAudio;

        /**
         * @param mSurfaceView 需要显示的控件
         * @param strUserCode  用户名
         * @param iChannle     通道流
         * @param iVideo       是否打开视频
         * @param iAudio       是否打开音频
         */
        RemoteMediaShow(SurfaceView mSurfaceView, String strUserCode, int iChannle, int iVideo, int iAudio) {
            this.mSurfaceView = mSurfaceView;
            this.strUserCode = strUserCode;
            this.iChannle = iChannle;
            this.iVideo = iVideo;
            this.iAudio = iAudio;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            System.out.println("remote surfaceChanged");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            System.out.println("remote surfaceCreated");
            RemoteMediaControl(strUserCode, iVideo, iAudio, iChannle, mSurfaceView);
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

    private void RemoteMediaControl(String strUserCode, int iVideo, int iAudio, int iChannle, SurfaceView mSurfaceSmall) {
        if ((mIntRemoteVideoClose == iVideo) && (mIntRemoteAudioClose == iAudio)) {
            sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchR_CloseRemoteMediaStream--0");
            sdkUnit.VCOM_CloseRemoteMediaStream(strUserCode, iChannle, "");
            sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchR_CloseRemoteMediaStream--1");

        } else {
            if (iVideo == mIntRemoteVideoOpen) {
                sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchR_SetViewHolder--0");
                sdkUnit.VCOM_SetViewHolder(strUserCode, iChannle, mSurfaceSmall.getHolder());
                sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchR_SetViewHolder--1");
            }

            sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchR_GetRemoteMediaStream--0");
            int remoteResult = sdkUnit.VCOM_GetRemoteMediaStream(strUserCode, iChannle, iVideo, iAudio, "");
            sdkUnit.VCOM_SetSDKParamString(VCOM_SDK_PARAM_TYPE_WRITELOG, "Android--SwitchR_GetRemoteMediaStream--1");

            Log.i(tag, "remoteResult:" + remoteResult + "-userCode:" + strUserCode);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            sdkUnit.VCOM_SetCameraDisplayOriOrientation();
        } else {
            sdkUnit.VCOM_SetCameraDisplayOriOrientation();
        }
    }

    /**
     * 切换父控件布局参数已达到两个视频切换
     */
    private void switchParentPreview() {
        LinearLayout remoteLayout = findViewById(R.id.ll_surface_remote);
        LinearLayout otherLayout = findViewById(R.id.ll_surface_other);

        ViewGroup.LayoutParams remoteLayoutParams = remoteLayout.getLayoutParams();
        ViewGroup.LayoutParams otherLayoutParams = otherLayout.getLayoutParams();

        remoteLayout.setLayoutParams(otherLayoutParams);

        otherLayout.setLayoutParams(remoteLayoutParams);

        mSurfaceRemote.setZOrderOnTop(true);
    }


}
