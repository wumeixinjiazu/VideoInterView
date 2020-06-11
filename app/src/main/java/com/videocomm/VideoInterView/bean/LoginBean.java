package com.videocomm.VideoInterView.bean;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/15 0015]
 * @function[功能简介 登陆接口返回的JSON]
 **/
public class LoginBean {

    /**
     * msg : 登录成功
     * content : {"configData":{"port":8080,"integratorCode":"QuDao01","businessCode":"Biz01","Server":"demo.videocomm.net"},"appId":"37245ec9-6ff3-4a0a-bd5b-4ad896be0d61","phoneNumber":"13570125462","token":"d5a7209e-faf2-48e0-8a76-04ae93957aaa"}
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
         * configData : {"port":8080,"integratorCode":"QuDao01","businessCode":"Biz01","Server":"demo.videocomm.net"}
         * appId : 37245ec9-6ff3-4a0a-bd5b-4ad896be0d61
         * phoneNumber : 13570125462
         * token : d5a7209e-faf2-48e0-8a76-04ae93957aaa
         */

        private ConfigDataBean configData;
        private String appId;
        private String phoneNumber;
        private String token;

        public ConfigDataBean getConfigData() {
            return configData;
        }

        public void setConfigData(ConfigDataBean configData) {
            this.configData = configData;
        }

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

        public static class ConfigDataBean {
            /**
             * port : 8080
             * integratorCode : QuDao01
             * businessCode : Biz01
             * Server : demo.videocomm.net
             */

            private int port;
            private String integratorCode;
            private String businessCode;
            private String Server;

            public int getPort() {
                return port;
            }

            public void setPort(int port) {
                this.port = port;
            }

            public String getIntegratorCode() {
                return integratorCode;
            }

            public void setIntegratorCode(String integratorCode) {
                this.integratorCode = integratorCode;
            }

            public String getBusinessCode() {
                return businessCode;
            }

            public void setBusinessCode(String businessCode) {
                this.businessCode = businessCode;
            }

            public String getServer() {
                return Server;
            }

            public void setServer(String Server) {
                this.Server = Server;
            }
        }
    }
}
