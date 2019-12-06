package com.example.ulsanathelticmatching.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.model.ChatModel;
import com.example.ulsanathelticmatching.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.usermgmt.response.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class ChatActivity extends AppCompatActivity {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.chatactivity_recyclerview);   //리싸이클러뷰 만들기
        recyclerView.setAdapter(new ChatRecyclerViewAdapter()); //어뎁터 붙이기
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //레이아웃을 리니어레이아웃으로 하기

    }

    //뷰홀더를 관리하기 위한 어탭터 클래스
    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<ChatModel> chatModels = new ArrayList<>(); //채팅에 대한 정보를 담을 chatModels 배열 생성
        private String uid;
        private ArrayList<String> destinationUsers = new ArrayList<>();

        public ChatRecyclerViewAdapter() {
            //채팅목록 가져옴
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();  //

            //내가 소속된 방 들어감
            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chatModels.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        //데이터 쌓아놓기
                        chatModels.add(item.getValue(ChatModel.class));
                    }
                    notifyDataSetChanged(); //데이터 갱신
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //뷰홀더 생성
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
            return new CustomViewHolder(view);
        }


        //뷰홀더를 합침
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

            final CustomViewHolder customViewHolder = (CustomViewHolder) holder; //커스텀 뷰 홀더 만들기
            String destinationUid = null; //상대방uid

            //챗방에 있는 유저를 모두 체크
            for (String user : chatModels.get(position).users.keySet()) {
                if (!user.equals(uid)) {   ///만약 내가 아니면 상대방 uid 담기
                    destinationUid = user; //destionationUID(상대방 uid) uid 담음
                    destinationUsers.add(destinationUid); //destinationUsers 배열에 uid 담음
                }
            }

            // 사용자의 프로필을 로드
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //한번 user의 destinationUid 경로에 있는 내용의 정적 스냅샷을 읽어와서 전체 내용에 대한 변경을 읽고 수신 대기

                    //상대방 정보를 userModel 당음
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    Glide.with(customViewHolder.itemView.getContext())    //이미지 로딩 라이브러리
                            .load(userModel.profileImageUrl)              //userModel의 profileImageUrl을 가져옴
                            .apply(new RequestOptions().circleCrop())     //사진을 원형으로 자름
                            .into(customViewHolder.imageView);            // 뷰홀더의 이미지뷰에 이미지를 남음
                    customViewHolder.textView_title.setText(userModel.userName);  //뷰홀더의 textView_title의 userModel객체의 userName 값으로 담기

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //메세지를 내림차순으로 정렬 후 마지막 메세지 띄우기
            //string값(채팅메세지키값)에 대한 코멘트내용을 트리맵으로 만들어서 역순으로 정렬
            Map<String, ChatModel.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
            commentMap.putAll(chatModels.get(position).comments); //chatModels에 (채팅내용)comments에 대한 값을 모두 가져옴
            String lastMessageKey = (String) commentMap.keySet().toArray()[0]; //채팅내용 중 가장 1번쨰 값의 키를 가져옴
            customViewHolder.textView_last_message.setText(chatModels.get(position).comments.get(lastMessageKey).message);
            //뷰홀더의 textView_last_message에 가져온 키값을 넣어 해당 키값의 채팅 내용을 가져옴

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //클릭시 대화방으로 이동
                    Intent intent = new Intent(view.getContext(), Message2Activity.class);
                    intent.putExtra("destinationUid", destinationUsers.get(position));         //상대방 UID를 인텐트에 담아서 보냄

                    //이동 효과주기 (오른쪽에서 왼쪽으로 밀기)
                    ActivityOptions activityOptions = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright, R.anim.toleft);
                        startActivity(intent, activityOptions.toBundle());
                    }
                }
            });

            //Timestamp 시간 포맷 변경
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); //서울시간에 맞게 데이터포맷을 바꿈
            long unixTime = (long)chatModels.get(position).comments.get(lastMessageKey).timestamp;
            Date date = new Date(unixTime);     //comments에서 가져온 시간을 담음
            customViewHolder.textView_timestamp.setText(simpleDateFormat.format(date)); //바꾼 시간포맷을 뷰홀더의 textView_timestamp에 담음
        }

        @Override
        public int getItemCount() {
            return chatModels.size();
        } //chatModels의 크기 리턴

        //리스트에 들어갈 각 뷰를 관리할 뷰홀더 클래스
        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;   //프로필사진 담을 이미지뷰
            public TextView textView_title;  //상대방 이름 담을 텍스트뷰
            public TextView textView_last_message;  //마지막 메세지 내용을 담을 텍스트뷰
            public TextView textView_timestamp;  //시간을 담을 텍스트뷰

            public CustomViewHolder(View view) {
                super(view);

                imageView = (ImageView) view.findViewById(R.id.chatitem_imageview);
                textView_title = (TextView) view.findViewById(R.id.chatitem_textview_title);
                textView_last_message = (TextView) view.findViewById(R.id.chatitem_textview_lastMessage);
                textView_timestamp = (TextView) view.findViewById(R.id.chatitem_textview_timestamp);
            }
        }
    }
}
