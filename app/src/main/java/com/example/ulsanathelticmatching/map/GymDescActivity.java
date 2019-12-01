package com.example.ulsanathelticmatching.map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.board.BoardItem;
import com.example.ulsanathelticmatching.model.GymModel;

public class GymDescActivity extends AppCompatActivity {

    Button webSieteBtn, mapBtn;
    GymModel item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_desc);

        webSieteBtn = (Button)findViewById(R.id.webSieteBtn);
        mapBtn = (Button)findViewById(R.id.mapBtn);

        Intent intent = getIntent();
        item  = (GymModel)intent.getSerializableExtra("OBJECT");

        webSieteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GymWebActivity.class);
                intent.putExtra("name",item.locationName);
                startActivity(intent);
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
