package com.example.ulsanathelticmatching.chat;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.chatfragment_recyclerview);
        recyclerView.setAdapter(new ChatRecycleViewAdapter()); //리사이클러뷰에 바인딩
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }


    class ChatRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        //채팅목록 가져오기
        private List<ChatModel> chatModels = new ArrayList<>(); //채팅에 대한 모델을 가진 리스트
        private String uid; //
        private ArrayList<String> destinationUsers = new ArrayList<>();

        public ChatRecycleViewAdapter() {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //현재 uid

            //내가 소속된 방 들어가기
            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chatModels.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        chatModels.add(item.getValue(ChatModel.class));

                    }
                    notifyDataSetChanged(); //새로 데이터 갱신
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           //화면 보여줌
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            //데이터를 바인딩
            final CustomViewHolder customViewHolder = (CustomViewHolder) holder;
            String destinationUid = null;
            // 챗방에 있는 유저를 모두 체크
            for (String user : chatModels.get(position).users.keySet()) { //방의 유저 키
                if (!user.equals(uid)) { //내가 아닌 사람
                    destinationUid = user;
                    destinationUsers.add(destinationUid);
                }
            }
            //상대방
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    Glide.with(customViewHolder.itemView.getContext())
                            .load(userModel.profileImageUrl)  //상대방 이미지url 받아옴
                            .apply(new RequestOptions().circleCrop())  //이미지를 원형으로 자름
                            .into(customViewHolder.imageView);   //custonViewHolder의 이미지뷰에 넣음

                    customViewHolder.textView_title.setText(userModel.userName); //상대방이름으로 채팅방이름 정함

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //메시지를 내림 차순으로 정렬 후 마지막 메세지의 키값을 가져옴
            Map<String, ChatModel.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
            commentMap.putAll(chatModels.get(position).comments); //채팅에 대한 내용 가져옴
            String lastMessageKey = (String) commentMap.keySet().toArray()[0]; //첫번째값만 뽑아옴
            customViewHolder.textView_last_message.setText(chatModels.get(position).comments.get(lastMessageKey).message); //첫번째 값을 바인딩
            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid",destinationUsers.get(position));

                    ActivityOptions activityOptions = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                     //   activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright, R.anim.toleft);
                        startActivity(intent, activityOptions.toBundle());
                    }
                }
            });
           simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

                long unixTime = (long) chatModels.get(position).comments.get(lastMessageKey).timestamp;
                Date date = new Date(unixTime);
            customViewHolder.textView_timestamp.setText(simpleDateFormat.format(date));
        }

        @Override
        public int getItemCount() {
            return chatModels.size();
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
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

