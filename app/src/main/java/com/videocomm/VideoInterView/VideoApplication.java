package com.videocomm.VideoInterView;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;
import com.videocomm.VideoInterView.bean.TradeInfo;
import com.videocomm.VideoInterView.utils.AppUtil;
import com.videocomm.mediasdk.VComMediaSDK;

import java.util.List;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/3/31 0031]
 * @function[功能简介]
 **/
public class VideoApplication extends Application {
    private String appId;
    private String userCode;
    private String idcardNum;
    private String userPhone;
    private String userName;
    private int userSex;
    private String idcardAddress;
    private String idcardBirth;
    private String idcardVaildTime;
    private String idcardInvaildTime;
    private String idcardSignOrganization;
    private String idcardNation;
    private String productNumber;
    private String productName;
    private int fxModel;
    private int videoStatus;
    private int status;
    private String localRecord;
    private String tradeNo;
    private String recordPath;
    private String selectBussiness; //选择的办理业务
    private String targetUserName = "";        //对方用户名字

    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }

    public String getSelectBussiness() {
        return selectBussiness;
    }

    public void setSelectBussiness(String selectBussiness) {
        this.selectBussiness = selectBussiness;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    private List<TradeInfo.PicListBean> picList;
    private List<TradeInfo.VideosBean> videos;
    private List<TradeInfo.ExInfosBean> exInfos;


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getIdcardNum() {
        return idcardNum;
    }

    public void setIdcardNum(String idcardNum) {
        this.idcardNum = idcardNum;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public String getIdcardAddress() {
        return idcardAddress;
    }

    public void setIdcardAddress(String idcardAddress) {
        this.idcardAddress = idcardAddress;
    }

    public String getIdcardBirth() {
        return idcardBirth;
    }

    public void setIdcardBirth(String idcardBirth) {
        this.idcardBirth = idcardBirth;
    }

    public String getIdcardVaildTime() {
        return idcardVaildTime;
    }

    public void setIdcardVaildTime(String idcardVaildTime) {
        this.idcardVaildTime = idcardVaildTime;
    }

    public String getIdcardInvaildTime() {
        return idcardInvaildTime;
    }

    public void setIdcardInvaildTime(String idcardInvaildTime) {
        this.idcardInvaildTime = idcardInvaildTime;
    }

    public String getIdcardSignOrganization() {
        return idcardSignOrganization;
    }

    public void setIdcardSignOrganization(String idcardSignOrganization) {
        this.idcardSignOrganization = idcardSignOrganization;
    }

    public String getIdcardNation() {
        return idcardNation;
    }

    public void setIdcardNation(String idcardNation) {
        this.idcardNation = idcardNation;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getFxModel() {
        return fxModel;
    }

    public void setFxModel(int fxModel) {
        this.fxModel = fxModel;
    }

    public int getVideoStatus() {
        return videoStatus;
    }

    public void setVideoStatus(int videoStatus) {
        this.videoStatus = videoStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLocalRecord() {
        return localRecord;
    }

    public void setLocalRecord(String localRecord) {
        this.localRecord = localRecord;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public List<TradeInfo.PicListBean> getPicList() {
        return picList;
    }

    public void setPicList(List<TradeInfo.PicListBean> picList) {
        this.picList = picList;
    }

    public List<TradeInfo.VideosBean> getVideos() {
        return videos;
    }

    public void setVideos(List<TradeInfo.VideosBean> videos) {
        this.videos = videos;
    }

    public List<TradeInfo.ExInfosBean> getExInfos() {
        return exInfos;
    }

    public void setExInfos(List<TradeInfo.ExInfosBean> exInfos) {
        this.exInfos = exInfos;
    }

    public static class PicListBean {
        /**
         * pic : f9109e2a-f5d1-4ebe-9605-0b072f9a92ca.jpg
         * type : 15
         */

        private String pic;
        private int type;

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public static class VideosBean {
        /**
         * filepath : /home/AnyChatCloud/html/AnyChatFaceX/自助开户录制视频.mp4
         * type : 2
         */

        private String filepath;
        private int type;

        public String getFilepath() {
            return filepath;
        }

        public void setFilepath(String filepath) {
            this.filepath = filepath;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public static class ExInfosBean {
        /**
         * exKey : address
         * exValue : 广州市天河区科韵路
         * description :
         */

        private String exKey;
        private String exValue;
        private String description;

        public String getExKey() {
            return exKey;
        }

        public void setExKey(String exKey) {
            this.exKey = exKey;
        }

        public String getExValue() {
            return exValue;
        }

        public void setExValue(String exValue) {
            this.exValue = exValue;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtil.init(this);
        //逐木鸟
        CrashReport.initCrashReport(getApplicationContext(), "25839b668f", true);

        VComMediaSDK.GetInstance().VCOM_Initialize(0, "", getApplicationContext());//Content最好传 getApplicationContext

    }
}
