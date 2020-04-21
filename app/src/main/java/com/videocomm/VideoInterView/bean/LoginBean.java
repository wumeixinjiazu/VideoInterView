package com.videocomm.VideoInterView.bean;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/15 0015]
 * @function[功能简介 登陆接口返回的JSON]
 **/
public class LoginBean {

    /**
     * msg : 登录成功
     * content : {"token":"801e58bb-ac69-415e-a920-c8cec9f044d9","userPhone":"13570125462"}
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
         * token : 801e58bb-ac69-415e-a920-c8cec9f044d9
         * userPhone : 13570125462
         */

        private String token;
        private String userPhone;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUserPhone() {
            return userPhone;
        }

        public void setUserPhone(String userPhone) {
            this.userPhone = userPhone;
        }
    }
}
