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
import com.example.ulsanathelticmatching.myboard.MyBoardActivity;
import com.example.ulsanathelticmatching.sign.SignInActivity;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listView;

    private FirebaseAuth mAuth;

    private TextView authName;
    private TextView authEmail;
    private ImageView authAvatar;

    private Spinner sp_sport, sp_area;

    BoardAdapters myadapter;
    ArrayAdapter<CharSequence> spSport, spArea;
    private String select_sport,select_area;


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

        sp_sport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                view.get
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), sp_area.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        myadapter = new BoardAdapters(getApplicationContext(), R.layout.activity_board_items,select_area,select_sport);
        listView.setAdapter(myadapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BoardDescActivity.class); // 다음넘어갈 화면
                intent.putExtra("OBJECT", (Serializable) myadapter.getItem(position));
                startActivity(intent);
            }
        });


        navigationView.setNavigationItemSelectedListener(this);

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

        Glide
                .with(this)
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .circleCrop()
                .placeholder(R.drawable.logo)
                .into(authAvatar);

       Log.d("에러 내용", String.valueOf(mAuth.getCurrentUser().getPhotoUrl()));
        passPushTokenToServer();

    }

    @Override
    public void onBackPressed() {
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

    //푸시알람
    void passPushTokenToServer(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //uid 담기
        String token = FirebaseInstanceId.getInstance().getToken(); //토큰 만들기

        //DB users의 각 값에 'pushToken : 토큰값' 형태로 넣기
        Map<String,Object> map = new HashMap<>();
        map.put("pushToken",token);

        //DB에 토큰넣기
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);

    }
}