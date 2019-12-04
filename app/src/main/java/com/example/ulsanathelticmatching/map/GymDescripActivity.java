package com.example.ulsanathelticmatching.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.model.GymModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GymDescripActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static  final int REQUEST_LOCATION=1;
    private GoogleMap mMap; //구글맵객체
    Button webSieteBtn, mapBtn;
    GymModel item;
    LocationManager locationManager;
    private double latitude = 0.0,longitude = 0.0;
    private TextView tv_name, tv_locationName, tv_typeClassification, tv_weekEndTime,
            tv_address, tv_closedDay, tv_starttime, tv_endtime, tv_weekStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_descrip);

        ///////////////////////구글 맵 부분///////////////////////////////////////////////
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map); //플래그먼트로 생성한 구글 맵 객체 xml과 연결
        mapFragment.getMapAsync(this); //구글맵 객체 동기화

        ActivityCompat.requestPermissions(this,new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
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
            public void onClick(View v) {
                openKakaoPath(v);
            }
        });
    }
    public void openKakaoPath(View view){
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);//현재 위치 관리를 하는 객체 생성
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//GPS가 되는지 확인
                OnGPS(); //GPS를 사용 못하면 사용하게 하는 다이얼로그를 불러 온다
            }else {
                getLocation(); //GPS를 사용할 경우

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("카카오 길 찾기");
                //길찾기 방법 url에 던질 문자열 배열
                builder.setItems(R.array.kakao_path_find_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int pos) {
                        ////////////////////////카카오톡 길찾기 호출 부분////////////////////////////////////////
                        String[] items = getResources().getStringArray(R.array.kakao_path_find_btn);
                        final String pathmethod[] = {"CAR", "PUBLICTRANSIT", "FOOT"};
                        String url = "daummaps://route?sp="
                                + latitude + "," + longitude //내위치 경도 위도
                                + "&ep=" + Double.valueOf(item.latitude) + "," + Double.valueOf(item.longitude) //체육관 위치 경도 위도
                                + "&by=" + pathmethod[pos]; //찾아가는 방법
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));//카카오톡 url로 이동
                        startActivity(intent);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
    }
    private void getLocation() {
        //사용자 퍼미션 체크
        if (ActivityCompat.checkSelfPermission(GymDescripActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GymDescripActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }else {
            Location LocationGps= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //GPS에서 위치를 불러 온다
            Location LocationNetwork=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);// 전화기지국에서 위치를 불러 온다
            Location LocationPassive=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);//WiFi AP 에서 위치를 불러 온다

            if (LocationGps !=null) { //GPS가 null 아니면
                latitude=LocationGps.getLatitude();
                longitude=LocationGps.getLongitude();
            } else if (LocationNetwork !=null) {//전화기지국가 null 아니면
                latitude=LocationNetwork.getLatitude();
                longitude=LocationNetwork.getLongitude();
            } else if (LocationPassive !=null) {//WiFi AP가 null 아니면
                latitude=LocationPassive.getLatitude();
                longitude=LocationPassive.getLongitude();
            }else{
                Toast.makeText(this, "당신의 위치를 알 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage("GPS 사용하기").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
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
}