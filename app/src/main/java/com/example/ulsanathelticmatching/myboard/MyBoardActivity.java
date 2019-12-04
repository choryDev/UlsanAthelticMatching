package com.example.ulsanathelticmatching.myboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.board.BoardDescActivity;

import java.io.Serializable;

public class MyBoardActivity extends AppCompatActivity {

    private MyBoardAdapters myadapter;
    private ListView myListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_board);

        myListView = (ListView)findViewById(R.id.MyBoardActivity_listview);

        myadapter = new MyBoardAdapters(getApplicationContext(), R.layout.activity_myboard_items);
        myListView.setAdapter(myadapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 다음넘어갈 화면
                Intent intent = new Intent(getApplicationContext(), MyBoardDescActivity.class);
                //게시판 객체를 다음 화면으로 전달
                intent.putExtra("OBJECT", (Serializable) myadapter.getItem(position));
                startActivity(intent);
            }
        });
    }
}
