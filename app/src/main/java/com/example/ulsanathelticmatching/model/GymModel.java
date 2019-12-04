package com.example.ulsanathelticmatching.model;

import java.io.Serializable;

public class GymModel implements Serializable {//체육관 객체
    public String name;//개방장소명
    public String locationName;//개방시설명
    public String typeClassification;//개방시설유형구분
    public String closedDay;//휴관일
    public String starttime;//평일운영시작시각
    public String endtime;//평일운영종료시각
    public String weekStartTime;//주말운영시작시각
    public String weekEndTime;//주말운영종료시각
    public String address;//소재지도로명주소
    public String latitude;//위도
    public String longitude;//경도
    public String administrativeDistrict;//행정구//이건 필터에서만 사용
}
