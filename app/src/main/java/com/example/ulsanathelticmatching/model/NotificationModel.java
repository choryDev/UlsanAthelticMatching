package com.example.ulsanathelticmatching.model;

public class NotificationModel {//푸시 메시지를 보내는 데이터 모델 클래스
    public String to;
    public Notification notification = new Notification();
    public Data data = new Data();

    public static class Notification {
        public String title; //푸시알림 이름
        public String text; //푸시알림 내용
    }
    public static class Data{
        public String title;  //데이터 이름
        public String text;    //데이처 내용
    }
}
