package com.example.ulsanathelticmatching.board;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ulsanathelticmatching.R;

public class BoardDescActivity extends AppCompatActivity {

    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_desc);

        Intent intent = getIntent();
        index  = intent.getIntExtra("index", 0);

        TextView tv = (TextView)findViewById(R.id.activity_board_desc_test);
        tv.setText(Integer.toString(index));
    }
}
