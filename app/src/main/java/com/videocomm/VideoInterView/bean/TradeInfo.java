package com.videocomm.VideoInterView.bean;

import java.util.List;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/5/5 0005]
 * @function[功能简介]
 **/
public class TradeInfo {

    /**
     * appId : C49451B7-2739-C244-9273-3CA2B11C6D6F
     * userCode : usercode_1
     * idcardNum : 440102199703023214
     * userPhone : 13312345678
     * userName : 张三
     * userSex : 0
     * idcardAddress : 广州市越秀区北京路183号1-2单元
     * idcardBirth : 1997-03-02
     * idcardVaildTime : 2002-12-21
     * idcardInvaildTime : 2022-12-21
     * idcardSignOrganization : 广州市越秀区公安局
     * idcardNation : 汉
     * productNumber : 20032993
     * productName : 消费贷款产品
     * fxModel : 4
     * videoStatus : 1
     * status : 0
     * picList : [{"pic":"f9109e2a-f5d1-4ebe-9605-0b072f9a92ca.jpg","type":15},{"pic":"f8bb3a6e-700d-4197-af7c-d69c8cd40e6f.jpg","type":16},{"pic":"a327a295-5e01-4181-9acc-1b86c294e4cb.jpg","type":17}]
     * videos : [{"filepath":"/home/AnyChatCloud/html/AnyChatFaceX/自助开户录制视频.mp4","type":2},{"filepath":"/home/AnyChatCloud/html/AnyChatFaceX/活体检测视频.mp4","type":2}]
     * exInfos : [{"exKey":"address","exValue":"广州市天河区科韵路","description":""},{"exKey":"ip","exValue":"192.168.0.101","description":""}]
     * localRecord : 1
     * tradeNo : 202005041235310001
     */

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
    private List<PicListBean> picList;
    private List<VideosBean> videos;
    private List<ExInfosBean> exInfos;

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

    public List<PicListBean> getPicList() {
        return picList;
    }

    public void setPicList(List<PicListBean> picList) {
        this.picList = picList;
    }

    public List<VideosBean> getVideos() {
        return videos;
    }

    public void setVideos(List<VideosBean> videos) {
        this.videos = videos;
    }

    public List<ExInfosBean> getExInfos() {
        return exInfos;
    }

    public void setExInfos(List<ExInfosBean> exInfos) {
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
}
