package com.videocomm.VideoInterView.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.VideoApplication;
import com.videocomm.VideoInterView.activity.base.TitleActivity;
import com.videocomm.VideoInterView.bean.QueueBean;
import com.videocomm.VideoInterView.bean.QueueStateBean;
import com.videocomm.VideoInterView.bean.TradeInfo;
import com.videocomm.VideoInterView.utils.AppUtil;
import com.videocomm.VideoInterView.utils.DialogFactory;
import com.videocomm.VideoInterView.utils.JsonUtil;
import com.videocomm.VideoInterView.utils.SpUtil;
import com.videocomm.VideoInterView.utils.StringUtil;
import com.videocomm.VideoInterView.utils.ToastUtil;
import com.videocomm.mediasdk.VComMediaSDK;
import com.videocomm.mediasdk.VComSDKDefine;
import com.videocomm.mediasdk.VComSDKEvent;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUECTRL_ENTERQUEUE;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUECTRL_HANGUPVIDEO;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUECTRL_LEAVEQUEUE;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUECTRL_QUERYQUEUEINFO;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUECTRL_QUREYQUEUELENGTH;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUEEVENT_AGENTSERVICE;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUEEVENT_ENTERRESULT;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUEEVENT_HANGUPVIDEO;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUEEVENT_QUERYQUEUEINFO;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUEEVENT_QUREYQUEUELENGTH;
import static com.videocomm.mediasdk.VComSDKDefine.VCOM_QUEUEEVENT_STARTVIDEO;

public class QueueActivity extends TitleActivity implements VComSDKEvent {
    private TextView showTextView;
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
    private Button queueButton;
    private boolean isAgentHandup = false;//记录是否坐席挂断

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
        queueButton = (Button) findViewById(R.id.queue_btn);
        queueButton.setOnClickListener(v -> alertDialog());
        if (targetUserName.length() > 0) {
            //表示 用户还没进入队列之前 坐席已经示闲（等待用户）
            mTitleLayoutManager.setTitle("呼叫坐席" + targetUserName);
            queueButton.setText(getString(R.string.finish_call));
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
        dialog = DialogFactory.getDialog(DialogFactory.DIALOGID_EXIT_QUEUE, this, v -> {
            if (queueButton.getText().toString().equals(getString(R.string.finish_call))) {
                sdkUnit.VCOM_QueueControl(VCOM_QUEUECTRL_HANGUPVIDEO, "");
            } else if (queueButton.getText().toString().equals(getString(R.string.finish_queue))) {
                sdkUnit.VCOM_QueueControl(VCOM_QUEUECTRL_LEAVEQUEUE, "");
            }
            mVideoApplication.setTargetUserName("");
            finish();
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
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
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
        finish();
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
    public void OnRemoteVideoData(String s, int i, int i1, int i2, byte[] bytes, int i3, int i4, int i5, int i6, int i7) {

    }

    @Override
    public void OnRemoteAudioData(String s, int i, int i1, byte[] bytes, int i2, int i3) {

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
    public void OnSendFileStatus(int i, int i1, int i2, String s, int i3, int i4, String s1) {

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
                    //发送数据
                    String data = generateQueueConfig();
                    Log.d(tag, data);
                    sdkUnit.VCOM_QueueControl(VCOM_QUEUECTRL_ENTERQUEUE, data);//进入队列
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
                queueButton.setText(getString(R.string.finish_call));
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
                isAgentHandup = true;
                break;
            case VCOM_QUEUEEVENT_ENTERRESULT:
                //进入队列(坐席拒绝或者呼叫超过一分钟回调这里)
                if (iErrorCode == 0 && isAgentHandup) {
                    mTitleLayoutManager.setTitle(mVideoApplication.getSelectBussiness() + "-排队等待中");
                    queueButton.setText(getString(R.string.finish_queue));
                    timeshow.setVisibility(View.VISIBLE);
                    showTextView.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void OnAIAbilityEvent(int i, int i1, String s) {

    }

    //生成队列发送数据
    private String generateQueueConfig() {
        //生成流水号
        String tradNo = StringUtil.getCurrentFormatTime();
        Random random = new Random();
        int num = random.nextInt(900) + 100;
        tradNo = tradNo + num;

        String frontPic = "";
        String backPic = "";
        String facePic = "";
        List<TradeInfo.PicListBean> picList = mVideoApplication.getPicList();
        if (picList != null) {
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
        }

        int queueId = mVideoApplication.getQueueId();
        int sex = mVideoApplication.getUserSex();
        String userCode = mVideoApplication.getUserCode();
        String userName = mVideoApplication.getUserName();
        String userPhone = SpUtil.getInstance().getString(SpUtil.USERPHONE, "15915658142");
        String idCardAddress = mVideoApplication.getIdcardAddress();
        String idcardNum = mVideoApplication.getIdcardNum();
        String address = mVideoApplication.getAddress();
        String addrDesc = mVideoApplication.getAddressDesc();
        double latitude = mVideoApplication.getLatitude();
        double longitude = mVideoApplication.getLongitude();
        String ip = AppUtil.getIP();
        String businessData = "{\"userId\":\"" + userCode + "\",\"username\":\"" + userCode + "\",\"userStr\":{\"content\":[{\"groupData\":[{\"key\":\"userName\",\"name\":\"客户名称\",\"order\":1,\"value\":\"" + userName + "\"},{\"key\":\"userPhone\",\"name\":\"客户手机\",\"order\":2,\"value\":\"" + userPhone + "\"},{\"key\":\"userSex\",\"name\":\"客户性别\",\"order\":3,\"value\":\"" + sex + "\"},{\"key\":\"idcardAddress\",\"name\":\"证件地址\",\"order\":4,\"value\":\"" + idCardAddress + "\"},{\"key\":\"idcardNum\",\"name\":\"证件号码\",\"order\":5,\"value\":\"" + idcardNum + "\"}],\"groupName\":\"客户信息\",\"groupOrder\":1},{\"groupData\":[{\"key\":\"productNumber\",\"name\":\"产品编号\",\"order\":1,\"value\":\"Product01\"},{\"key\":\"productName\",\"name\":\"产品名称\",\"order\":2,\"value\":\"中国一号资产管理计划\"},{\"key\":\"integratorCode\",\"name\":\"渠道编码\",\"order\":3,\"value\":\"QuDao01\"},{\"key\":\"integratorName\",\"name\":\"渠道名称\",\"order\":4,\"value\":\"自助渠道\"},{\"key\":\"businessCode\",\"name\":\"业务编码\",\"order\":5,\"value\":\"Biz01\"},{\"key\":\"businessName\",\"name\":\"业务类型\",\"order\":6,\"value\":\"双录业务\"}],\"groupName\":\"业务信息\",\"groupOrder\":2}],\"expansion\":\"{\\\"address\\\":\\\"" + address + "\\\",\\\"ip\\\":\\\"" + ip + "\\\"}\",\"from\":\"Android\",\"thirdTradeNo\":\"" + tradNo + "\",\"type\":2,\"picList\":[{\"pic\":\"" + frontPic + "\",\"type\":15},{\"pic\":\"" + backPic + "\",\"type\":16},{\"pic\":\"" + facePic + "\",\"type\":17}],\"exInfos\":[{\"exKey\":\"address\",\"exValue\":\"" + address + "\",\"description\":\"" + addrDesc + "\"},{\"exKey\":\"ip\",\"exValue\":\"192.168.0.101\",\"description\":\"\"},{\"exKey\":\"latitude\",\"exValue\":\"" + latitude + "\",\"description\":\"\"},{\"exKey\":\"longitude\",\"exValue\":\"" + longitude + "\",\"description\":\"\"}]}}";
//        try {
//            businessData = Base64.encodeToString(businessData.getBytes("UTF-8"), Base64.NO_WRAP);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        String resultData = "{\"queueid\":\"" + queueId + "\",\"business\":" + businessData + "}";
        return resultData;
    }

}
