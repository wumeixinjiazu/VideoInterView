package com.videocomm.VideoInterView.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * @author[wengCJ]
 * @version[创建日期，2019/12/27 0027]
 * @function[功能简介 关于权限申请的工具类]
 **/
public class PermissionUtil {

    /**
     * 检查权限是否拥有 没有则去申请 Activity调用
     */
    public static boolean checkPermission(Activity activity, String[] permissions, int requestCode) {

        if (Build.VERSION.SDK_INT >= 23) {//6.0才用动态权限
            for (String permission : permissions) {
                //检查是否授予权限 返回的结果为PackageManager.PERMISSION_GRANTED（0）表示授予权限
                if (ContextCompat.checkSelfPermission(activity,
                        permission) != PackageManager.PERMISSION_GRANTED) {
                    //请求权限

                    ActivityCompat.requestPermissions(activity,
                            permissions,
                            requestCode);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 检查权限是否拥有 没有则去申请 Fragment调用
     */
    public static boolean checkPermission(Fragment fragment, String[] permissions, int requestCode) {

        if (Build.VERSION.SDK_INT >= 23) {//6.0才用动态权限
            for (String permission : permissions) {
                //检查是否授予权限 返回的结果为PackageManager.PERMISSION_GRANTED（0）表示授予权限
                if (ContextCompat.checkSelfPermission(fragment.getActivity(),
                        permission) != PackageManager.PERMISSION_GRANTED) {
                    //请求权限

                    fragment.requestPermissions(
                            permissions,
                            requestCode);
                    return false;
                }
            }
        }
        return true;
    }
}
