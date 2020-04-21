package com.videocomm.VideoInterView.bean;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/13 0013]
 * @function[功能简介  身份证背面Bean]
 **/
public class IdCardFrontBean {

    /**
     * msg : 正面身份证上传成功
     * content : {"address":null,"idCardNo":null,"birth":null,"name":null,"sex":null,"nation":null,"frontIdCardUrl":"1a27c834-af86-4bfb-b029-074c7ce9a375.jpg"}
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
         * address : null
         * idCardNo : null
         * birth : null
         * name : null
         * sex : null
         * nation : null
         * frontIdCardUrl : 1a27c834-af86-4bfb-b029-074c7ce9a375.jpg
         */

        private String address;
        private String idCardNo;
        private String birth;
        private String name;
        private String sex;
        private String nation;
        private String frontIdCardUrl;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getIdCardNo() {
            return idCardNo;
        }

        public void setIdCardNo(String idCardNo) {
            this.idCardNo = idCardNo;
        }

        public String getBirth() {
            return birth;
        }

        public void setBirth(String birth) {
            this.birth = birth;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getNation() {
            return nation;
        }

        public void setNation(String nation) {
            this.nation = nation;
        }

        public String getFrontIdCardUrl() {
            return frontIdCardUrl;
        }

        public void setFrontIdCardUrl(String frontIdCardUrl) {
            this.frontIdCardUrl = frontIdCardUrl;
        }
    }
}
