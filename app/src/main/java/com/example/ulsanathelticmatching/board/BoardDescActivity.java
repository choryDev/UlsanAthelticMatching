package com.example.ulsanathelticmatching.board;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ulsanathelticmatching.R;

import com.example.ulsanathelticmatching.model.BoardItem;

import com.example.ulsanathelticmatching.chat.Message2Activity;

import java.util.List;

public class BoardDescActivity extends AppCompatActivity {

    private BoardItem item;
    private List<BoardItem> boardItemslist = null;
    private TextView sport, area, title, date, name, content;
    private ImageView image;

    private Button chatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_desc);

        Intent intent = getIntent();
        item  = (BoardItem)intent.getSerializableExtra("OBJECT");

         image = (ImageView)findViewById(R.id.avatar);
         sport = (TextView)findViewById(R.id.sports);
         area = (TextView)findViewById(R.id.area);
         title = (TextView)findViewById(R.id.title);
         date = (TextView)findViewById(R.id.date);
         name = (TextView)findViewById(R.id.name);
         content = (TextView)findViewById(R.id.content);

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

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Message2Activity.class);
                intent.putExtra("destinationUid",item.uid);  //글 작성자 uid 받아오기
                startActivity(intent);
            }
        });
    }
}
