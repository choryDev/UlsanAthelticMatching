package com.example.ulsanathelticmatching.model;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {//체팅방
    public Map<String,Boolean> users = new HashMap<>(); //채팅방 유저들
    public Map<String,Comment> comments = new HashMap<>(); //채팅방 대화내용

    public static class Comment{
        public String uid;  //메세지보낸 사람uid
        public String message;  //메세지 내용
        public Object timestamp;  //메세지 보낸 시간
    }

}
