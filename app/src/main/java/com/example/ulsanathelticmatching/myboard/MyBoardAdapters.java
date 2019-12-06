package com.example.ulsanathelticmatching.myboard;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.model.BoardItem;
import com.example.ulsanathelticmatching.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyBoardAdapters extends BaseAdapter {
    private Context mContext = null;
    private int layout = 0;
    private List<BoardItem> boardItemslist = null;
    private LayoutInflater inflater = null;
    private FirebaseAuth mAuth;

    ImageView image, rival_avatar;
    TextView sport, area, title, date, name, rival_name ;

    public MyBoardAdapters(Context c, int l) {
        mAuth = FirebaseAuth.getInstance();
        boardItemslist = new ArrayList<>();
        //Firebase Db에서 BoardItemr객체의 목록을 가져온다.
        FirebaseDatabase.getInstance().getReference().child("BoardItem").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boardItemslist.clear();//쌓이는 걸 막기 위해 어레이리스트 청소
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    BoardItem boardItem = item.getValue(BoardItem.class);
                    //자신의 uid와 작성자 uid, 상대편 uid가 같으면 자신이 관련된 글이므로 조회
                    if(mAuth.getUid().equals(boardItem.uid)||mAuth.getUid().equals(boardItem.rivaluid))
                        boardItemslist.add(boardItem);
                }
                notifyDataSetChanged();//데이터 갱신
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        this.mContext = c;
        this.layout = l;
        this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return boardItemslist.size();
    }

    @Override
    public BoardItem getItem(int position) {
        // TODO Auto-generated method stub
        return boardItemslist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override//리스트뷰의 자식객체에 정보들 세팅
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if(convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
        }
        image = (ImageView) convertView.findViewById(R.id.iv_avatar);
        sport = (TextView) convertView.findViewById(R.id.tv_sport);
        area = (TextView) convertView.findViewById(R.id.tv_area);
        title = (TextView) convertView.findViewById(R.id.tv_title);
        date = (TextView) convertView.findViewById(R.id.tv_date);
        name = (TextView) convertView.findViewById(R.id.tv_name);

        rival_avatar = (ImageView) convertView.findViewById(R.id.iv_rv_avatar);
        rival_name = (TextView) convertView.findViewById(R.id.tv_rv_name);

        Glide
                .with(convertView)
                .load(Uri.parse(boardItemslist.get(position).img))
                .circleCrop()
                .placeholder(R.drawable.logo)
                .into(image);
        sport.setText(boardItemslist.get(position).sports);
        area.setText(boardItemslist.get(position).area);
        title.setText(boardItemslist.get(position).title);
        date.setText(boardItemslist.get(position).date);
        name.setText(boardItemslist.get(position).name);

        //경기의 상대방이 정해진 경우 상대편에 누구인지 띄운다
        //만약 상대방이 ""이면 프로필사진와 이름을 안띄우고
        //상대방이 정해져 있으면 프로필사진과 이름을 띄운다
        if(!boardItemslist.get(position).rivaluid.equals(""))
        setMatchingRival((boardItemslist.get(position).rivaluid));

        return convertView;
    }
    public void setMatchingRival(String rivaluid){//DB에서 상대편의 정보를 가져와 반대편에 누구와 경기가 잡힌지 보여 줌
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