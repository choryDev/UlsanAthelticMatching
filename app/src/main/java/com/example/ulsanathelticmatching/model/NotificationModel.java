package com.example.ulsanathelticmatching.model;

public class NotificationModel {
    public String to;
    public Notification notification;

    public class Notification {
        public String title;
        public String uid;
        public String text;
    }
}
