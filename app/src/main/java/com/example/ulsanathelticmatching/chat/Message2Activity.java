package com.example.ulsanathelticmatching.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.model.ChatModel;
import com.example.ulsanathelticmatching.model.NotificationModel;
import com.example.ulsanathelticmatching.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Message2Activity extends AppCompatActivity {
    private String destinationUid;
    private Button button;
    private EditText editText;

    private String uid;
    private String chatRoomUid;

    private RecyclerView recyclerView;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    private UserModel destinationuserModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message2);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();  //채팅을 요구 하는 아아디 즉 단말기에 로그인된 UID
        destinationUid = getIntent().getStringExtra("destinationUid");
        button = (Button) findViewById(R.id.messageAcitvity_Button);
        editText = (EditText) findViewById(R.id.messageAcitvity_editText);

        recyclerView = (RecyclerView) findViewById(R.id.messageActivity_recyclerview);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(uid, true);
                chatModel.users.put(destinationUid, true);


                if (chatRoomUid == null) {
                    button.setEnabled(false); //메시지 전송이 완료되기전엔 버튼 사용x
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom(); //데이터베이스가 완료됐을때 실행
                        }
                    });
                } else {
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = editText.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP; //1970/1/1 시간을 뺀 밀리세컨즈값
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sendGcm();
                            editText.setText("");  //에디트 텍스트 초기화

                        }
                    });
                }
            }
        });
        checkChatRoom();
    }
    //푸시
    void sendGcm(){
        Gson gson = new Gson();

        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = destinationuserModel.pushToken;
        notificationModel.notification.title = userName;
        notificationModel.notification.text = editText.getText().toString();
        notificationModel.data.title = userName;  //푸시받을때 데이터 파싱
        notificationModel.data.text = editText.getText().toString();

        RequestBody requestBody = RequestBody.create(gson.toJson(notificationModel), MediaType.parse("application/json; charset=utf8"));
        Request request = new Request.Builder()
                .header("Content-Type","application/json")
                .addHeader("Authorization","key=AAAAI_qLyCQ:APA91bFa8Ilp_4-nW7Bcr4cJqUfxUTrakNR5UpfNzm_QFXPJYpIiVqnzIx035ss4cy2HFtZ_3OpkWxkRXy-UJ-VBdgUvYwOzWD3woIWMWTEOxU2oL1lGPaQNvMKWkquxn931DU94zvHl")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
 //실패시
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//성공시
            }
        });

    }

    void checkChatRoom() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    if (chatModel.users.containsKey(destinationUid)) {
                        chatRoomUid = item.getKey();
                        button.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(Message2Activity.this));
                        recyclerView.setAdapter(new RecycleViewAdapter());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    class RecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        //코멘트를 담음
        List<ChatModel.Comment> comments;

        UserModel userModel;

        public RecycleViewAdapter() {
            comments = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userModel = dataSnapshot.getValue(UserModel.class); //유저모델 정보가 담김
                    getMessageList();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        //message리스트 받아옴
        void getMessageList() {
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    comments.clear(); //데이터가 쌓이는 것을 방지

                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        comments.add(item.getValue(ChatModel.Comment.class));
                    }
                    notifyDataSetChanged(); //메세지 갱신

                    recyclerView.scrollToPosition(comments.size() - 1); //맨 마지막 포지션으로 이동
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);

            return new MessageViewHolder(view); //뷰 재사용 클래스
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);

            if (comments.get(position).uid.equals(uid)) {
                //내가 말하는 부분
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE); //내 프로필 감춤
                messageViewHolder.textView_message.setTextSize(20);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
            } else {
                //상대방 메세지
                Glide.with(holder.itemView.getContext())
                        .load(userModel.profileImageUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile);
                messageViewHolder.textView_name.setText(userModel.userName);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(20);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
            }

            //시간정제화
            long unixTime = (long)comments.get(position).timestamp;
            Date date = new Date(unixTime); //comments에서 가져온 시간을 담음
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); //서울시간에 맞게 데이터포맷을 바꿈
            String time = simpleDateFormat.format(date); //바꾼 시간포맷을 스트림에 담음
            messageViewHolder.textView_timestamp.setText(time); //타임스템프에 담음

        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;


            public MessageViewHolder(View view) {
                super(view);
                textView_message = (TextView) view.findViewById(R.id.messageItem_textView_message);
                textView_name = (TextView) view.findViewById(R.id.messageItem_textview_name);
                imageView_profile = (ImageView) view.findViewById(R.id.messageItem_imageview_profile);
                linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_destination);
                linearLayout_main = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_main);
                textView_timestamp = (TextView) view.findViewById(R.id.messageItem_textView_timestamp);


            }
        }
    }
}