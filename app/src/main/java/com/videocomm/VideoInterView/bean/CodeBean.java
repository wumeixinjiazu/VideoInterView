package com.videocomm.VideoInterView.bean;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/15 0015]
 * @function[功能简介 图形验证码 发送验证码接口 返回的JSON]
 **/
public class CodeBean {

    /**
     * msg : 验证码超时
     * content : null
     * errorcode : 1
     */

    private String msg;
    private Object content;
    private int errorcode;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }
}
