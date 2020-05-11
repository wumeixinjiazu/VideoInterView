package com.videocomm.VideoInterView.bean;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/15 0015]
 * @function[功能简介 登陆接口返回的JSON]
 **/
public class LoginBean {


    /**
     * msg : 登录成功
     * content : {"appId":"","phoneNumber":"13570125462","token":"d3649810-9ee7-4915-bc7e-10fe7d57b781"}
     * errorcode : 0
     */

    private String msg;
    private ContentBean content;
    private int errorcode;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public static class ContentBean {
        /**
         * appId :
         * phoneNumber : 13570125462
         * token : d3649810-9ee7-4915-bc7e-10fe7d57b781
         */

        private String appId;
        private String phoneNumber;
        private String token;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
