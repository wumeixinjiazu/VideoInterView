package com.videocomm.VideoInterView.bean;

import java.util.List;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/23 0023]
 * @function[功能简介 人脸识别bean]
 **/
public class IdentityFaceBean {

    /**
     * msg : 人脸识别成功
     * content : {"faceCount":1,"dectectResult":true,"faceImageUrl":"f6dd516d-f272-49c5-8473-31f2ce40dc41.jpg","faceList":[{"faceId":"13dc455eca2b9a1f0ce78646842bf7fd","faceImgFile":null,"age":26,"gender":"male","withGlasses":false,"isFace":true,"completeness":1,"occlusion":0.1557142857142857,"illumination":null,"faceLandmark":null,"yaw":-2.76,"pitch":-21.24,"roll":2.68,"width":236,"height":227,"blur":0}]}
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
         * faceCount : 1
         * dectectResult : true
         * faceImageUrl : f6dd516d-f272-49c5-8473-31f2ce40dc41.jpg
         * faceList : [{"faceId":"13dc455eca2b9a1f0ce78646842bf7fd","faceImgFile":null,"age":26,"gender":"male","withGlasses":false,"isFace":true,"completeness":1,"occlusion":0.1557142857142857,"illumination":null,"faceLandmark":null,"yaw":-2.76,"pitch":-21.24,"roll":2.68,"width":236,"height":227,"blur":0}]
         */

        private int faceCount;
        private boolean dectectResult;
        private String faceImageUrl;
        private List<FaceListBean> faceList;

        public int getFaceCount() {
            return faceCount;
        }

        public void setFaceCount(int faceCount) {
            this.faceCount = faceCount;
        }

        public boolean isDectectResult() {
            return dectectResult;
        }

        public void setDectectResult(boolean dectectResult) {
            this.dectectResult = dectectResult;
        }

        public String getFaceImageUrl() {
            return faceImageUrl;
        }

        public void setFaceImageUrl(String faceImageUrl) {
            this.faceImageUrl = faceImageUrl;
        }

        public List<FaceListBean> getFaceList() {
            return faceList;
        }

        public void setFaceList(List<FaceListBean> faceList) {
            this.faceList = faceList;
        }

        public static class FaceListBean {
            /**
             * faceId : 13dc455eca2b9a1f0ce78646842bf7fd
             * faceImgFile : null
             * age : 26
             * gender : male
             * withGlasses : false
             * isFace : true
             * completeness : 1
             * occlusion : 0.1557142857142857
             * illumination : null
             * faceLandmark : null
             * yaw : -2.76
             * pitch : -21.24
             * roll : 2.68
             * width : 236.0
             * height : 227.0
             * blur : 0.0
             */

            private String faceId;
            private Object faceImgFile;
            private int age;
            private String gender;
            private boolean withGlasses;
            private boolean isFace;
            private int completeness;
            private double occlusion;
            private Object illumination;
            private Object faceLandmark;
            private double yaw;
            private double pitch;
            private double roll;
            private double width;
            private double height;
            private double blur;

            public String getFaceId() {
                return faceId;
            }

            public void setFaceId(String faceId) {
                this.faceId = faceId;
            }

            public Object getFaceImgFile() {
                return faceImgFile;
            }

            public void setFaceImgFile(Object faceImgFile) {
                this.faceImgFile = faceImgFile;
            }

            public int getAge() {
                return age;
            }

            public void setAge(int age) {
                this.age = age;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public boolean isWithGlasses() {
                return withGlasses;
            }

            public void setWithGlasses(boolean withGlasses) {
                this.withGlasses = withGlasses;
            }

            public boolean isIsFace() {
                return isFace;
            }

            public void setIsFace(boolean isFace) {
                this.isFace = isFace;
            }

            public int getCompleteness() {
                return completeness;
            }

            public void setCompleteness(int completeness) {
                this.completeness = completeness;
            }

            public double getOcclusion() {
                return occlusion;
            }

            public void setOcclusion(double occlusion) {
                this.occlusion = occlusion;
            }

            public Object getIllumination() {
                return illumination;
            }

            public void setIllumination(Object illumination) {
                this.illumination = illumination;
            }

            public Object getFaceLandmark() {
                return faceLandmark;
            }

            public void setFaceLandmark(Object faceLandmark) {
                this.faceLandmark = faceLandmark;
            }

            public double getYaw() {
                return yaw;
            }

            public void setYaw(double yaw) {
                this.yaw = yaw;
            }

            public double getPitch() {
                return pitch;
            }

            public void setPitch(double pitch) {
                this.pitch = pitch;
            }

            public double getRoll() {
                return roll;
            }

            public void setRoll(double roll) {
                this.roll = roll;
            }

            public double getWidth() {
                return width;
            }

            public void setWidth(double width) {
                this.width = width;
            }

            public double getHeight() {
                return height;
            }

            public void setHeight(double height) {
                this.height = height;
            }

            public double getBlur() {
                return blur;
            }

            public void setBlur(double blur) {
                this.blur = blur;
            }
        }
    }
}
