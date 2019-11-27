package com.example.ulsanathelticmatching.board;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ulsanathelticmatching.R;

import java.util.ArrayList;

public class BoardListActivity extends AppCompatActivity {
    ArrayList<BoardItem> boardItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);
    }
}
