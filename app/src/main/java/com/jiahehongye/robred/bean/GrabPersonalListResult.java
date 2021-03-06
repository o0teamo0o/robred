package com.jiahehongye.robred.bean;

import java.util.List;

/**
 * Created by huangjunhui on 2016/12/27.14:18
 */
public class GrabPersonalListResult {
    /**
     * result : success
     * data : {"sendRedEnveList":[{"redEnveMoney":"1.00","remainRedEnveSum":"0","memberPersonalDescription":"","redEnveMode":"1","redEnveNum":"1","userPhoto":"http://img1.jinhoubaobao.com/group1/M00/00/00/dwozhlawM5WAYdrdAABCatO3X_A45.jpeg","userProp":"1","grabId":"","redEnveMark":"4","grabUser":"","sendRedEnveDate":"2017-02-20 15:48:46","auditStatus":"3","personal":"0","redEnveCode":"HBA01148757691936515793","USER_LEVEL":"4","nickName":"宝宝心里苦","redEnveLeaveword":"恭喜发财，大吉大利","memberGender":"1","memberMobile":"15933434904","memberId":"A01145438652011443124","createDate":"2017-02-20 15:48:39"},{"redEnveMoney":"1.00","remainRedEnveSum":"0","memberPersonalDescription":"Qwe","redEnveMode":"1","redEnveNum":"1","userPhoto":"http://img1.jinhoubaobao.com/group1/M00/00/06/ezjIvVhfgQSAYBsTAAAlgHSRpss87.jpeg","userProp":"1","grabId":"b784c71af74211e690b4842b2b8fd827","redEnveMark":"3","grabUser":"A01148301075279766489","sendRedEnveDate":"2017-02-20 15:37:09","auditStatus":"3","personal":"0","redEnveCode":"HBA01148757622385579244","USER_LEVEL":"1","nickName":"dddhg","redEnveLeaveword":"恭喜发财，大吉大利","memberGender":"1","memberMobile":"18618348641","memberId":"A01147998463496632337","createDate":"2017-02-20 15:37:04"},{"redEnveMoney":"0.10","remainRedEnveSum":"0","memberPersonalDescription":"Qwe","redEnveMode":"1","redEnveNum":"1","userPhoto":"http://img1.jinhoubaobao.com/group1/M00/00/06/ezjIvVhfgQSAYBsTAAAlgHSRpss87.jpeg","userProp":"1","grabId":"bd54f329f74211e690b4842b2b8fd827","redEnveMark":"3","grabUser":"A01148301075279766489","sendRedEnveDate":"2017-02-20 15:42:02","auditStatus":"3","personal":"0","redEnveCode":"HBA01148757651630981565","USER_LEVEL":"1","nickName":"dddhg","redEnveLeaveword":"恭喜发财，大吉大利","memberGender":"1","memberMobile":"18618348641","memberId":"A01147998463496632337","createDate":"2017-02-20 15:41:56"},{"redEnveMoney":"0.10","remainRedEnveSum":"0","memberPersonalDescription":"Qwe","redEnveMode":"1","redEnveNum":"1","userPhoto":"http://img1.jinhoubaobao.com/group1/M00/00/06/ezjIvVhfgQSAYBsTAAAlgHSRpss87.jpeg","userProp":"1","grabId":"baeb3cf2f74211e690b4842b2b8fd827","redEnveMark":"3","grabUser":"A01148301075279766489","sendRedEnveDate":"2017-02-20 15:38:22","auditStatus":"3","personal":"0","redEnveCode":"HBA01148757629608158599","USER_LEVEL":"1","nickName":"dddhg","redEnveLeaveword":"恭喜发财，大吉大利","memberGender":"1","memberMobile":"18618348641","memberId":"A01147998463496632337","createDate":"2017-02-20 15:38:16"}]}
     */

    private String result;
    private DataBean data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * redEnveMoney : 1.00
         * remainRedEnveSum : 0
         * memberPersonalDescription :
         * redEnveMode : 1
         * redEnveNum : 1
         * userPhoto : http://img1.jinhoubaobao.com/group1/M00/00/00/dwozhlawM5WAYdrdAABCatO3X_A45.jpeg
         * userProp : 1
         * grabId :
         * redEnveMark : 4
         * grabUser :
         * sendRedEnveDate : 2017-02-20 15:48:46
         * auditStatus : 3
         * personal : 0
         * redEnveCode : HBA01148757691936515793
         * USER_LEVEL : 4
         * nickName : 宝宝心里苦
         * redEnveLeaveword : 恭喜发财，大吉大利
         * memberGender : 1
         * memberMobile : 15933434904
         * memberId : A01145438652011443124
         * createDate : 2017-02-20 15:48:39
         */

        private List<SendRedEnveListBean> sendRedEnveList;

        public List<SendRedEnveListBean> getSendRedEnveList() {
            return sendRedEnveList;
        }

        public void setSendRedEnveList(List<SendRedEnveListBean> sendRedEnveList) {
            this.sendRedEnveList = sendRedEnveList;
        }

        public static class SendRedEnveListBean {
            private String redEnveMoney;
            private String remainRedEnveSum;
            private String memberPersonalDescription;
            private String redEnveMode;
            private String redEnveNum;
            private String userPhoto;
            private String userProp;
            private String grabId;
            private String redEnveMark;
            private String grabUser;
            private String sendRedEnveDate;
            private String auditStatus;
            private String personal;
            private String redEnveCode;
            private String USER_LEVEL;
            private String nickName;
            private String redEnveLeaveword;
            private String memberGender;
            private String memberMobile;
            private String memberId;
            private String createDate;

            public String getRedEnveMoney() {
                return redEnveMoney;
            }

            public void setRedEnveMoney(String redEnveMoney) {
                this.redEnveMoney = redEnveMoney;
            }

            public String getRemainRedEnveSum() {
                return remainRedEnveSum;
            }

            public void setRemainRedEnveSum(String remainRedEnveSum) {
                this.remainRedEnveSum = remainRedEnveSum;
            }

            public String getMemberPersonalDescription() {
                return memberPersonalDescription;
            }

            public void setMemberPersonalDescription(String memberPersonalDescription) {
                this.memberPersonalDescription = memberPersonalDescription;
            }

            public String getRedEnveMode() {
                return redEnveMode;
            }

            public void setRedEnveMode(String redEnveMode) {
                this.redEnveMode = redEnveMode;
            }

            public String getRedEnveNum() {
                return redEnveNum;
            }

            public void setRedEnveNum(String redEnveNum) {
                this.redEnveNum = redEnveNum;
            }

            public String getUserPhoto() {
                return userPhoto;
            }

            public void setUserPhoto(String userPhoto) {
                this.userPhoto = userPhoto;
            }

            public String getUserProp() {
                return userProp;
            }

            public void setUserProp(String userProp) {
                this.userProp = userProp;
            }

            public String getGrabId() {
                return grabId;
            }

            public void setGrabId(String grabId) {
                this.grabId = grabId;
            }

            public String getRedEnveMark() {
                return redEnveMark;
            }

            public void setRedEnveMark(String redEnveMark) {
                this.redEnveMark = redEnveMark;
            }

            public String getGrabUser() {
                return grabUser;
            }

            public void setGrabUser(String grabUser) {
                this.grabUser = grabUser;
            }

            public String getSendRedEnveDate() {
                return sendRedEnveDate;
            }

            public void setSendRedEnveDate(String sendRedEnveDate) {
                this.sendRedEnveDate = sendRedEnveDate;
            }

            public String getAuditStatus() {
                return auditStatus;
            }

            public void setAuditStatus(String auditStatus) {
                this.auditStatus = auditStatus;
            }

            public String getPersonal() {
                return personal;
            }

            public void setPersonal(String personal) {
                this.personal = personal;
            }

            public String getRedEnveCode() {
                return redEnveCode;
            }

            public void setRedEnveCode(String redEnveCode) {
                this.redEnveCode = redEnveCode;
            }

            public String getUSER_LEVEL() {
                return USER_LEVEL;
            }

            public void setUSER_LEVEL(String USER_LEVEL) {
                this.USER_LEVEL = USER_LEVEL;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getRedEnveLeaveword() {
                return redEnveLeaveword;
            }

            public void setRedEnveLeaveword(String redEnveLeaveword) {
                this.redEnveLeaveword = redEnveLeaveword;
            }

            public String getMemberGender() {
                return memberGender;
            }

            public void setMemberGender(String memberGender) {
                this.memberGender = memberGender;
            }

            public String getMemberMobile() {
                return memberMobile;
            }

            public void setMemberMobile(String memberMobile) {
                this.memberMobile = memberMobile;
            }

            public String getMemberId() {
                return memberId;
            }

            public void setMemberId(String memberId) {
                this.memberId = memberId;
            }

            public String getCreateDate() {
                return createDate;
            }

            public void setCreateDate(String createDate) {
                this.createDate = createDate;
            }
        }
    }


//    /**
//     * result : success
//     * data : {"sendRedEnveList":[{"redEnveMoney":"50.00","remainRedEnveSum":"8","redEnveNum":"10","redEnveMode":"1","userPhoto":"http://img1.jinhoubaobao.com/group1/M00/00/00/dwozhlawM5WAYdrdAABCatO3X_A45.jpeg","userProp":"1","grabId":"","redEnveMark":"2","grabUser":"","sendRedEnveDate":"2016-12-27 11:08:40","auditStatus":"3","personal":"0","redEnveCode":"HBA01148280811207014536","USER_LEVEL":"1","nickName":"宝宝心里苦","redEnveLeaveword":"恭喜发财，大吉大利","ID":"A01145438652011443124","createDate":"2016-12-27 11:08:32"},{"redEnveMoney":"2.00","remainRedEnveSum":"0","redEnveNum":"2","redEnveMode":"1","userPhoto":"http://img1.jinhoubaobao.com/group1/M00/00/00/dwozhlawM5WAYdrdAABCatO3X_A45.jpeg","userProp":"1","grabId":"","redEnveMark":"4","grabUser":"","sendRedEnveDate":"2016-12-27 13:30:26","auditStatus":"3","personal":"0","redEnveCode":"HBA01148281661878638541","USER_LEVEL":"1","nickName":"宝宝心里苦","redEnveLeaveword":"恭喜发财，大吉大利","ID":"A01145438652011443124","createDate":"2016-12-27 13:30:19"},{"redEnveMoney":"1.00","remainRedEnveSum":"0","redEnveNum":"1","redEnveMode":"0","userPhoto":"http://img1.jinhoubaobao.com/group1/M00/00/01/ezjIvVc1kaSAbCGtAAE7rFuUTcQ599.jpg","userProp":"1","grabId":"","redEnveMark":"4","grabUser":"","sendRedEnveDate":"2016-12-26 09:45:03","auditStatus":"3","personal":"1","redEnveCode":"HBA01148271667325425183","USER_LEVEL":"1","nickName":"误杀咯","redEnveLeaveword":"www.baidu.com","ID":"A01145439046434062291","createDate":"2016-12-26 09:44:33"},{"redEnveMoney":"0.10","remainRedEnveSum":"0","redEnveNum":"1","redEnveMode":"1","userPhoto":"http://192.168.0.106:8080/upload/image/20161226/6ac0fa94047a42909dc705b412a087d5.jpeg","userProp":"1","grabId":"","redEnveMark":"4","grabUser":"","sendRedEnveDate":"2016-12-27 10:51:57","auditStatus":"3","personal":"0","redEnveCode":"HBA01148280710973285919","USER_LEVEL":"1","nickName":"Hhh","redEnveLeaveword":"恭喜发财，大吉大利","ID":"A01148213130517368397","createDate":"2016-12-27 10:51:50"}]}
//     */
//
//    private String result;
//    private DataBean data;
//
//    public String getResult() {
//        return result;
//    }
//
//    public void setResult(String result) {
//        this.result = result;
//    }
//
//    public DataBean getData() {
//        return data;
//    }
//
//    public void setData(DataBean data) {
//        this.data = data;
//    }
//
//    public static class DataBean {
//        /**
//         * redEnveMoney : 50.00
//         * remainRedEnveSum : 8
//         * redEnveNum : 10
//         * redEnveMode : 1
//         * userPhoto : http://img1.jinhoubaobao.com/group1/M00/00/00/dwozhlawM5WAYdrdAABCatO3X_A45.jpeg
//         * userProp : 1
//         * grabId :
//         * redEnveMark : 2
//         * grabUser :
//         * sendRedEnveDate : 2016-12-27 11:08:40
//         * auditStatus : 3
//         * personal : 0
//         * redEnveCode : HBA01148280811207014536
//         * USER_LEVEL : 1
//         * nickName : 宝宝心里苦
//         * redEnveLeaveword : 恭喜发财，大吉大利
//         * ID : A01145438652011443124
//         * createDate : 2016-12-27 11:08:32
//         */
//
//        private List<SendRedEnveListBean> sendRedEnveList;
//
//        public List<SendRedEnveListBean> getSendRedEnveList() {
//            return sendRedEnveList;
//        }
//
//        public void setSendRedEnveList(List<SendRedEnveListBean> sendRedEnveList) {
//            this.sendRedEnveList = sendRedEnveList;
//        }
//
//        public static class SendRedEnveListBean {
//            private String redEnveMoney;
//            private String remainRedEnveSum;
//            private String redEnveNum;
//            private String redEnveMode;
//            private String userPhoto;
//            private String userProp;
//            private String grabId;
//            private String redEnveMark;
//            private String grabUser;
//            private String sendRedEnveDate;
//            private String auditStatus;
//            private String personal;
//            private String redEnveCode;
//            private String USER_LEVEL;
//            private String nickName;
//            private String redEnveLeaveword;
//            private String ID;
//            private String createDate;
//
//            public String getRedEnveMoney() {
//                return redEnveMoney;
//            }
//
//            public void setRedEnveMoney(String redEnveMoney) {
//                this.redEnveMoney = redEnveMoney;
//            }
//
//            public String getRemainRedEnveSum() {
//                return remainRedEnveSum;
//            }
//
//            public void setRemainRedEnveSum(String remainRedEnveSum) {
//                this.remainRedEnveSum = remainRedEnveSum;
//            }
//
//            public String getRedEnveNum() {
//                return redEnveNum;
//            }
//
//            public void setRedEnveNum(String redEnveNum) {
//                this.redEnveNum = redEnveNum;
//            }
//
//            public String getRedEnveMode() {
//                return redEnveMode;
//            }
//
//            public void setRedEnveMode(String redEnveMode) {
//                this.redEnveMode = redEnveMode;
//            }
//
//            public String getUserPhoto() {
//                return userPhoto;
//            }
//
//            public void setUserPhoto(String userPhoto) {
//                this.userPhoto = userPhoto;
//            }
//
//            public String getUserProp() {
//                return userProp;
//            }
//
//            public void setUserProp(String userProp) {
//                this.userProp = userProp;
//            }
//
//            public String getGrabId() {
//                return grabId;
//            }
//
//            public void setGrabId(String grabId) {
//                this.grabId = grabId;
//            }
//
//            public String getRedEnveMark() {
//                return redEnveMark;
//            }
//
//            public void setRedEnveMark(String redEnveMark) {
//                this.redEnveMark = redEnveMark;
//            }
//
//            public String getGrabUser() {
//                return grabUser;
//            }
//
//            public void setGrabUser(String grabUser) {
//                this.grabUser = grabUser;
//            }
//
//            public String getSendRedEnveDate() {
//                return sendRedEnveDate;
//            }
//
//            public void setSendRedEnveDate(String sendRedEnveDate) {
//                this.sendRedEnveDate = sendRedEnveDate;
//            }
//
//            public String getAuditStatus() {
//                return auditStatus;
//            }
//
//            public void setAuditStatus(String auditStatus) {
//                this.auditStatus = auditStatus;
//            }
//
//            public String getPersonal() {
//                return personal;
//            }
//
//            public void setPersonal(String personal) {
//                this.personal = personal;
//            }
//
//            public String getRedEnveCode() {
//                return redEnveCode;
//            }
//
//            public void setRedEnveCode(String redEnveCode) {
//                this.redEnveCode = redEnveCode;
//            }
//
//            public String getUSER_LEVEL() {
//                return USER_LEVEL;
//            }
//
//            public void setUSER_LEVEL(String USER_LEVEL) {
//                this.USER_LEVEL = USER_LEVEL;
//            }
//
//            public String getNickName() {
//                return nickName;
//            }
//
//            public void setNickName(String nickName) {
//                this.nickName = nickName;
//            }
//
//            public String getRedEnveLeaveword() {
//                return redEnveLeaveword;
//            }
//
//            public void setRedEnveLeaveword(String redEnveLeaveword) {
//                this.redEnveLeaveword = redEnveLeaveword;
//            }
//
//            public String getID() {
//                return ID;
//            }
//
//            public void setID(String ID) {
//                this.ID = ID;
//            }
//
//            public String getCreateDate() {
//                return createDate;
//            }
//
//            public void setCreateDate(String createDate) {
//                this.createDate = createDate;
//            }
//        }
//    }


}
