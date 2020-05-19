package com.videocomm.VideoInterView.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.VideoApplication;
import com.videocomm.VideoInterView.activity.base.TitleActivity;
import com.videocomm.VideoInterView.bean.QueueBean;
import com.videocomm.VideoInterView.bean.QueueStateBean;
import com.videocomm.VideoInterView.bean.TradeInfo;
import com.videocomm.VideoInterView.utils.DialogFactory;
import com.videocomm.VideoInterView.utils.JsonUtil;
import com.videocomm.VideoInterView.utils.SpUtil;
import com.videocomm.VideoInterView.utils.StringUtil;
import com.videocomm.VideoInterView.utils.ToastUtil;
import com.videocomm.mediasdk.VComMediaSDK;
import com.videocomm.mediasdk.VComSDKDefine;
import com.videocomm.mediasdk.VComSDKEvent;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUECTRL_ENTERQUEUE;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUECTRL_LEAVEQUEUE;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUECTRL_QUERYQUEUEINFO;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUECTRL_QUREYQUEUELENGTH;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUEEVENT_AGENTSERVICE;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUEEVENT_HANGUPVIDEO;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUEEVENT_QUERYQUEUEINFO;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUEEVENT_QUREYQUEUELENGTH;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUEEVENT_STARTVIDEO;

public class QueueActivity extends TitleActivity implements VComSDKEvent {
    private Button quickButton;
    private TextView showTextView;
    private ImageButton mImgBtnReturn;
    private TextView timeshow;
    private final int TIME_UPDATE = 291;        //Handler发送消息,队列人数的实时更新

    private VComMediaSDK sdkUnit;
    private String tag = getClass().getSimpleName();
    private QueueStateBean queueStateBean;
    private Timer timer;
    private Handler mHandler;
    private Dialog dialog;
    private VideoApplication mVideoApplication;
    private QueueBean queueBean;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //自定义标题栏
        setContentView(R.layout.activity_queue);

        //初始化SDK
        initSdk();
        //初始化布局
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        //全局变量类的对象初始化
        mVideoApplication = (VideoApplication) getApplication();
        //标题栏的设置
        mTitleLayoutManager.setTitle(mVideoApplication.getSelectBussiness() + "-排队等待中");
        showTextView = (TextView) findViewById(R.id.queue_show);
        //实时更新显示时间
        timeshow = (TextView) findViewById(R.id.queue_time);
        String targetUserName = mVideoApplication.getTargetUserName();
        if (targetUserName.length() > 0) {
            //表示 用户还没进入队列之前 坐席已经示闲（等待用户）
            mTitleLayoutManager.setTitle("呼叫坐席" + targetUserName);
            showTextView.setVisibility(View.GONE);
            timeshow.setVisibility(View.GONE);
        }

        mHandler = new Handler() {


            @Override
            public void handleMessage(Message msg) {
                if (msg.what == TIME_UPDATE) {
                    sdkUnit.VCOM_QueueControl(VCOM_QUEUECTRL_QUREYQUEUELENGTH, "");//获取队列人数、排队时长、排第几位
                }
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                mHandler.sendEmptyMessage(TIME_UPDATE);
            }
        }, 0, 1000);

        quickButton = (Button) findViewById(R.id.queue_btn);
        quickButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                alertDialog();
            }
        });
    }

    /**
     * 刷新数据
     */
    private void refreshData(QueueStateBean queueStateBean) {
        Log.d(tag, "当前排队人数共:" + queueStateBean.getLength() + "人,您现在排在第 " + queueStateBean.getIndex() + " 位");
        showTextView.setText("当前排队人数共:" + queueStateBean.getLength() + "人,您现在排在第 " + queueStateBean.getIndex() + " 位");
        timeshow.setText("您已等待了 " + StringUtil.getTimeShowStringTwo(queueStateBean.getTime()));
    }

    private void alertDialog() {
        dialog = DialogFactory.getDialog(DialogFactory.DIALOGID_EXIT_QUEUE, this, new OnClickListener() {
            @Override
            public void onClick(View v) {
                sdkUnit.VCOM_QueueControl(VCOM_QUEUECTRL_LEAVEQUEUE, "");
                finish();
            }
        });
        dialog.show();
    }


    //sdk 初始化
    private void initSdk() {
        if (sdkUnit == null) {
            sdkUnit = VComMediaSDK.GetInstance();
        }
        sdkUnit.SetSDKEvent(this);
        sdkUnit.VCOM_QueueControl(VCOM_QUEUECTRL_QUERYQUEUEINFO, "");//查询队列信息
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        Log.d(tag, "onResume");
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(tag, "onRestart");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if (sdkUnit != null) {
            sdkUnit.RemoveSDKEvent(this);
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        mVideoApplication.setTargetUserName("");
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

            alertDialog();
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void OnLoginSystem(String lpUserCode, int iErrorCode, int iReConnect) {

    }

    @Override
    public void OnDisconnect(int iErrorCode) {

    }

    @Override
    public void OnServerKickout(int i) {
        ToastUtil.show("已在别处登录");
        startActivity(new Intent(QueueActivity.this, LoginActivity.class));
    }

    /**
     * 进出会议室通知
     */
    @Override
    public void OnConferenceResult(int iAction, String lpConfId, int iErrorCode) {
        if (VComSDKDefine.VCOM_CONFERENCE_ACTIONCODE_JOIN == iAction) {
            if (iErrorCode == 0) {
            }
        }
    }

    private void startVideoActvity(String confid) {
        Intent intent = new Intent();
        intent.putExtra("confid", confid);
        intent.setClass(this, VideoActivity.class);
        this.startActivity(intent);
        finish();
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
        switch (iEventType) {
            case VCOM_QUEUEEVENT_QUERYQUEUEINFO:
                if (iErrorCode != 0) {
                    return;
                }
                //查询队列
                queueBean = JsonUtil.jsonToBean(lpUserData, QueueBean.class);
                if (queueBean != null) {
                    //默认队列
                    String lpCtrlValue = "{\"queueid\": \"" + 302 + "\"}";

                    switch (mVideoApplication.getSelectBussiness()) {
                        case "汽车消费办理业务":
                            lpCtrlValue = "{\"queueid\": \"" + 201 + "\"}";
                            break;
                        case "小额消费贷款业务":
                            lpCtrlValue = "{\"queueid\": \"" + 202 + "\"}";
                            break;
                        case "房屋装修贷业务":
                            lpCtrlValue = "{\"queueid\": \"" + 301 + "\"}";
                            break;
                        case "信用卡面签业务业务":
                            lpCtrlValue = "{\"queueid\": \"" + 302 + "\"}";
                            break;
                        default:
                            break;
                    }
                    Log.d(tag, lpCtrlValue);
                    sdkUnit.VCOM_QueueControl(VCOM_QUEUECTRL_ENTERQUEUE, lpCtrlValue);//进入队列
                }
                break;
            case VCOM_QUEUEEVENT_QUREYQUEUELENGTH:
                if (iErrorCode != 0) {
                    return;
                }
                //进入排队后，可以获取队列人数、排队时长、排第几位
                queueStateBean = JsonUtil.jsonToBean(lpUserData, QueueStateBean.class);
                runOnUiThread(() -> refreshData(queueStateBean));
                break;
            case VCOM_QUEUEEVENT_AGENTSERVICE:
                if (iErrorCode != 0) {
                    return;
                }
                //坐席点击 示闲 时回调
                //坐席ID
                String agentId = JsonUtil.jsonToStr(lpUserData, "agent");
                mVideoApplication.setTargetUserName(agentId);

                mTitleLayoutManager.setTitle("呼叫坐席" + agentId);
                showTextView.setText("正在呼叫坐席" + agentId + "中...");
                timeshow.setVisibility(View.INVISIBLE);
                //发送数据
                String temp = GenerateConfig(mVideoApplication.getUserCode(), mVideoApplication.getUserName(), SpUtil.getInstance().getString(SpUtil.USERPHONE, "15915658142"),
                        mVideoApplication.getUserSex(), mVideoApplication.getIdcardAddress(), mVideoApplication.getIdcardNum());
                Log.d(tag, temp);
                sdkUnit.VCOM_SendMessage(agentId, 0, temp);

                String picData = GeneratePicConfig();
                sdkUnit.VCOM_SendMessage(agentId, 1, picData);
                break;
            case VCOM_QUEUEEVENT_STARTVIDEO:
                if (iErrorCode != 0) {
                    return;
                }
                //获取会议ID
                String confid = JsonUtil.jsonToStr(lpUserData, "confid");
                startVideoActvity(confid);
                break;
            case VCOM_QUEUEEVENT_HANGUPVIDEO:
                //用户 坐席挂断
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void OnAIAbilityEvent(int i, int i1, String s) {

    }

    /**
     * 生成Json配置
     */
    private String GenerateConfig(String userCode, String userName, String userPhone, int sex, String address, String idcardNum) {
        //生成流水号
        String tradNo = StringUtil.getCurrentFormatTime();
        Random random = new Random();
        int num = random.nextInt(900) + 100;
        tradNo = tradNo + num;

        String temp = "{\"userId\":\"" + userCode + "\",\"username\":\"" + userCode + "\",\"userStr\":{\"content\":[{\"groupData\":[{\"key\":\"userName\",\"name\":\"客户名称\",\"order\":1,\"value\":\"" + userName + "\"},{\"key\":\"userPhone\",\"name\":\"客户手机\",\"order\":2,\"value\":\"" + userPhone + "\"},{\"key\":\"userSex\",\"name\":\"客户性别\",\"order\":3,\"value\":\"" + sex + "\"},{\"key\":\"idcardAddress\",\"name\":\"证件地址\",\"order\":4,\"value\":\"" + address + "\"},{\"key\":\"idcardNum\",\"name\":\"证件号码\",\"order\":5,\"value\":\"" + idcardNum + "\"}],\"groupName\":\"客户信息\",\"groupOrder\":1},{\"groupData\":[{\"key\":\"productNumber\",\"name\":\"产品编号\",\"order\":1,\"value\":\"Product01\"},{\"key\":\"productName\",\"name\":\"产品名称\",\"order\":2,\"value\":\"中国一号资产管理计划\"},{\"key\":\"integratorCode\",\"name\":\"渠道编码\",\"order\":3,\"value\":\"QuDao01\"},{\"key\":\"integratorName\",\"name\":\"渠道名称\",\"order\":4,\"value\":\"自助渠道\"},{\"key\":\"businessCode\",\"name\":\"业务编码\",\"order\":5,\"value\":\"Biz01\"},{\"key\":\"businessName\",\"name\":\"业务类型\",\"order\":6,\"value\":\"双录业务\"}],\"groupName\":\"业务信息\",\"groupOrder\":2}],\"expansion\":\"{\\\"address\\\":\\\"广州市天河区科韵路\\\",\\\"ip\\\":\\\"192.168.0.101\\\"}\",\"from\":\"Android\",\"thirdTradeNo\":\"" + tradNo + "\",\"type\":2}}";

        return temp;
    }

    private String GeneratePicConfig() {
        String frontPic = "";
        String backPic = "";
        String facePic = "";
        List<TradeInfo.PicListBean> picList = mVideoApplication.getPicList();
        if (picList.size() > 0) {
            for (int i = 0; i < picList.size(); i++) {
                if (picList.get(i).getType() == 15) {
                    frontPic = picList.get(i).getPic();
                } else if (picList.get(i).getType() == 16) {
                    backPic = picList.get(i).getPic();
                } else if (picList.get(i).getType() == 17) {
                    facePic = picList.get(i).getPic();
                }
            }
        }

        String temp = "{\"picList\":[{\"pic\":\"" + frontPic + "\",\"type\":15},{\"pic\":\"" + backPic + "\",\"type\":16},{\"pic\":\"" + facePic + "\",\"type\":17}]}";
        return temp;
    }

}
