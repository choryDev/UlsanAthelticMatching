package com.example.ulsanathelticmatching.model;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {//체팅방 객체
    public Map<String,Boolean> users = new HashMap<>(); //채팅방 유저들
    public Map<String,Comment> comments = new HashMap<>(); //채팅방 대화내용

    public static class Comment{
        public String uid;
        public String message;
        public Object timestamp;
    }

}
