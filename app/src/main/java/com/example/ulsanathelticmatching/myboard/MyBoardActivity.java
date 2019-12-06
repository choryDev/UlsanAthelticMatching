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
    //내가 쓴 게시글 또는 메칭이 성사된 나와 관련된 게시글만 조회할 수 있는 액티비티
    private MyBoardAdapters myadapter;//내게시글 객체를 담아옴
    private ListView myListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_board);

        myListView = (ListView)findViewById(R.id.MyBoardActivity_listview);
        //내게시글 어뎁터를 받아옴
        myadapter = new MyBoardAdapters(getApplicationContext(), R.layout.activity_myboard_items); //내게시글 어뎁터
        myListView.setAdapter(myadapter);//내게시글 어뎁터를 리스트뷰와 연결

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override//내게시글 아이템을 선택시 일어나는 이벤트
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 다음넘어갈 화면
                Intent intent = new Intent(getApplicationContext(), MyBoardDescActivity.class);
                //선택 된 게시판 객체를 다음 화면으로 전달
                intent.putExtra("OBJECT", (Serializable) myadapter.getItem(position));
                startActivity(intent);
            }
        });
    }
}
