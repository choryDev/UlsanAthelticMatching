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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.chatactivity_recyclerview); //리사이클 뷰 객체
        recyclerView.setAdapter(new ChatRecyclerViewAdapter()); //어뎁터로 연결
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //어뎁터를 해당 액티비티에 연결
    }

    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {//채팅방 연결할 어뎁터
        private List<ChatModel> chatModels = new ArrayList<>(); //chatmodel을 담음
        private String uid;
        private ArrayList<String> destinationUsers = new ArrayList<>();

        public ChatRecyclerViewAdapter() {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();//내 식별자를 가지고옴

            //Firebase Db에 chatrooms에서 사용자가 내이름이 있을 경우 가져옴
            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chatModels.clear(); //채팅방 어레이리스트 청소
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

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
            return new CustomViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

            final CustomViewHolder customViewHolder = (CustomViewHolder) holder;
            String destinationUid = null; //상대방uid

            //챗방에 있는 유저를 모두 체크
            for (String user : chatModels.get(position).users.keySet()) {
                if (!user.equals(uid)) {   //만약 내가 아니면 상대방 uid 담기
                    destinationUid = user;
                    destinationUsers.add(destinationUid);
                }
            }//채팅방의 밖에 화면을 보여줘야 하기 때문에 상대방 uid를 가지고 상대방 정보를 가져온다
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class); //가져온 uid를 UserModel에 담아서 세팅
                    Glide.with(customViewHolder.itemView.getContext())
                            .load(userModel.profileImageUrl)
                            .apply(new RequestOptions().circleCrop())
                            .into(customViewHolder.imageView);
                    customViewHolder.textView_title.setText(userModel.userName);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //메세지를 내림차순으로 정렬 후 마지막 메세지 띄우기
            Map<String, ChatModel.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
            commentMap.putAll(chatModels.get(position).comments);
            String lastMessageKey = (String) commentMap.keySet().toArray()[0]; //마지막 메세지를 보여주게 함
            customViewHolder.textView_last_message.setText(chatModels.get(position).comments.get(lastMessageKey).message);

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //클릭시 대화방으로 이동
                    Intent intent = new Intent(view.getContext(), Message2Activity.class);
                    intent.putExtra("destinationUid", destinationUsers.get(position));//상대방의 정보를

                    //이동 효과주기 (오른쪽에서 왼쪽으로 밀기)
                    ActivityOptions activityOptions = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright, R.anim.toleft);
                        startActivity(intent, activityOptions.toBundle());
                    }
                }
            });
            //Timestamp 시간 포맷 변경
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            long unixTime = (long)chatModels.get(position).comments.get(lastMessageKey).timestamp;
            Date date = new Date(unixTime);
            customViewHolder.textView_timestamp.setText(simpleDateFormat.format(date));
        }

        @Override
        public int getItemCount() {
            return chatModels.size();
        }
        //어뎁터에 자식 플레그먼트에 내용을 단다.
        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView_title;
            public TextView textView_last_message;
            public TextView textView_timestamp;

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
