package com.videocomm.VideoInterView.bean;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/5/19 0019]
 * @function[功能简介]
 **/
public class RouteBean {

    /**
     * msg : 获取进线路由成功！
     * content : {"id":3,"routeType":0,"routeKey":"{\"businessCode\":\"Biz01\",\"integratorCode\":\"QuDao01\",\"productCode\":\"Product01\"}","paramData":"{\"businessName\":\"双录业务\",\"integratorName\":\"自助渠道\",\"businessHallId\":666666001,\"queueId\":201,\"description\":\"资产管理业务队列\",\"name\":\"资产管理业务队列\",\"isCheck\":\"0\",\"productName\":\" 中国一号资产管理计划\"}","routeName":"双录业务路由","appId":"37245ec9-6ff3-4a0a-bd5b-4ad896be0d61","createdUserId":"9","createdOrgId":"7","createdTime":"2020-05-07 14:25:55","integratorName":"自助渠道","businessName":"双录业务","integratorCode":"QuDao01","businessCode":"Biz01","isCheck":0,"queueId":201,"businessHallId":666666001}
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
         * id : 3
         * routeType : 0
         * routeKey : {"businessCode":"Biz01","integratorCode":"QuDao01","productCode":"Product01"}
         * paramData : {"businessName":"双录业务","integratorName":"自助渠道","businessHallId":666666001,"queueId":201,"description":"资产管理业务队列","name":"资产管理业务队列","isCheck":"0","productName":" 中国一号资产管理计划"}
         * routeName : 双录业务路由
         * appId : 37245ec9-6ff3-4a0a-bd5b-4ad896be0d61
         * createdUserId : 9
         * createdOrgId : 7
         * createdTime : 2020-05-07 14:25:55
         * integratorName : 自助渠道
         * businessName : 双录业务
         * integratorCode : QuDao01
         * businessCode : Biz01
         * isCheck : 0
         * queueId : 201
         * businessHallId : 666666001
         */

        private int id;
        private int routeType;
        private String routeKey;
        private String paramData;
        private String routeName;
        private String appId;
        private String createdUserId;
        private String createdOrgId;
        private String createdTime;
        private String integratorName;
        private String businessName;
        private String integratorCode;
        private String businessCode;
        private int isCheck;
        private int queueId;
        private int businessHallId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getRouteType() {
            return routeType;
        }

        public void setRouteType(int routeType) {
            this.routeType = routeType;
        }

        public String getRouteKey() {
            return routeKey;
        }

        public void setRouteKey(String routeKey) {
            this.routeKey = routeKey;
        }

        public String getParamData() {
            return paramData;
        }

        public void setParamData(String paramData) {
            this.paramData = paramData;
        }

        public String getRouteName() {
            return routeName;
        }

        public void setRouteName(String routeName) {
            this.routeName = routeName;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getCreatedUserId() {
            return createdUserId;
        }

        public void setCreatedUserId(String createdUserId) {
            this.createdUserId = createdUserId;
        }

        public String getCreatedOrgId() {
            return createdOrgId;
        }

        public void setCreatedOrgId(String createdOrgId) {
            this.createdOrgId = createdOrgId;
        }

        public String getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(String createdTime) {
            this.createdTime = createdTime;
        }

        public String getIntegratorName() {
            return integratorName;
        }

        public void setIntegratorName(String integratorName) {
            this.integratorName = integratorName;
        }

        public String getBusinessName() {
            return businessName;
        }

        public void setBusinessName(String businessName) {
            this.businessName = businessName;
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

        public int getIsCheck() {
            return isCheck;
        }

        public void setIsCheck(int isCheck) {
            this.isCheck = isCheck;
        }

        public int getQueueId() {
            return queueId;
        }

        public void setQueueId(int queueId) {
            this.queueId = queueId;
        }

        public int getBusinessHallId() {
            return businessHallId;
        }

        public void setBusinessHallId(int businessHallId) {
            this.businessHallId = businessHallId;
        }
    }
}
