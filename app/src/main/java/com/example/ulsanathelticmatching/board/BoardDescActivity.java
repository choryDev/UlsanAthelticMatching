package com.example.ulsanathelticmatching.board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ulsanathelticmatching.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class BoardDescActivity extends AppCompatActivity {

    private BoardItem item;
    private List<BoardItem> boardItemslist = null;
    private TextView sport, area, title, date, name, content;
    private ImageView image;
    private Button button;

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
         button = (Button) findViewById(R.id.sendMessageBtn);

        Glide
                .with(this)
                .load(Uri.parse(item.img))
                .circleCrop()
                .placeholder(R.drawable.logo)
                .into(image);
        sport.setText("종목 : " + item.sports);
        area.setText("지역 : " + item.area);
        title.setText(item.title);
        date.setText("날짜 : " + item.date);
        name.setText(item.name);
        content.setText("내용 : " + item.content);

    }
}
