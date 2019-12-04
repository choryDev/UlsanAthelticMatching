package com.example.ulsanathelticmatching.model;

import java.io.Serializable;

public class BoardItem implements Serializable {
    public String primarykey; //식별자
    public String uid; //게시글을 올린 사람의 식별자
    public String rivaluid = ""; //상대 편의 식별자를 담을 객체
    public String name;//게시글을 올린 사람의 이름
    public String title; //게시글 제목
    public String date; //게시글 날자
    public String content; //게시글 내용
    public String sports; //게시글 운동 장르
    public String area; //게시글 지역
    public String img; //게시글에 보여질 사진
}