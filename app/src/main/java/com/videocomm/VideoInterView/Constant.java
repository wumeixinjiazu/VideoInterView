package com.videocomm.VideoInterView;

/**
 *
 **/
public class Constant {
    public static final int REQUEST_CODE_CHOOSE_ACT = 10000;
    public static final int RESULT_CODE_PARAMS_ACT = 10001;
    public static final int RESULT_CODE_IDENTITY_ACT = 10002;
    public static final int RESULT_CODE_Record_Result_ACT = 10003;
    public static final int RESULT_CODE_DETECT_ACT = 10004;
    public static final int REQUEST_CODE_LOCATION = 10005;

    public static final int CAMERA_REQUEST_CODE = 1000;
    public static final int PHOTO_REQUEST_CODE = 1001;
    public static final int CAMERA_PERMISSION_CODE = 2000;
    public static final int PHOTO_PERMISSION_CODE = 2001;

    public static final int LOGIN_PERMISSION_CODE = 3000;

    /**
     * 用过数值来区分 身份证照片 是通过相册还是相机
     */
    public static final int FRONT_VIEW = 3000;
    public static final int BACK_VIEW = 3001;
    public static final int OPEN_CAMERA = 10;
    public static final int OPEN_PHOTO = 20;
    public static final int FRONT_VIEW_BY_CAMERA = FRONT_VIEW + OPEN_CAMERA;
    public static final int BACK_VIEW_BY_CAMERA = BACK_VIEW + OPEN_CAMERA;
    public static final int FRONT_VIEW_BY_PHOTO = FRONT_VIEW + OPEN_PHOTO;
    public static final int BACK_VIEW_BY_PHOTO = BACK_VIEW + OPEN_PHOTO;

    public static final String IDCARD_IMAGE_TYPE = "image_type";

    /**
     * 音视频打开和关闭常量
     */
    public static final int mIntRemoteVideoOpen = 1;
    public static final int mIntRemoteAudioOpen = 1;
    public static final int mIntLocalVideoOpen = 1;
    public static final int mIntLocalAudioOpen = 1;
    public static final int mIntRemoteVideoClose = 0;
    public static final int mIntRemoteAudioClose = 0;
    public static final int mIntLocalVideoClose = 0;
    public static final int mIntLocalAudioClose = 0;
    public static final int mIntLocalChannelIndex = 0;
    public static final int mIntRemoteChannelIndex = 0;


    /**
     * 活体检测动作
     */
    public static final int UI_ACTION_EYE = 0;
    public static final int UI_ACTION_MOUSE = 0;
    public static final int UI_ACTION_HEAD = 0;

    /**
     * 临柜模式
     */
    public static final int LOCALSCENE_CLOSE = 0;
    public static final int LOCALSCENE_OPEN = 1;

    /**
     * 相册获取的图片压缩路径
     */
    public static final String PHOTO_FRONT_PATH = "photoFront";
    public static final String PHOTO_BACK_PATH = "photoBack";

    /**
     * 拍照的图片压缩路径
     */
    public static final String TAKE_PIC_FRONT_PATH = "takeFront";
    public static final String TAKE_PIC_BACK_PATH = "takeBack";

    public static final String DEFAULT_FRONT_PATH = "default_front";
    public static final String DEFAULT_BACK_PATH = "default_back";

    /**
     * 人脸识别图片压缩路径
     */
    public static final String FACE_RECO_PIC_PATH = "faceReco";

    /**
     * 发送接收消息类型
     * 风险播报 0
     * 文字聊天 1
     */
    public static final int MSG_TYPE_RISK = 0;
    public static final int MSG_TYPE_CHAT = 1;


}
