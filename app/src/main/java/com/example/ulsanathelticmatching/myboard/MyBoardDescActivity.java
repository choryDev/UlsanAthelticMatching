package com.example.ulsanathelticmatching.myboard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.chat.Message2Activity;
import com.example.ulsanathelticmatching.model.BoardItem;
import com.example.ulsanathelticmatching.model.ChatModel;
import com.example.ulsanathelticmatching.model.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBoardDescActivity extends AppCompatActivity {
    //내 게시판 아이템의 상세정보 액티비티
    private BoardItem item;
    private TextView sport, area, title, date, name, content, rival_name;
    private ImageView image, rival_avatar;

    private String uid; //내 식별자 uid

    private Button chatBtn;

    private ArrayList<String> livalNameLists = new ArrayList<>(); //나와 채팅을 한 상대 이름 문자열 리스트
    private ArrayList<String> livalUidLists = new ArrayList<>(); //나와 채팅을 한 상대 식별자 문자열 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myboard_desc);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //내식별자 uid 생성
        //나와 매칭을 할 수 있는 사람은 나와 채팅을 한적 있는 사람
        //내가 소속된 방을 찾음
        //채팅방안의 유저 중에 내가 있으면 그 채팅방 배열을 가져온다
        FirebaseDatabase.getInstance().getReference().child("chatrooms") ///채팅방을 가져 옴 그러나 그 채팅방에 내가 있어야 가져온다
                .orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {//내가 포함된 채팅방 배열 확인
                    //데이터 쌓아놓기
                    for (String user : item.getValue(ChatModel.class).users.keySet()) {//uid는 hashmap으로 저장되어 이렇게 확인
                        if(!uid.equals(user)){//채팅을 2인이서 할 수 있어서 내가 아닌 다른사람이 상대방이므로 체크한다
                            FirebaseDatabase.getInstance().getReference().child("users").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override //DB에 접근하여 상대방 정보 가져옴
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                    livalNameLists.add(userModel.userName); //DB에 접근하여 상대방 이름 넣음 가져옴
                                    livalUidLists.add(userModel.uid); //DB에 접근하여 상대방 식별자 uid 넣음 가져옴
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Intent intent = getIntent();
        item  = (BoardItem)intent.getSerializableExtra("OBJECT");

        image = (ImageView)findViewById(R.id.avatar);
        sport = (TextView)findViewById(R.id.sports);
        area = (TextView)findViewById(R.id.area);
        title = (TextView)findViewById(R.id.title);
        date = (TextView)findViewById(R.id.date);
        name = (TextView)findViewById(R.id.name);
        content = (TextView)findViewById(R.id.content);

        rival_name = (TextView)findViewById(R.id.rival_name);
        rival_avatar = (ImageView)findViewById(R.id.rival_avatar);

        chatBtn = (Button)findViewById(R.id.btn_chat);

        Glide
                .with(this)
                .load(Uri.parse(item.img))
                .circleCrop()
                .placeholder(R.drawable.logo)
                .into(image);
        sport.setText(item.sports);
        area.setText(item.area);
        title.setText(item.title);
        date.setText(item.date);
        name.setText(item.name);
        content.setText(item.content);

        //경기의 상대방이 정해진 경우 상대편에 누구인지 띄운다
        //만약 상대방이 ""이면 프로필사진와 이름을 안띄우고
        //상대방이 정해져 있으면 프로필사진과 이름을 띄운다
        if(!item.rivaluid.equals("")){
            setMatchingRival(item.rivaluid);
        }

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //안드로이드 다이얼로그에 아이템은 배열을 문자열로 넣어줘야해서
                //어레이 리스트를 문자열로 변경
                String[] livalNameArr = new String[livalNameLists.size()];
                for(int i = 0; i < livalNameLists.size(); i++){
                    livalNameArr[i] = livalNameLists.get(i);
                }
                //다이얼로그에 context와 상대사람 문자열 배열을 넣음
                showDialog(MyBoardDescActivity.this, livalNameArr);
            }
        });
    }

    public void showDialog(Activity activity, String[] arr){//커스텀 다이얼로그
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.my_board_dialog);

        Button btndialog = (Button) dialog.findViewById(R.id.btndialog);
        btndialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);//다이얼로그 안에 리스트뷰를 넣는다
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.my_board_dialog_items, R.id.tv_name, arr);
        listView.setAdapter(arrayAdapter);//어뎁터 연결

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String rivalUid = livalUidLists.get(position);
                Map<String,Object> map = new HashMap<>();//Firebase에 저장이된 경기 객체에
                map.put("rivaluid",rivalUid);//rivaluid에 상대편 식별자를 넣는다 그럼 매칭 성공
                FirebaseDatabase.getInstance().getReference().child("BoardItem").child(item.primarykey).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "매칭이 성사 되었습니다.", Toast.LENGTH_SHORT).show();
                        setMatchingRival(rivalUid);
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    public void setMatchingRival(String rivaluid){//DB에서 상대편의 정보를 가져온다
        FirebaseDatabase.getInstance().getReference().child("users").child(rivaluid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                Glide
                        .with(getApplicationContext())
                        .load(Uri.parse(userModel.profileImageUrl))
                        .circleCrop()
                        .placeholder(R.drawable.logo)
                        .into(rival_avatar);
                rival_name.setText(userModel.userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
