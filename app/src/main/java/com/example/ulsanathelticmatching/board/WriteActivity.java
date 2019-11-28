package com.example.ulsanathelticmatching.board;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WriteActivity extends AppCompatActivity {
    Button btn_date,btn_save, btn_cancel;
    TextView tv_date;
    Spinner sp_sports, sp_area;

    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;

    EditText edt_title, edt_content;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        btn_date = (Button)findViewById(R.id.select_date);
        btn_save = (Button)findViewById(R.id.btn_save);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        edt_title = (EditText)findViewById(R.id.edt_title);
        edt_content = (EditText)findViewById(R.id.edt_content);
        tv_date = (TextView)findViewById(R.id.tv_date);
        sp_sports = (Spinner)findViewById(R.id.sp_sports);
        sp_area = (Spinner)findViewById(R.id.sp_area);


        mAuth = FirebaseAuth.getInstance();

        //다이얼로그
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(WriteActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                tv_date.setText(year+"/"+ (month + 1) + "/" + day);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = edt_title.getText().toString();
                String date = tv_date.getText().toString();
                String content = edt_content.getText().toString();

                if(title == "" ||date == "" || content == ""  )
                    Toast.makeText(getApplicationContext(), "제목 날짜 내용 기입해주세요", Toast.LENGTH_SHORT).show();

                SimpleDateFormat pkformat = new SimpleDateFormat ( "yyyyMMddHHmmss");
                Date time = new Date();

                BoardItem item = new BoardItem();
                item.primarykey = pkformat.format(time);
                item.uid = mAuth.getCurrentUser().getUid();
                item.name  = mAuth.getCurrentUser().getDisplayName();
                item.img  = String.valueOf(mAuth.getCurrentUser().getPhotoUrl());
                item.title  = title;
                item.date = date;
                item.content = content;
                item.sports = sp_sports.getSelectedItem().toString();
                item.area = sp_area.getSelectedItem().toString();

                FirebaseDatabase.getInstance().getReference().child("BoardItem").child(pkformat.format(time)).setValue(item);

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
