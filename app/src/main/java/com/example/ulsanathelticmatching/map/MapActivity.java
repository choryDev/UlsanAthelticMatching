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
    //json으로 받아온 울산 운동장 리스트를 조회해주는 액티비티
    ArrayList<GymModel> gymModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        gymModels = new ArrayList<GymModel>(); // Json으로 받아올 객체를 어레이 리스트에 담음

        jsonParsing(getJsonString());//json객체를 불러오고 그 불러온 객체를 변환

        ListView listView = (ListView)findViewById(R.id.MapActivity_listview);

        final MapAdapters myAdapter = new MapAdapters(this,gymModels);//불러온 json객체를 어뎁터에 연결

        listView.setAdapter(myAdapter);// 리스트뷰에 어뎁터 연결

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override //리스트 뷰에 아이템을 선택하면 해당 아이템을 상세보기 액티비티로 넘긴다
            public void onItemClick(AdapterView parent, View v, int position, long id){
                Intent intent = new Intent(getApplicationContext(), GymDescripActivity.class); // 다음넘어갈 화면
                intent.putExtra("OBJECT", (Serializable) myAdapter.getItem(position));
                startActivity(intent);
            }
        });
    }

    private void jsonParsing(String json)
    {//String으로 이루어진 json을 받음
        try{
            JSONObject jsonObject = new JSONObject(json);//

            JSONArray array = jsonObject.getJSONArray("records"); //json객체의 records 컬럼을 가져온다

            for(int i=0; i<array.length(); i++)
            {//json객체의 records만큼 환인을 합니다
                JSONObject obj = array.getJSONObject(i);

                GymModel gymModel = new GymModel(); //GymModel객체에 담음

                gymModel.name = obj.getString("개방시설명");
                gymModel.locationName = obj.getString("개방장소명");
                gymModel.typeClassification = obj.getString("개방시설유형구분");
                gymModel.closedDay = obj.getString("휴관일");
                gymModel.starttime = obj.getString("평일운영시작시각");
                gymModel.endtime = obj.getString("평일운영종료시각");
                gymModel.weekStartTime = obj.getString("주말운영시작시각");
                gymModel.weekEndTime = obj.getString("주말운영종료시각");
                if(obj.has("소재지도로명주소")){ // 주소 컬럼이 2가지 방식인데 객체에 해당 컬럼이 없을 경우 해당 정보에서 끊겨서 더이상 못불러 옴
                    gymModel.address = obj.getString("소재지도로명주소");
                }else if(obj.has("소재지지번주소")){
                    gymModel.address = obj.getString("소재지지번주소");
                }else{
                    gymModel.address = "주소 정보가 없습니다";
                }
                gymModel.latitude = obj.getString("위도");
                gymModel.longitude = obj.getString("경도");
                gymModel.administrativeDistrict = obj.getString("행정구");

                gymModels.add(gymModel);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getJsonString() //json 파일을 불러오는 함수
    {
        String json = "";

        try {//저장된 json객체를 연다.
            InputStream is = getAssets().open("전국공공시설개방표준데이터울산.json");
            int fileSize = is.available();//json객체를 int형으로 변환

            byte[] buffer = new byte[fileSize]; //버퍼에 담음
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");//json객체를 utf형태로 문자열로 변환
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return json; //문자열로 변환된 json을 반환
    }
}