package com.videocomm.VideoInterView.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.VideoApplication;
import com.videocomm.VideoInterView.activity.base.TitleActivity;
import com.videocomm.VideoInterView.utils.DialogFactory;
import com.videocomm.VideoInterView.utils.JsonUtil;
import com.videocomm.VideoInterView.utils.LogUtil;
import com.videocomm.VideoInterView.utils.SpUtil;
import com.videocomm.VideoInterView.utils.ToastUtil;
import com.videocomm.VideoInterView.view.LocalFullScreenCameraPreview;
import com.videocomm.mediasdk.VComMediaSDK;
import com.videocomm.mediasdk.VComSDKEvent;

import static com.videocomm.VideoInterView.Constant.LOCALSCENE_OPEN;
import static com.videocomm.VideoInterView.Constant.mIntLocalAudioClose;
import static com.videocomm.VideoInterView.Constant.mIntLocalAudioOpen;
import static com.videocomm.VideoInterView.Constant.mIntLocalChannelIndex;
import static com.videocomm.VideoInterView.Constant.mIntLocalVideoClose;
import static com.videocomm.VideoInterView.Constant.mIntLocalVideoOpen;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_AIABILITY_ASR_AWORD;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_AIABILITY_EVENT_PROCESSING;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_AIABILITY_EVENT_RESULT;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_AIABILITY_TTS;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_CONFERENCE_ACTIONCODE_EXIT;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_CONFERENCE_ACTIONCODE_JOIN;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_MEDIAFILE_CMD_LOAD;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_MEDIAFILE_CMD_PLAY;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_MEDIAFILE_CMD_STOP;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_MEDIAFILE_CMD_UNLOAD;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_MEDIAFILE_EVENT_LOAD;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_MEDIAFILE_EVENT_STOP;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUEEVENT_ENTERRESULT;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_SDK_PARAM_TYPE_GUID;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_SDK_PARAM_TYPE_LOCALSCENE;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/5/4 0004]
 * @function[功能简介 AI录制界面]
 **/
public class RecordActivity extends TitleActivity implements VComSDKEvent, View.OnClickListener {

    private VComMediaSDK sdkUnit;
    private SurfaceView mLocalSurface;
    private VideoApplication mVideoApplication;
    private Chronometer chronometer;// 计时器
    private ImageButton btnRecord;
    private TextView tvRuleL;
    private TextView tvRuleR;
    private ImageView ivRecording;
    private TextView tvQuestion;

    private String lpUserCode = "";
    private boolean isRecording = false;//记录是否在录制
    private boolean isRecordState = false;//记录录制状态
    private boolean isSuccess = false;//记录是否成功
    private int ansErrorNum = 0;//记录回答错误的次数
    private int iRecordId;//录像ID

    private RecordHandler mHandler = new RecordHandler();
    private static final int HANDLER_RECORDING = 1000;
    private String ASRUuid;//asr 唯一识别码
    private boolean isSelfExit = false;//记录是否自己退出

    private static final int ASK_STATE_ONE = 1;
    private static final int ASK_STATE_TWO = 2;
    private static final int ASK_STATE_THREE = 3;
    private int ASK_STATE = ASK_STATE_ONE;//记录现在提问第几个问题
    private Integer mediafileid;//SDK播放音频ID

    class RecordHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_RECORDING:
                    if (!isRecordState) {
                        ivRecording.setVisibility(View.INVISIBLE);
                    } else {
                        ivRecording.setVisibility(View.VISIBLE);
                    }
                    isRecordState = !isRecordState;
                    sendEmptyMessageDelayed(HANDLER_RECORDING, 1000);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        mTitleLayoutManager.setTitle("AI双录").showLeft(true).setLeftListener(this);
        mVideoApplication = (VideoApplication) getApplication();
        initSDK();
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        chronometer = findViewById(R.id.chronometer);
        ivRecording = findViewById(R.id.iv_recording);
        btnRecord = findViewById(R.id.btn_record);
        tvRuleL = findViewById(R.id.tv_record_rule_l);
        tvRuleR = findViewById(R.id.tv_record_rule_r);
        tvQuestion = findViewById(R.id.tv_question);
        TextView tvRule2 = findViewById(R.id.tv_record_rule_2);
        tvRule2.setText(Html.fromHtml(getResources().getString(R.string.blue_red_blue_red, "根据系统语音提示，回答", " 是 ", "或", " 否 ")));

        btnRecord.setOnClickListener(this);
        //获取唯一识别号
        ASRUuid = sdkUnit.VCOM_GetSDKParamString(7);
    }

    /**
     * 初始化本地视频
     */
    private void initVideo() {
        LocalFullScreenCameraPreview llLocal = findViewById(R.id.ll_local);
        mLocalSurface = new SurfaceView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mLocalSurface.setLayoutParams(params);
        llLocal.addView(mLocalSurface);

        LocalMediaShow mLocalMediaShow = new LocalMediaShow(mLocalSurface, lpUserCode);
        mLocalSurface.getHolder().addCallback(mLocalMediaShow);
    }

    /**
     * 初始化SDK
     */
    private void initSDK() {
        if (sdkUnit == null) {
            sdkUnit = VComMediaSDK.GetInstance();
        }
        sdkUnit.SetSDKEvent(this);

        String userPhone = SpUtil.getInstance().getString(SpUtil.USERPHONE, "VideoInterView");

        sdkUnit.VCOM_SetUserConfig(userPhone, userPhone, "", "", "");
        sdkUnit.VCOM_Login("139.9.171.70:8080", 0, "");
    }

    /**
     * 登陆通知
     */
    @Override
    public void OnLoginSystem(String lpUserCode, int iErrorCode, int iReConnect) {
        this.lpUserCode = lpUserCode;
        Log.i(tag, "OnLoginSystem--lpUserCode:" + lpUserCode + "--iErrorCode:" + iErrorCode + "--iReConnect" + iReConnect);
        mVideoApplication.setUserCode(lpUserCode);
        sdkUnit.VCOM_JoinConference("", "", "");//会议号为空 表示随机
    }

    /**
     * 连接断开通知
     */
    @Override
    public void OnDisconnect(int iErrorCode) {
        Log.i(tag, "OnDisconnect--iErrorCode:" + iErrorCode);
        finish();
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
        if (iAction == VCOM_CONFERENCE_ACTIONCODE_JOIN) {
            initVideo();
        }
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
        if (isSelfExit) {
            return;
        }
        mVideoApplication.setRecordPath(lpFileName);

        Intent intent = new Intent(this, RecordResultActivity.class);
        if (iErrorCode == 0) {
            intent.putExtra("isSuccess", isSuccess);
        } else {
            intent.putExtra("isSuccess", isSuccess);
        }
        startActivity(intent);

        //重新初始化
        mHandler.removeMessages(HANDLER_RECORDING);
        tvRuleL.setVisibility(View.VISIBLE);
        tvRuleR.setVisibility(View.VISIBLE);
        ivRecording.setVisibility(View.INVISIBLE);
        chronometer.setVisibility(View.INVISIBLE);
        btnRecord.setVisibility(View.VISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());//计时器清零
        chronometer.stop();//计时器停止
        isRecording = false;
        ASK_STATE = ASK_STATE_ONE;
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
    }


    // 发送消息回调（用于发送回执）
    @Override
    public void OnSendMessage(int iMsgId, int iErrorCode) {

    }

    // 媒体文件控制回调
    @Override
    public void OnMediaFileControlEvent(int iMediaFileId, int iEventType, int iParam, String lpParam) {
        Log.i(tag, "OnMediaFileControlEvent--iMediaFileId:" + iMediaFileId + "--iEventType:" + iEventType + "--iParam" + iParam + "--lpParam" + lpParam);

        if (iEventType == VCOM_MEDIAFILE_EVENT_STOP) {
            //播放音频结束  停止音频  卸载音频
            sdkUnit.VCOM_MediaFileControl(VCOM_MEDIAFILE_CMD_STOP, iMediaFileId, null);
            sdkUnit.VCOM_MediaFileControl(VCOM_MEDIAFILE_CMD_UNLOAD, iMediaFileId, null);
            //开始ASR识别
            String asr = GenerateASRConfig(mVideoApplication.getUserCode(), 1);
            Log.d(tag, asr);
            sdkUnit.VCOM_AIAbilityControl(VCOM_AIABILITY_ASR_AWORD, asr);

            //3秒后结束ASR识别
            new Handler().postDelayed(() -> {
                String asr1 = GenerateASRConfig(mVideoApplication.getUserCode(), 2);
                Log.d(tag, asr1);
                sdkUnit.VCOM_AIAbilityControl(VCOM_AIABILITY_ASR_AWORD, asr1);
            }, 3000);

        } else if (iEventType == VCOM_MEDIAFILE_EVENT_LOAD) {
            String playResult = sdkUnit.VCOM_MediaFileControl(VCOM_MEDIAFILE_CMD_PLAY, mediafileid, "");
            LogUtil.e(tag, "playResult:" + playResult);
        }
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

    //AI 能力
    @Override
    public void OnAIAbilityEvent(int iEventType, int iErrorCode, String lpUserData) {
        Log.i(tag, "OnAIAbilityEvent--iEventType:" + iEventType + "--iErrorCode:" + iErrorCode + "--lpUserData" + lpUserData);
        sdkUnit.VCOM_StopRecord(iRecordId);
        isSuccess = false;
        switch (iEventType) {
            case VCOM_AIABILITY_EVENT_PROCESSING:
                break;
            case VCOM_AIABILITY_EVENT_RESULT://  TTS / ASR
                String taskid = JsonUtil.jsonToStr(lpUserData, "taskid");
                Log.d(tag, "taskid:" + taskid);

                if (taskid.equalsIgnoreCase(ASRUuid)) {
                    String result = JsonUtil.jsonToStr(lpUserData, "result");
                    Log.d(tag, "result:" + result);
                    //回答不通过条件
                    boolean isAnswerFalse = result.contains("不是") || result.contains("否") || result.contains("不") || result.contains("没有") || result.length() == 0;
                    //回答通过条件
                    boolean isAnswerTrue = result.contains("是的") || result.contains("是") || result.contains("确认") || result.contains("对的") || result.contains("对");

                    //处理回答的信息
                    if (isAnswerFalse) {
                        answerHandler(false);
                    } else if (isAnswerTrue) {
                        answerHandler(true);
                    } else {
                        answerHandler(false);
                    }
                }
            default:
                break;
        }
    }

    /**
     * 回答处理
     *
     * @param ansResult 回答是否通过
     */
    private void answerHandler(boolean ansResult) {
        switch (ASK_STATE) {
            case ASK_STATE_ONE:
                if (ansResult) {
                    //成功  切换到第二问
                    ASK_STATE = ASK_STATE_TWO;
                    ansErrorNum = 0;
                    playTTS(getString(R.string.ai_question_two));
                } else {
                    ansErrorNum++;
                    if (ansErrorNum == 3) {
                        ansErrorNum = 0;
                        isSuccess = false;
                        sdkUnit.VCOM_StopRecord(iRecordId);
                        ToastUtil.show("录制失败");
                    } else {
                        //回答错误
                        ToastUtil.show("回答错误，请重新回答");
                        playTTS(getString(R.string.ai_question_one));
                    }
                }
                break;
            case ASK_STATE_TWO:
                if (ansResult) {
                    //成功  切换到第三问
                    ASK_STATE = ASK_STATE_THREE;
                    ansErrorNum = 0;
                    playTTS(getString(R.string.ai_question_three));
                } else {
                    ansErrorNum++;
                    if (ansErrorNum == 3) {
                        ansErrorNum = 0;
                        isSuccess = false;
                        sdkUnit.VCOM_StopRecord(iRecordId);
                        ToastUtil.show("录制失败");
                    } else {
                        //回答错误
                        ToastUtil.show("回答错误，请重新回答");
                        playTTS(getString(R.string.ai_question_two));
                    }
                }
                break;
            case ASK_STATE_THREE:
                if (ansResult) {
                    //回答正确
                    ASK_STATE = ASK_STATE_ONE;
                    sdkUnit.VCOM_StopRecord(iRecordId);
                    isSuccess = true;
                    ansErrorNum = 0;
                } else {
                    ansErrorNum++;
                    if (ansErrorNum == 3) {
                        ansErrorNum = 0;
                        isSuccess = false;
                        sdkUnit.VCOM_StopRecord(iRecordId);
                        ToastUtil.show("录制失败");
                    } else {
                        //回答错误
                        ToastUtil.show("回答错误，请重新回答");
                        playTTS(getString(R.string.ai_question_three));
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title_left:
                onBackPressed();
                break;
            case R.id.btn_record:
                startRecord();
                break;
            default:
                break;
        }
    }

    /**
     * 开始录制
     */
    private void startRecord() {
        if (!isRecording) {
            String userCode = mVideoApplication.getUserCode();
            String json = GenerateRecordConfig(userCode);
            iRecordId = sdkUnit.VCOM_SetRecordConfig(json, "");
            Log.d(tag, iRecordId + "");
            if (iRecordId != -1) {
                sdkUnit.VCOM_StartRecord(iRecordId);
                tvRuleL.setVisibility(View.INVISIBLE);
                tvRuleR.setVisibility(View.INVISIBLE);
                btnRecord.setVisibility(View.INVISIBLE);
                ivRecording.setVisibility(View.VISIBLE);
                chronometer.setVisibility(View.VISIBLE);
                chronometer.setBase(SystemClock.elapsedRealtime());//计时器清零
                chronometer.start();//开始计时
                mHandler.sendEmptyMessage(HANDLER_RECORDING);//开始闪动

                //开始语音合成 播放问题
                playTTS(getString(R.string.ai_question_one));
                isRecording = true;
            }
        }
    }

    /**
     * 播放TTS
     */
    private void playTTS(String text) {
        tvQuestion.setText(text);
        String tts = GenerateLoadConfig(text);
        Log.d(tag, tts);
        String loadResult = sdkUnit.VCOM_MediaFileControl(VCOM_MEDIAFILE_CMD_LOAD, 0, tts);
        Log.d(tag, loadResult);
        String resultCode = JsonUtil.jsonToStr(loadResult, "errorcode");
        String resultMediafileid = JsonUtil.jsonToStr(loadResult, "mediafileid");
        mediafileid = 0;
        if (!resultCode.equals("0")) {
            return;
        }
        if (!resultMediafileid.equals("")) {
            mediafileid = Integer.parseInt(resultMediafileid.trim());
        }
    }

    /**
     * 生成录像JSON配置
     *
     * @param strUserCode
     * @return
     */
    private String GenerateRecordConfig(String strUserCode) {
        return "{\"audio\":\"true\", \"video\":\"true\", \"bitrate\":0, \"filename\":\"vcomrecord\", \"format\":\"mp4\", \"width\":640, \"height\":480, " +
                "\"mode\":\"one\", \"rewrite\":\"false\", \"serverrecord\":\"false\",\"layout\":[{\"channelindex\":0, \"index\":1, \"usercode\":\"" + strUserCode + "\"}]}";
    }

    /**
     * 生成ASR JSON配置
     *
     * @param userCode
     * @param action
     * @return
     */
    private String GenerateASRConfig(String userCode, int action) {
        return "{\n" +
                "\t\"taskid\":\"" + ASRUuid + "\",\n" +
                "\t\"usercode\": \"" + userCode + "\",\n" +
                "\t\"action\":" + action + "\n" +
                "\t}\n";
    }

    /**
     * 加载TTS Json配置
     *
     * @param text
     * @return
     */
    private String GenerateLoadConfig(String text) {
        String playJson = "{\"channelindex\":5, \"text\":\"" + text + "\"}";
        return playJson;
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

    private void LocalMediaControl(String strUserCode, int iVideo, int iAudio, SurfaceView mSurfaceBig) {
        if ((mIntLocalVideoClose == iVideo) && (mIntLocalAudioClose == iAudio)) {
            sdkUnit.VCOM_CloseLocalMediaStream(mIntLocalChannelIndex, "");

        } else {
            if (iVideo == mIntLocalVideoOpen) {
                sdkUnit.VCOM_SetViewHolder(strUserCode, mIntLocalChannelIndex, mSurfaceBig.getHolder());
            }
            sdkUnit.VCOM_OpenLocalMediaStream(mIntLocalChannelIndex, iVideo, iAudio, "");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //打开自己的音视频
        sdkUnit.VCOM_OpenLocalMediaStream(mIntLocalChannelIndex, mIntLocalVideoOpen, mIntLocalAudioOpen, "");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //关闭自己的音视频
        sdkUnit.VCOM_CloseLocalMediaStream(mIntLocalChannelIndex, "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        sdkUnit.RemoveSDKEvent(this);
    }

    @Override
    public void onBackPressed() {
        DialogFactory.getDialog(DialogFactory.DIALOGID_EXIT_ACT, this, v -> {
            //手动退出
            isSelfExit = true;
            sdkUnit.VCOM_LeaveConference();
            sdkUnit.VCOM_Logout();
            finish();
        }).show();
    }
}
