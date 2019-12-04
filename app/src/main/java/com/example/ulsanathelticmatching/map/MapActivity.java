package com.example.ulsanathelticmatching.map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.model.GymModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    ArrayList<GymModel> gymModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        gymModels = new ArrayList<GymModel>();

        jsonParsing(getJsonString());

        ListView listView = (ListView)findViewById(R.id.MapActivity_listview);

        final MapAdapters myAdapter = new MapAdapters(this,gymModels);

        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                Intent intent = new Intent(getApplicationContext(), GymDescActivity.class); // 다음넘어갈 화면
                intent.putExtra("OBJECT", (Serializable) myAdapter.getItem(position));
                startActivity(intent);
            }
        });
    }

    private void jsonParsing(String json)
    {
        try{
            JSONObject jsonObject = new JSONObject(json);

            JSONArray array = jsonObject.getJSONArray("records");

            for(int i=0; i<array.length(); i++)
            {
                JSONObject obj = array.getJSONObject(i);

                GymModel gymModel = new GymModel();

                gymModel.name = obj.getString("개방시설명");//개방장소명
                gymModel.locationName = obj.getString("개방장소명");//개방장소명
                gymModel.typeClassification = obj.getString("개방시설유형구분");//개방시설유형구분
                gymModel.closedDay = obj.getString("휴관일");//휴관일
                gymModel.starttime = obj.getString("평일운영시작시각");//평일운영시작시각
                gymModel.endtime = obj.getString("평일운영종료시각");//평일운영종료시각
                gymModel.weekStartTime = obj.getString("주말운영시작시각");//주말운영시작시각
                gymModel.weekEndTime = obj.getString("주말운영종료시각");//주말운영종료시각
                if(obj.has("소재지도로명주소")){ // 주소 컬럼이 2가지 방식인데 객체마다 컬럼 유무가 달라서
                    gymModel.address = obj.getString("소재지도로명주소");//소재지도로명주소
                }else if(obj.has("소재지지번주소")){
                    gymModel.address = obj.getString("소재지지번주소");//소재지도로명주소
                }else{
                    gymModel.address = "주소 정보가 없습니다";
                }
                gymModel.latitude = obj.getString("위도");//위도
                gymModel.longitude = obj.getString("경도");//경도
                gymModel.administrativeDistrict = obj.getString("행정구");//행정구//이건 필터에서만 사용

                gymModels.add(gymModel);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getJsonString() //json 파일을 불러오는 함수
    {
        String json = "";

        try {
            InputStream is = getAssets().open("전국공공시설개방표준데이터울산.json");
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return json;
    }
}