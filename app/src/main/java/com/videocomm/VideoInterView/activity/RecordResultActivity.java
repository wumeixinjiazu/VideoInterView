package com.videocomm.VideoInterView.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.VideoApplication;
import com.videocomm.VideoInterView.activity.base.TitleActivity;
import com.videocomm.VideoInterView.bean.TradeInfo;
import com.videocomm.VideoInterView.utils.BitmapUtil;
import com.videocomm.VideoInterView.utils.DialogUtil;
import com.videocomm.VideoInterView.utils.JsonUtil;
import com.videocomm.VideoInterView.utils.SpUtil;
import com.videocomm.VideoInterView.utils.StringUtil;
import com.videocomm.VideoInterView.utils.ToastUtil;
import com.videocomm.mediasdk.VComMediaSDK;
import com.videocomm.mediasdk.VComSDKEvent;

import java.io.File;
import java.util.Random;

import static com.videocomm.VideoInterView.Constant.LOCALSCENE_CLOSE;
import static com.videocomm.VideoInterView.Constant.LOCALSCENE_OPEN;
import static com.videocomm.VideoInterView.Constant.RESULT_CODE_Record_Result_ACT;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_CONFERENCE_ACTIONCODE_EXIT;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_SDK_PARAM_TYPE_LOCALSCENE;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_SENDFILE_CODE_START;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/5/5 0005]
 * @function[功能简介]
 **/
public class RecordResultActivity extends TitleActivity implements View.OnClickListener, VComSDKEvent {

    private Dialog mUploadDialog;
    private ImageView ivPlay;
    private ImageView ivFirstFrame;
    private VideoView mVideoView;
    private VideoApplication mVideoApplication;
    private Button btnQueue;
    private View fileUploadRoot;
    private TextView tvFaild;
    private TextView tvSuccess;
    private Button btnAgain;
    private TextView tvUploadState;
    private VComMediaSDK sdkUnit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitleLayoutManager.setTitle("AI双录");
        setContentView(R.layout.activity_record_result);
        initView();
        sdkUnit = VComMediaSDK.GetInstance();
        sdkUnit.SetSDKEvent(this);
    }

    private void initView() {

        mVideoApplication = (VideoApplication) getApplication();
        tvFaild = findViewById(R.id.tv_faild_reason);
        tvSuccess = findViewById(R.id.tv_success_reason);
        mVideoView = findViewById(R.id.videoview);
        ivFirstFrame = findViewById(R.id.iv_first_frame);
        ivPlay = findViewById(R.id.iv_play);
        btnAgain = findViewById(R.id.btn_record_again);
        btnQueue = findViewById(R.id.btn_start_queue);
        fileUploadRoot = findViewById(R.id.file_upload_root);
        tvUploadState = findViewById(R.id.tv_upload_state);

        btnAgain.setOnClickListener(this);
        btnQueue.setOnClickListener(this);
        ivFirstFrame.setOnClickListener(this);

        //录制是否成功
        boolean isSuccess = getIntent().getBooleanExtra("isSuccess", false);
        if (isSuccess) {
            btnQueue.setText("提交视频");
            tvFaild.setVisibility(View.GONE);
            tvSuccess.setVisibility(View.VISIBLE);
        } else {
            btnQueue.setText("转人工客服");
            tvFaild.setVisibility(View.VISIBLE);
            tvSuccess.setVisibility(View.GONE);
        }

        getVideoFirstFrame("");
    }

    private void initPlay() {
        //加载指定的视频文件
        mVideoView.setVideoPath(mVideoApplication.getRecordPath());
        //创建MediaController对象
        MediaController mediaController = new MediaController(this);

        //VideoView与MediaController建立关联
        mVideoView.setMediaController(mediaController);

        //开始播放
        mVideoView.start();

        //让VideoView获取焦点
        mVideoView.requestFocus();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record_again://重新录制
                finish();
                break;
            case R.id.btn_start_queue://开始排队/转人工服务
                switch (btnQueue.getText().toString()) {
                    case "转人工客服":
                        sdkUnit.VCOM_LeaveConference();
                        //设置临柜模式
                        sdkUnit.VCOM_SetSDKParamInt(VCOM_SDK_PARAM_TYPE_LOCALSCENE, LOCALSCENE_CLOSE);
                        break;
                    case "提交视频":
                        fileUpload();
                        break;
                    default:
                        break;
                }
                break;
            case R.id.iv_first_frame:
                ivPlay.setVisibility(View.GONE);
                ivFirstFrame.setVisibility(View.GONE);
                initPlay();
                break;
            default:
                break;
        }
    }

    /**
     * 文件上传
     */
    private void fileUpload() {
        mVideoView.setVisibility(View.GONE);
        tvSuccess.setVisibility(View.GONE);
        tvFaild.setVisibility(View.GONE);
        btnAgain.setVisibility(View.GONE);
        btnQueue.setVisibility(View.GONE);
        fileUploadRoot.setVisibility(View.VISIBLE);

        String recordPath = mVideoApplication.getRecordPath();
        if (recordPath == null) {
            return;
        }
        int uploadResult = sdkUnit.VCOM_SetSendFileConfig(recordPath, "", "");
        if (uploadResult > 0) {
            int sendFileResult = sdkUnit.VCOM_SendFileControl(uploadResult, VCOM_SENDFILE_CODE_START, 0, "");
            Log.d(tag, "sendFileResult:" + sendFileResult);
            mUploadDialog = DialogUtil.createUploadDialog(this, "0%");
        }
    }

    /**
     * 获取视频第一帧并设置
     *
     * @param videoUrl
     */
    public void getVideoFirstFrame(String videoUrl) {
        if (!TextUtils.isEmpty(videoUrl)) {
            File file = new File(videoUrl);
            if (file.exists()) {
                ivFirstFrame.setImageBitmap(BitmapUtil.getVideoThumbnail(
                        videoUrl, MediaStore.Images.Thumbnails.MINI_KIND));
            }
        }
    }

    /**
     * 上传数据到后台
     */
    public void onUploadTradeInfo() {

        String tradNo = StringUtil.getCurrentFormatTime();
        Random random = new Random();
        int num = random.nextInt(900) + 100;
        tradNo = tradNo + num;

        TradeInfo info = new TradeInfo();
        info.setAppId(SpUtil.getInstance().getString(SpUtil.APPID));
        info.setUserCode(mVideoApplication.getUserCode());
        info.setIdcardNum(mVideoApplication.getIdcardNum());
        info.setUserPhone(SpUtil.getInstance().getString(SpUtil.USERPHONE));
        info.setUserName(mVideoApplication.getUserName());
        info.setUserSex(mVideoApplication.getUserSex());
        info.setIdcardAddress(mVideoApplication.getIdcardAddress());
        info.setIdcardBirth(mVideoApplication.getIdcardBirth());
        info.setIdcardVaildTime(mVideoApplication.getIdcardVaildTime());
        info.setIdcardInvaildTime(mVideoApplication.getIdcardInvaildTime());
        info.setIdcardSignOrganization(mVideoApplication.getIdcardSignOrganization());
        info.setIdcardNation(mVideoApplication.getIdcardNation());
        info.setPicList(mVideoApplication.getPicList());
        info.setExInfos(mVideoApplication.getExInfos());
        info.setTradeNo(tradNo);
        String data = JsonUtil.beanToString(info);
//        HashMap<String, String> map = new HashMap<String, String>();
//        map.put("data", jsonObject.toString());
        Log.d(tag, "data:---" + data);
    }

    /**
     * 登陆通知
     */
    @Override
    public void OnLoginSystem(String lpUserCode, int iErrorCode, int iReConnect) {
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
    }

    /**
     * 进出会议室通知
     */
    @Override
    public void OnConferenceResult(int iAction, String lpConfId, int iErrorCode) {
        Log.i(tag, "OnConferenceResult--iAction:" + iAction + "--lpConfId:" + lpConfId + "--iErrorCode" + iErrorCode);
        if (iAction == VCOM_CONFERENCE_ACTIONCODE_EXIT) {
            //退出会议 进入排队界面
            Intent intent = new Intent(RecordResultActivity.this, QueueActivity.class);
            startActivityForResult(intent, RESULT_CODE_Record_Result_ACT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(tag, "onActivityResult" + requestCode);
        sdkUnit.VCOM_JoinConference("", "", "");
    }

    /**
     * 其他用户进出会议室通知
     */
    @Override
    public void OnConferenceUser(String lpUserCode, int iAction, String lpConfId) {
        Log.i(tag, "OnConferenceUser--lpUserCode:" + lpUserCode + "--iAction:" + iAction + "--lpConfId" + lpConfId);
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
        DialogUtil.setProgress(iProgress + "%");
        if (iProgress == 100 && iErrorCode == 0) {
            mUploadDialog.dismiss();
            ToastUtil.show("上传成功");
            tvUploadState.setText("上传成功");

            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("isSuccess", true);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void OnReceiveMessage(String lpUserCode, int iMsgType, String lpMessage) {
        Log.i(tag, "OnReceiveMessage--lpUserCode:" + lpUserCode + "--iMsgType:" + iMsgType + "--lpMessage" + lpMessage);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sdkUnit.RemoveSDKEvent(this);
    }
}
