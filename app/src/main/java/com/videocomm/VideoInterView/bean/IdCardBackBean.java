package com.videocomm.VideoInterView.bean;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/13 0013]
 * @function[功能简介 身份证正面Bean]
 **/
public class IdCardBackBean {


    /**
     * msg : 反面身份证上传成功
     * content : {"issueDate":"20180807","expiryDate":"20280807","issueOrganiz":"惠来县公安局","backIdCardUrl":"b61b92d3-091b-4f64-ada1-6cb6efb6da77.jpg"}
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
         * issueDate : 20180807
         * expiryDate : 20280807
         * issueOrganiz : 惠来县公安局
         * backIdCardUrl : b61b92d3-091b-4f64-ada1-6cb6efb6da77.jpg
         */

        private String issueDate;
        private String expiryDate;
        private String issueOrganiz;
        private String backIdCardUrl;

        public String getIssueDate() {
            return issueDate;
        }

        public void setIssueDate(String issueDate) {
            this.issueDate = issueDate;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public String getIssueOrganiz() {
            return issueOrganiz;
        }

        public void setIssueOrganiz(String issueOrganiz) {
            this.issueOrganiz = issueOrganiz;
        }

        public String getBackIdCardUrl() {
            return backIdCardUrl;
        }

        public void setBackIdCardUrl(String backIdCardUrl) {
            this.backIdCardUrl = backIdCardUrl;
        }
    }
}
