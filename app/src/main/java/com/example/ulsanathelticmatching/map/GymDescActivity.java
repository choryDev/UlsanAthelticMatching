package com.example.ulsanathelticmatching.map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.model.GymModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GymDescActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap; //구글맵객체
    Button webSieteBtn, mapBtn;
    GymModel item;
    private LocationManager lm;
    private double longitude; //위도
    private double latitude; //경도
    private final String pathmethod[] = {"CAR", "PUBLICTRANSIT", "FOOT"};
    private TextView tv_name, tv_locationName, tv_typeClassification, tv_weekEndTime,
                    tv_address, tv_closedDay, tv_starttime, tv_endtime, tv_weekStartTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_desc);


        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ///////////////////////구글 맵 부분///////////////////////////////////////////////
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map); //플래그먼트로 생성한 구글 맵 객체 xml과 연결
        mapFragment.getMapAsync(this); //구글맵 객체 동기화

        //////////////////////하단 버튼 2개 처리 부분//////////////////////////////////////
        webSieteBtn = (Button)findViewById(R.id.webSieteBtn);
        mapBtn = (Button)findViewById(R.id.mapBtn);

        //////////////////////인텐트로 받아온 체육관 정보 텍스트 뷰 연결
        Intent intent = getIntent(); //인텐트로 체육관 정보를 받아 옴
        item  = (GymModel)intent.getSerializableExtra("OBJECT");

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_locationName = (TextView) findViewById(R.id.tv_locationName);
        tv_typeClassification = (TextView) findViewById(R.id.typeClassification);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_closedDay = (TextView) findViewById(R.id.tv_closeDay);
        tv_starttime = (TextView) findViewById(R.id.tv_starttime);
        tv_endtime = (TextView) findViewById(R.id.tv_endtime);
        tv_weekEndTime = (TextView) findViewById(R.id.tv_weekEndTime);
        tv_weekStartTime = (TextView) findViewById(R.id.tv_weekStartTime);

        tv_name.setText(item.name);
        tv_locationName.setText(item.locationName);
        tv_typeClassification.setText(item.typeClassification);
        tv_address.setText(item.address);
        tv_closedDay.setText(item.closedDay);
        tv_starttime.setText(item.starttime);
        tv_endtime.setText(item.endtime);
        tv_weekEndTime.setText(item.weekEndTime);
        tv_weekStartTime.setText(item.weekStartTime);

        webSieteBtn.setOnClickListener(new View.OnClickListener() {
            @Override //해당 체육관 정보 알려주는 웹 사이트 보여 준다.
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GymWebActivity.class);
                intent.putExtra("name",item.locationName);
                startActivity(intent);
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openKakaoPath(view);
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;
        //구글 맵에 표시되는 위도 경도 체크
        LatLng gymLocation = new LatLng(Double.valueOf(item.latitude),Double.valueOf(item.longitude));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(gymLocation);
        markerOptions.title(item.locationName);//구글맵 마커에 보여지는 체육관 이름
        markerOptions.snippet(item.address); //구글맵 마커에 보여지는 체육관 주소

        mMap.addMarker(markerOptions); //주소 속성

        mMap.moveCamera(CameraUpdateFactory.newLatLng(gymLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10)); //처음에 줌 되는 정도
    }
    public void openKakaoPath(View view)//카카오 길찾기 다이얼로그 호출 함수
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("카카오 길 찾기");
        //길찾기 방법 url에 던질 문자열 배열
        builder.setItems(R.array.kakao_path_find_btn, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int pos)
            {   //현재 내위치 불러오기
                if ( Build.VERSION.SDK_INT >= 23 && //버전이 안맞을 경우 호출
                        ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions( GymDescActivity.this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                            0 );
                }
                else{
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    longitude = location.getLongitude(); //내위치 위도
                    latitude = location.getLatitude(); //내위치 경도
                }
                ////////////////////////카카오톡 길찾기 호출 부분////////////////////////////////////////
                String[] items = getResources().getStringArray(R.array.kakao_path_find_btn);
                String url = "daummaps://route?sp="
                        +latitude+","+longitude //내위치 경도 위도
                        +"&ep="+Double.valueOf(item.latitude)+","+Double.valueOf(item.longitude) //체육관 위치 경도 위도
                        +"&by="+pathmethod[pos]; //찾아가는 방법
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
}