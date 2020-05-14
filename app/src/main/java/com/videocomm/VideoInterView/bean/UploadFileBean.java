package com.videocomm.VideoInterView.bean;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/5/14 0014]
 * @function[功能简介]
 **/
public class UploadFileBean {

    /**
     * msg :
     * content : {"id":null,"fileNo":"20200514212642864880173","fileFormat":"mp4","fileName":"a528b168-b3aa-49e3-b4d2-e1f05c3ac835.mp4","sourceName":"vcomrecord_20200514212640_2.mp4","hashCode":null,"fullUrl":"/home/facexfile/a528b168-b3aa-49e3-b4d2-e1f05c3ac835.mp4","name":null,"type":null,"size":13380,"sizeName":null,"status":null,"fieldOne":null,"fieldTwo":null,"fieldThree":null,"position":null,"outerUrl":null,"appId":"37245ec9-6ff3-4a0a-bd5b-4ad896be0d61","createdUserId":null,"createdOrgId":null,"createdTime":null,"fileMd5":"39AAC9B6E523252F846023671B35CF77","videoTag":null}
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
         * id : null
         * fileNo : 20200514212642864880173
         * fileFormat : mp4
         * fileName : a528b168-b3aa-49e3-b4d2-e1f05c3ac835.mp4
         * sourceName : vcomrecord_20200514212640_2.mp4
         * hashCode : null
         * fullUrl : /home/facexfile/a528b168-b3aa-49e3-b4d2-e1f05c3ac835.mp4
         * name : null
         * type : null
         * size : 13380
         * sizeName : null
         * status : null
         * fieldOne : null
         * fieldTwo : null
         * fieldThree : null
         * position : null
         * outerUrl : null
         * appId : 37245ec9-6ff3-4a0a-bd5b-4ad896be0d61
         * createdUserId : null
         * createdOrgId : null
         * createdTime : null
         * fileMd5 : 39AAC9B6E523252F846023671B35CF77
         * videoTag : null
         */

        private Object id;
        private String fileNo;
        private String fileFormat;
        private String fileName;
        private String sourceName;
        private Object hashCode;
        private String fullUrl;
        private Object name;
        private Object type;
        private int size;
        private Object sizeName;
        private Object status;
        private Object fieldOne;
        private Object fieldTwo;
        private Object fieldThree;
        private Object position;
        private Object outerUrl;
        private String appId;
        private Object createdUserId;
        private Object createdOrgId;
        private Object createdTime;
        private String fileMd5;
        private Object videoTag;

        public Object getId() {
            return id;
        }

        public void setId(Object id) {
            this.id = id;
        }

        public String getFileNo() {
            return fileNo;
        }

        public void setFileNo(String fileNo) {
            this.fileNo = fileNo;
        }

        public String getFileFormat() {
            return fileFormat;
        }

        public void setFileFormat(String fileFormat) {
            this.fileFormat = fileFormat;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getSourceName() {
            return sourceName;
        }

        public void setSourceName(String sourceName) {
            this.sourceName = sourceName;
        }

        public Object getHashCode() {
            return hashCode;
        }

        public void setHashCode(Object hashCode) {
            this.hashCode = hashCode;
        }

        public String getFullUrl() {
            return fullUrl;
        }

        public void setFullUrl(String fullUrl) {
            this.fullUrl = fullUrl;
        }

        public Object getName() {
            return name;
        }

        public void setName(Object name) {
            this.name = name;
        }

        public Object getType() {
            return type;
        }

        public void setType(Object type) {
            this.type = type;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public Object getSizeName() {
            return sizeName;
        }

        public void setSizeName(Object sizeName) {
            this.sizeName = sizeName;
        }

        public Object getStatus() {
            return status;
        }

        public void setStatus(Object status) {
            this.status = status;
        }

        public Object getFieldOne() {
            return fieldOne;
        }

        public void setFieldOne(Object fieldOne) {
            this.fieldOne = fieldOne;
        }

        public Object getFieldTwo() {
            return fieldTwo;
        }

        public void setFieldTwo(Object fieldTwo) {
            this.fieldTwo = fieldTwo;
        }

        public Object getFieldThree() {
            return fieldThree;
        }

        public void setFieldThree(Object fieldThree) {
            this.fieldThree = fieldThree;
        }

        public Object getPosition() {
            return position;
        }

        public void setPosition(Object position) {
            this.position = position;
        }

        public Object getOuterUrl() {
            return outerUrl;
        }

        public void setOuterUrl(Object outerUrl) {
            this.outerUrl = outerUrl;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public Object getCreatedUserId() {
            return createdUserId;
        }

        public void setCreatedUserId(Object createdUserId) {
            this.createdUserId = createdUserId;
        }

        public Object getCreatedOrgId() {
            return createdOrgId;
        }

        public void setCreatedOrgId(Object createdOrgId) {
            this.createdOrgId = createdOrgId;
        }

        public Object getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(Object createdTime) {
            this.createdTime = createdTime;
        }

        public String getFileMd5() {
            return fileMd5;
        }

        public void setFileMd5(String fileMd5) {
            this.fileMd5 = fileMd5;
        }

        public Object getVideoTag() {
            return videoTag;
        }

        public void setVideoTag(Object videoTag) {
            this.videoTag = videoTag;
        }
    }
}
