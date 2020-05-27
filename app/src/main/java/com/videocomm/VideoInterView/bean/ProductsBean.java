package com.videocomm.VideoInterView.bean;

import java.util.List;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/5/19 0019]
 * @function[功能简介]
 **/
public class ProductsBean {

    /**
     * msg :
     * content : [{"id":4,"appId":"37245ec9-6ff3-4a0a-bd5b-4ad896be0d61","coverUrl":"/home/facexfile/c989506c-3837-4447-be7e-28b9a2b94082.png","description":"","name":" 中国一号资产管理计划","code":"Product01"}]
     * errorcode : 0
     */

    private String msg;
    private int errorcode;
    private List<ContentBean> content;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * id : 4
         * appId : 37245ec9-6ff3-4a0a-bd5b-4ad896be0d61
         * coverUrl : /home/facexfile/c989506c-3837-4447-be7e-28b9a2b94082.png
         * description :
         * name :  中国一号资产管理计划
         * code : Product01
         */

        private int id;
        private String appId;
        private String coverUrl;
        private String description;
        private String name;
        private String code;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getCoverUrl() {
            return coverUrl;
        }

        public void setCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
