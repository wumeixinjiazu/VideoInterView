package com.videocomm.VideoInterView.bean;

import java.util.List;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/28 0028]
 * @function[功能简介 活体检测 bean]
 **/
public class LivingDetectionBean {

    /**
     * msg : 活体检测成功
     * content : {"faceLiveness":1,"dectectResult":true,"faceImageUrl":"74f904b2-d1c0-45b1-b6ed-67414b725ffb.jpg","faceList":[{"faceId":"3e7b0f60bfac8f94b6e4ba678720a92e","faceImgFile":null,"age":22,"gender":"male","withGlasses":false,"isFace":true,"completeness":1,"occlusion":0.02,"illumination":131,"faceLandmark":{"left_eye_center":[54.46,173.51],"left_eye_top":[47.24,160.48],"right_eye_top":[43.45,273.04],"left_eye_bottom":[63.99,170.18],"right_eye_bottom":[65.46,274.12],"right_eye_center":[54.12,271.28],"nose_center":[109.38,182.5],"mouse_center":[184.09,196.4],"mouse_top":[168.93,189.15],"mouse_bottom":[179.73,194.08],"yaw":39.91,"pitch":10.08,"roll":-3.58},"yaw":39.91,"pitch":10.08,"roll":-3.58,"width":274,"height":252,"blur":0}]}
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
         * faceLiveness : 1.0
         * dectectResult : true
         * faceImageUrl : 74f904b2-d1c0-45b1-b6ed-67414b725ffb.jpg
         * faceList : [{"faceId":"3e7b0f60bfac8f94b6e4ba678720a92e","faceImgFile":null,"age":22,"gender":"male","withGlasses":false,"isFace":true,"completeness":1,"occlusion":0.02,"illumination":131,"faceLandmark":{"left_eye_center":[54.46,173.51],"left_eye_top":[47.24,160.48],"right_eye_top":[43.45,273.04],"left_eye_bottom":[63.99,170.18],"right_eye_bottom":[65.46,274.12],"right_eye_center":[54.12,271.28],"nose_center":[109.38,182.5],"mouse_center":[184.09,196.4],"mouse_top":[168.93,189.15],"mouse_bottom":[179.73,194.08],"yaw":39.91,"pitch":10.08,"roll":-3.58},"yaw":39.91,"pitch":10.08,"roll":-3.58,"width":274,"height":252,"blur":0}]
         */

        private double faceLiveness;
        private boolean dectectResult;
        private String faceImageUrl;
        private List<FaceListBean> faceList;

        public double getFaceLiveness() {
            return faceLiveness;
        }

        public void setFaceLiveness(double faceLiveness) {
            this.faceLiveness = faceLiveness;
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
             * faceId : 3e7b0f60bfac8f94b6e4ba678720a92e
             * faceImgFile : null
             * age : 22
             * gender : male
             * withGlasses : false
             * isFace : true
             * completeness : 1
             * occlusion : 0.02
             * illumination : 131.0
             * faceLandmark : {"left_eye_center":[54.46,173.51],"left_eye_top":[47.24,160.48],"right_eye_top":[43.45,273.04],"left_eye_bottom":[63.99,170.18],"right_eye_bottom":[65.46,274.12],"right_eye_center":[54.12,271.28],"nose_center":[109.38,182.5],"mouse_center":[184.09,196.4],"mouse_top":[168.93,189.15],"mouse_bottom":[179.73,194.08],"yaw":39.91,"pitch":10.08,"roll":-3.58}
             * yaw : 39.91
             * pitch : 10.08
             * roll : -3.58
             * width : 274.0
             * height : 252.0
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
            private double illumination;
            private FaceLandmarkBean faceLandmark;
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

            public double getIllumination() {
                return illumination;
            }

            public void setIllumination(double illumination) {
                this.illumination = illumination;
            }

            public FaceLandmarkBean getFaceLandmark() {
                return faceLandmark;
            }

            public void setFaceLandmark(FaceLandmarkBean faceLandmark) {
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

            public static class FaceLandmarkBean {
                /**
                 * left_eye_center : [54.46,173.51]
                 * left_eye_top : [47.24,160.48]
                 * right_eye_top : [43.45,273.04]
                 * left_eye_bottom : [63.99,170.18]
                 * right_eye_bottom : [65.46,274.12]
                 * right_eye_center : [54.12,271.28]
                 * nose_center : [109.38,182.5]
                 * mouse_center : [184.09,196.4]
                 * mouse_top : [168.93,189.15]
                 * mouse_bottom : [179.73,194.08]
                 * yaw : 39.91
                 * pitch : 10.08
                 * roll : -3.58
                 */

                private double yaw;
                private double pitch;
                private double roll;
                private List<Double> left_eye_center;
                private List<Double> left_eye_top;
                private List<Double> right_eye_top;
                private List<Double> left_eye_bottom;
                private List<Double> right_eye_bottom;
                private List<Double> right_eye_center;
                private List<Double> nose_center;
                private List<Double> mouse_center;
                private List<Double> mouse_top;
                private List<Double> mouse_bottom;

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

                public List<Double> getLeft_eye_center() {
                    return left_eye_center;
                }

                public void setLeft_eye_center(List<Double> left_eye_center) {
                    this.left_eye_center = left_eye_center;
                }

                public List<Double> getLeft_eye_top() {
                    return left_eye_top;
                }

                public void setLeft_eye_top(List<Double> left_eye_top) {
                    this.left_eye_top = left_eye_top;
                }

                public List<Double> getRight_eye_top() {
                    return right_eye_top;
                }

                public void setRight_eye_top(List<Double> right_eye_top) {
                    this.right_eye_top = right_eye_top;
                }

                public List<Double> getLeft_eye_bottom() {
                    return left_eye_bottom;
                }

                public void setLeft_eye_bottom(List<Double> left_eye_bottom) {
                    this.left_eye_bottom = left_eye_bottom;
                }

                public List<Double> getRight_eye_bottom() {
                    return right_eye_bottom;
                }

                public void setRight_eye_bottom(List<Double> right_eye_bottom) {
                    this.right_eye_bottom = right_eye_bottom;
                }

                public List<Double> getRight_eye_center() {
                    return right_eye_center;
                }

                public void setRight_eye_center(List<Double> right_eye_center) {
                    this.right_eye_center = right_eye_center;
                }

                public List<Double> getNose_center() {
                    return nose_center;
                }

                public void setNose_center(List<Double> nose_center) {
                    this.nose_center = nose_center;
                }

                public List<Double> getMouse_center() {
                    return mouse_center;
                }

                public void setMouse_center(List<Double> mouse_center) {
                    this.mouse_center = mouse_center;
                }

                public List<Double> getMouse_top() {
                    return mouse_top;
                }

                public void setMouse_top(List<Double> mouse_top) {
                    this.mouse_top = mouse_top;
                }

                public List<Double> getMouse_bottom() {
                    return mouse_bottom;
                }

                public void setMouse_bottom(List<Double> mouse_bottom) {
                    this.mouse_bottom = mouse_bottom;
                }
            }
        }
    }
}
