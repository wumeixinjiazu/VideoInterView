package com.videocomm.VideoInterView.utils;

import com.videocomm.VideoInterView.R;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/3/31 0031]
 * @function[功能简介 登录工具 负责一些数据的审查]
 **/
public class LoginUtil {
    /**
     * 检查手机号是否正确
     *
     * @param phone
     * @return
     */
    public static boolean checkPhone(String phone) {
        if (phone.length() == 0) {
            ToastUtil.show(R.string.tip_phone_fromat_err);
            return false;
        }
        if (!StringUtil.isMobile(phone)) {
            ToastUtil.show(R.string.tip_phone_fromat_err);
            return false;
        }
        return true;
    }

    /**检查图形码
     * @param imageCode
     * @return
     */
    public static boolean checkImageCode(String imageCode){
        if (imageCode.length() == 0){
            ToastUtil.show((R.string.tip_image_code_err));
            return false;
        }

        return true;
    }

    public static boolean checkCode(String code){
        if (code.length() == 0){
            ToastUtil.show(R.string.tip_code_err);
            return false;
        }
        return true;
    }
}
