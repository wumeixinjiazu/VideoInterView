package com.videocomm.VideoInterView;

/**
 *
 **/
public class Constant {
    public static final int REQUEST_CODE_CHOOSE_ACT = 10000;
    public static final int RESULT_CODE_PARAMS_ACT = 10001;

    public static final int CAMERA_REQUEST_CODE = 1000;
    public static final int PHOTO_REQUEST_CODE = 1001;
    public static final int CAMERA_PERMISSION_CODR = 2000;
    public static final int PHOTO_PERMISSION_CODR = 2001;

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


}
