package com.example.ulsanathelticmatching.model;

public class NotificationModel {
    public String to;
    public Notification notification = new Notification();
    public Data data = new Data();

    public class Notification {
        public String title;
        public String text;
    }
    public static class Data{
        public String title;
        public String text;
    }
}
