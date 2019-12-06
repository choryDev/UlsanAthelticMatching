package com.example.ulsanathelticmatching.main;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.alarm.AlarmActivity;
import com.example.ulsanathelticmatching.board.BoardAdapters;
import com.example.ulsanathelticmatching.board.BoardDescActivity;
import com.example.ulsanathelticmatching.board.WriteActivity;
import com.example.ulsanathelticmatching.chat.ChatActivity;
import com.example.ulsanathelticmatching.chat.Message2Activity;
import com.example.ulsanathelticmatching.map.MapActivity;
//import com.example.ulsanathelticmatching.chat.ChatActivity;
import com.example.ulsanathelticmatching.model.BoardItem;
import com.example.ulsanathelticmatching.myboard.MyBoardActivity;
import com.example.ulsanathelticmatching.sign.SignInActivity;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listView;

    private FirebaseAuth mAuth;

    private TextView authName;
    private TextView authEmail;
    private ImageView authAvatar;

    private Spinner sp_sport, sp_area;
    private List<BoardItem> boardItemslist;//게시판 리스트
    private ArrayList<BoardItem> spinerFilterlist = new ArrayList<BoardItem>();//스피너 필터 게시판 리스트
    BoardAdapters myadapter; //게시판을 연결하는 어뎁터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        listView = (ListView)findViewById(R.id.MainActivity_listview);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        sp_sport = (Spinner)findViewById(R.id.spi_sport); //운동 선택 스피너
        sp_area = (Spinner)findViewById(R.id.spi_area); //지역 선택 스피너

        boardItemslist = new ArrayList<>(); //firebase DB로 부터 게시글 배열을 담을 어레이 리스트

        //BoardItem객체로 부터 받아 온다
        FirebaseDatabase.getInstance().getReference().child("BoardItem").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boardItemslist.clear(); //어레이리스트 청소를 한번 하고 담는다. 안하면 이전 데이터가 계속 쌓인다
                for (DataSnapshot item : dataSnapshot.getChildren()) { //Db로 부터 받은 데이터를 Foreach문으로 사용할 수 있게 새로 담는다.
                    BoardItem boardItem = item.getValue(BoardItem.class);
                    //자신의 uid와 작성자 uid, 경기 상대편 uid를 비교하여
                    //만약 같을 경우 조회를 안한다
                    if(!mAuth.getUid().equals(boardItem.uid)&&!mAuth.getUid().equals(boardItem.rivaluid)){
                        boardItemslist.add(boardItem);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myadapter = new BoardAdapters(getApplicationContext(), R.layout.activity_board_items,boardItemslist);//게시판 어뎁터
        listView.setAdapter(myadapter);//게시판 어뎁터를 연결

        sp_sport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override//스피너 필터하는 부분
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!sp_sport.getSelectedItem().toString().equals("모든 운동")){//모든 운동이 아니면 필터
                    spinerFilterlist.clear();
                    for(BoardItem item : boardItemslist){
                        if(item.sports.equals(sp_sport.getSelectedItem().toString())){
                            spinerFilterlist.add(item);
                        }
                    }
                    myadapter.selectSports(spinerFilterlist);
                    Toast.makeText(getApplicationContext(), sp_sport.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                }else{
                    myadapter.selectSports(boardItemslist);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override//스피너 필터하는 부분
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!sp_area.getSelectedItem().toString().equals("모든 지역")){//모든 지역이 아니면 필터
                    spinerFilterlist.clear();
                    for(BoardItem item : boardItemslist){//if문으로 조건을 필터한다
                        if(item.area.equals(sp_area.getSelectedItem().toString())){
                            spinerFilterlist.add(item);
                        }
                    }
                    myadapter.selectArea(spinerFilterlist);
                    Toast.makeText(getApplicationContext(), sp_area.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                }else{
                    myadapter.selectArea(boardItemslist);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//리스트뷰 클릭 리스너
            @Override //리스트뷰안의 상세 정보를 보기 위해 한 리스트뷰의 정보를 가지고 인텐트로 넘어간다
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BoardDescActivity.class); // 다음넘어갈 화면
                intent.putExtra("OBJECT", (Serializable) myadapter.getItem(position));//Serializable을 상속받아 인텐트로 객체를 넘긴다
                startActivity(intent);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);//우측 네비게이션 연결

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            //동그런거 누르면 메뉴가 보여진다.
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        View view = navigationView.getHeaderView(0);

        authName = (TextView)view.findViewById(R.id.auth_name);
        authEmail = (TextView)view.findViewById(R.id.auth_email);
        authAvatar = (ImageView)view.findViewById(R.id.auth_avatar);
        authName.setText(mAuth.getCurrentUser().getDisplayName());
        authEmail.setText(mAuth.getCurrentUser().getEmail());

        Glide //url사진을 불러와야기 때문에 ImageView로 불러오지 못하여 Glide를 이용합니다.
                .with(this)
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .circleCrop()
                .placeholder(R.drawable.logo)
                .into(authAvatar);

       Log.d("에러 내용", String.valueOf(mAuth.getCurrentUser().getPhotoUrl()));
        passPushTokenToServer(); //토큰생성
    }

    @Override
    public void onBackPressed() {//네비게이션 메뉴를 옆으로 미는 함수
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_chat) {
            Intent i = new Intent(this, ChatActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_my) {
            Intent i = new Intent(this, MyBoardActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_write) {
            Intent i = new Intent(this, WriteActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_gym) {
            Intent i = new Intent(this, MapActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_alarm) {
            Intent i = new Intent(this, AlarmActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_send) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Intent i = new Intent(this, SignInActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //푸시알람을 위한 토큰생성
    void passPushTokenToServer(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //uid 담기
        String token = FirebaseInstanceId.getInstance().getToken(); //토큰 만들기

        //DB users의 각 값에 'pushToken : 토큰값' 형태의 hashmap으로 넣기
        Map<String,Object> map = new HashMap<>();
        map.put("pushToken",token);

        //DB에 토큰을 넣기(기존DB업데이트)
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
    }
}