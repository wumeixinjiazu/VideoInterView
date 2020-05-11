package com.videocomm.VideoInterView.bean;

import java.util.List;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/21 0021]
 * @function[功能简介]
 **/
public class NetworkBean {

    /**
     * msg :
     * content : {"resultList":[{"id":9,"name":"广州五羊新城支行","city":"广州","address":"广州市越秀区寺右新马路124号102房","telphone":"020-87362803","isActive":1},{"id":10,"name":"广州赤岗支行","city":"广州","address":"广州市海珠区新港东路2-4号202房","telphone":"020-89237256","isActive":1},{"id":11,"name":"广州前进路支行","city":"广州","address":"广州市前进路44-50号首层之二铺","telphone":"020-84245097","isActive":1},{"id":14,"name":"广州天河北路支行","city":"广州","address":"广州市天河区天河北路芳草园583号","telphone":"020-38460815","isActive":1},{"id":15,"name":"广州天朗明居支行","city":"广州","address":"广州市天河区车陂路425号101-102铺、427号101-102铺、429号101铺、431号101铺、433号102铺","telphone":"020-38629491","isActive":1},{"id":16,"name":"广州鱼珠支行","city":"广州","address":"广州市天河区中山大道中1088号101房A102、A103商铺","telphone":"020-82295105","isActive":1},{"id":17,"name":"广州开发区西区支行","city":"广州","address":"广州市萝岗区开发大道234号","telphone":"020-82213846","isActive":1},{"id":18,"name":"广州天河东路支行","city":"广州","address":"广州市天河东路华康街2-4号首层","telphone":"020-38806402","isActive":1}],"count":8,"pageSize":10,"offset":0}
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
         * resultList : [{"id":9,"name":"广州五羊新城支行","city":"广州","address":"广州市越秀区寺右新马路124号102房","telphone":"020-87362803","isActive":1},{"id":10,"name":"广州赤岗支行","city":"广州","address":"广州市海珠区新港东路2-4号202房","telphone":"020-89237256","isActive":1},{"id":11,"name":"广州前进路支行","city":"广州","address":"广州市前进路44-50号首层之二铺","telphone":"020-84245097","isActive":1},{"id":14,"name":"广州天河北路支行","city":"广州","address":"广州市天河区天河北路芳草园583号","telphone":"020-38460815","isActive":1},{"id":15,"name":"广州天朗明居支行","city":"广州","address":"广州市天河区车陂路425号101-102铺、427号101-102铺、429号101铺、431号101铺、433号102铺","telphone":"020-38629491","isActive":1},{"id":16,"name":"广州鱼珠支行","city":"广州","address":"广州市天河区中山大道中1088号101房A102、A103商铺","telphone":"020-82295105","isActive":1},{"id":17,"name":"广州开发区西区支行","city":"广州","address":"广州市萝岗区开发大道234号","telphone":"020-82213846","isActive":1},{"id":18,"name":"广州天河东路支行","city":"广州","address":"广州市天河东路华康街2-4号首层","telphone":"020-38806402","isActive":1}]
         * count : 8
         * pageSize : 10
         * offset : 0
         */

        private int count;
        private int pageSize;
        private int offset;
        private List<ResultListBean> resultList;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public List<ResultListBean> getResultList() {
            return resultList;
        }

        public void setResultList(List<ResultListBean> resultList) {
            this.resultList = resultList;
        }

        public static class ResultListBean {
            /**
             * id : 9
             * name : 广州五羊新城支行
             * city : 广州
             * address : 广州市越秀区寺右新马路124号102房
             * telphone : 020-87362803
             * isActive : 1
             */

            private int id;
            private String name;
            private String city;
            private String address;
            private String telphone;
            private int isActive;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getTelphone() {
                return telphone;
            }

            public void setTelphone(String telphone) {
                this.telphone = telphone;
            }

            public int getIsActive() {
                return isActive;
            }

            public void setIsActive(int isActive) {
                this.isActive = isActive;
            }
        }
    }
}
