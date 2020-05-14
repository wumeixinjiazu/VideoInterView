package com.videocomm.VideoInterView.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/13 0013]
 * @function[功能简介 网络请求工具]
 **/
public class HttpUtil {
    private static final String tag = HttpUtil.class.getSimpleName();

    /**
     * OCR访问的链接
     */
    private static final String OCR_URL_HOST = "http://interview.videocomm.net/v1/client/ocr/";
    private static final String OCR_URL_PATH = "/uploadIdCard";
    private static final String OCR_REAL_URL = OCR_URL_HOST + OCR_URL_PATH;

    /**
     * Header信息
     */
    private static final String APPCODE = "dab61d9eef3b4afd8b1f7a0737e7d3ec";
    private static final String OCR_URL_HEADER_NAME = "Authorization";
    private static final String OCR_URL_HEADER_VALUE = "APPCODE dab61d9eef3b4afd8b1f7a0737e7d3ec";//注意中间有空格

    /**
     * 身份证图片Size 表示  face:正面  back:背面
     */
    public static final String OCR_SIZE_FACE = "front";
    public static final String OCR_SIZE_BACK = "back";

    /**
     * 活体检测动作 张嘴 眨眼 摇头
     */
    public static final String LIVING_ACTION_MOUTH = "openMouth";
    public static final String LIVING_ACTION_EYE = "blink";
    public static final String LIVING_ACTION_HEAD = "shakeHead";

    private static final String URL_BASE = "http://interview.videocomm.net";
    //图形验证码接口 GET
    //示例：/captcha/getNumImageCaptcha?code=0.3223
    public static final String GET_IMAGE_CAPTCHA = URL_BASE + "/captcha/getNumImageCaptcha?code=0.3223";
    //发送信息给用户接口 GET
    //示例：/captcha/sendSMSForUserLogin?phoneNumber=13570125462&imageCaptcha=1252
    private static final String SEND_SMS_USER_LOGIN = URL_BASE + "/captcha/sendSMSForUserLogin";
    //登陆接口 POST
    //示例：/v1/client/login?phoneNumber=13570125462&imageCaptcha=1252&loginType=0&smsCaptcha=688826
    private static final String REQUEST_LOGIN = URL_BASE + "/v1/client/login";
    private static final String REQUEST_NETWORK = URL_BASE + "/v1/client/businessOffice/getlistByCity";
    private static final String REQUEST_FACE_RECO = URL_BASE + "/v1/client/face/detectFace";
    private static final String REQUEST_LIVING_DETECTION = URL_BASE + "/v1/client/face/livingDetection";
    private static final String REQUEST_SEND_TRADEINFO = URL_BASE + "/v1/client/trade/saveTradeInfo";
    private static final String REQUEST_UPLOAD_FILE = URL_BASE + "/v1/client/uploadFile";
    private static OkHttpClient okHttpClient;

    static {

    }

    /**
     * OCR请求
     *
     * @param bitmap
     * @param size
     */
    public static void requestOcrPost(Bitmap bitmap, String size, File file, Callback callback) {
        MediaType mediaType = MediaType.parse("multipart/form-data");
        String token = SpUtil.getInstance().getString(SpUtil.TOKEN, "");
        String userPhone = SpUtil.getInstance().getString(SpUtil.USERPHONE, "");
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        String body2 = "{" +
                "\t\"token\":  \"" + token + "\",\n" +
                "\t\"phoneNumber\":  \"" + userPhone + "\",\n" +
                "\t\"image\":  \"" + base64ToNoHeaderBase64(bitmapToBase64(bitmap)) + "\",\n" +
                "}";
        Log.d(tag, OCR_URL_HOST + size + OCR_URL_PATH);
        Log.d(tag, body2);
        Request request = new Request.Builder()
                .url(OCR_URL_HOST + size + OCR_URL_PATH)
                .post(RequestBody.create(mediaType, body2))
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * OCR请求 上传文件
     *
     * @param size
     * @param file
     * @param callback
     */
    public static void requestOcrPost(String size, File file, Callback callback) {
        MediaType mediaType = MediaType.parse("multipart/form-data");
        String token = SpUtil.getInstance().getString(SpUtil.TOKEN, "");
        String userPhone = SpUtil.getInstance().getString(SpUtil.USERPHONE, "");

        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("image", file.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .addFormDataPart("token", token)
                .addFormDataPart("phoneNumber", userPhone)
                .build();

        Log.d(tag, OCR_URL_HOST + size + OCR_URL_PATH);
        Request request = new Request.Builder()
                .url(OCR_URL_HOST + size + OCR_URL_PATH)
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 图形验证码请求
     *
     * @param callback
     */
    public static void requestImageCaptcha(Callback callback) {
        requestGet(GET_IMAGE_CAPTCHA, callback);
    }

    /**
     * 请求发送验证码给用户
     *
     * @param phoneNumber
     * @param imageCapcha
     * @param callback
     */
    public static void requestSendSMSForUser(final String phoneNumber, final String imageCapcha, String ssionId, Callback callback) {
//        requestGet(SEND_SMS_USER_LOGIN + "?phoneNumber=" + phoneNumber + "&imageCaptcha=" + imageCapcha, callback);
        Request request = new Request.Builder()
                .url(SEND_SMS_USER_LOGIN + "?phoneNumber=" + phoneNumber + "&imageCaptcha=" + imageCapcha)
                .addHeader("Cookie", ssionId)
                .get()
                .build();
        okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void requestLogin(String phoneNumber, String imageCapcha, String smsCaptcha, Callback callback) {
        FormBody formBody = new FormBody.Builder()
                .add("phoneNumber", phoneNumber)
                .add("imageCapcha", imageCapcha)
                .add("smsCaptcha", smsCaptcha)
                .add("appCode", "VCom")
                .add("loginType", "0").build();
        requestPost(REQUEST_LOGIN, formBody, callback);
    }

    //请求网点
    public static void requestNetwork(String city, String userPhone, String pageSize, String offset, Callback callback) {
        String token = SpUtil.getInstance().getString(SpUtil.TOKEN, "");
        FormBody formBody = new FormBody.Builder().add("city", city).add("phoneNumber", userPhone).add("pageSize", pageSize).add("offset", offset).
                add("appId", "37245ec9-6ff3-4a0a-bd5b-4ad896be0d61").add("token", token).build();
        requestPost(REQUEST_NETWORK, formBody, callback);
    }

    //请求人脸识别
    public static void requestFaceReco(File file, Callback callback) {
        String token = SpUtil.getInstance().getString(SpUtil.TOKEN, "");
        String userPhone = SpUtil.getInstance().getString(SpUtil.USERPHONE, "");
        String appid = SpUtil.getInstance().getString(SpUtil.APPID, "");

        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("image", file.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .addFormDataPart("token", token)
                .addFormDataPart("appId", appid)
                .addFormDataPart("phoneNumber", userPhone)
                .build();
        requestPost(REQUEST_FACE_RECO, requestBody, callback);
    }

    //请求活体检测
    public static void requestLivingDetection(File file, String action, Callback callback) {
        String token = SpUtil.getInstance().getString(SpUtil.TOKEN, "");
        String userPhone = SpUtil.getInstance().getString(SpUtil.USERPHONE, "");
        String appid = SpUtil.getInstance().getString(SpUtil.APPID, "");

        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("image", file.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .addFormDataPart("token", token)
                .addFormDataPart("phoneNumber", userPhone)
                .addFormDataPart("appId", appid)
                .addFormDataPart("action", action)
                .build();
        requestPost(REQUEST_LIVING_DETECTION, requestBody, callback);
    }

    //请求发送业务数据
    public static void requestSendTradeInfo(String data, Callback callback) {
        Log.d(tag, data);
        String token = SpUtil.getInstance().getString(SpUtil.TOKEN, "");
        String userPhone = SpUtil.getInstance().getString(SpUtil.USERPHONE, "");
        String appid = SpUtil.getInstance().getString(SpUtil.APPID, "");

        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("token", token)
                .addFormDataPart("phoneNumber", userPhone)
                .addFormDataPart("appId", appid)
                .addFormDataPart("data", data)

                .build();
        requestPost(REQUEST_SEND_TRADEINFO, requestBody, callback);
    }

    //上传文件
    public static void requestUploadFile(File file, Callback callback) {
        String token = SpUtil.getInstance().getString(SpUtil.TOKEN, "");
        String userPhone = SpUtil.getInstance().getString(SpUtil.USERPHONE, "");
        String appid = SpUtil.getInstance().getString(SpUtil.APPID, "");

        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("token", token)
                .addFormDataPart("phoneNumber", userPhone)
                .addFormDataPart("appId", appid)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();
        requestPost(REQUEST_UPLOAD_FILE, requestBody, callback);
    }

    private static void requestPost(String url, RequestBody body, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Log.d(tag, request.url().toString());
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * Get请求
     *
     * @param url
     * @param callback
     */
    public static void requestGet(String url, Callback callback) {
        Log.d(tag, url);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 将base64的头去掉
     *
     * @param base64
     * @return
     */
    private static String base64ToNoHeaderBase64(String base64) {
        return base64.replace("data:image/jpeg;base64,", "");
    }

    /**
     * 将Bitmap转换成Base64字符串
     *
     * @param bitmap
     * @return
     */
    private static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        //转换来的base64码需要加前缀，必须是NO_WRAP参数，表示没有空格。
        return "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

}
