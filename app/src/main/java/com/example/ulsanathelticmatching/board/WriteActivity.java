package com.example.ulsanathelticmatching.board;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ulsanathelticmatching.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WriteActivity extends AppCompatActivity {
    Button btn_date, btn_save, btn_cancel;
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
//
//        btn_date = (Button)findViewById(R.id.select_date);
//        btn_save = (Button)findViewById(R.id.btn_save);
//        btn_cancel = (Button)findViewById(R.id.btn_cancel);
//        edt_title = (EditText)findViewById(R.id.edt_title);
//        edt_content = (EditText)findViewById(R.id.edt_content);
//        tv_date = (TextView)findViewById(R.id.tv_date);
//        sp_sports = (Spinner)findViewById(R.id.sp_sports);
//        sp_area = (Spinner)findViewById(R.id.sp_area);
//
//
//        mAuth = FirebaseAuth.getInstance();
//
//        //데이트 피커 객체를 불러오는 함수
//        btn_date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                calendar = Calendar.getInstance();
//                year = calendar.get(Calendar.YEAR);
//                month = calendar.get(Calendar.MONTH);
//                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//                datePickerDialog = new DatePickerDialog(WriteActivity.this,
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                                tv_date.setText(year+"/"+ (month + 1) + "/" + day);
//                            }
//                        }, year, month, dayOfMonth);
//                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
//                datePickerDialog.show();
//            }
//        });
//        //저장 버튼을 눌러 저장하는 함수
//        btn_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String title = edt_title.getText().toString();
//                String date = tv_date.getText().toString();
//                String content = edt_content.getText().toString();
//
//                //만약 값이 없을 경우 리턴시킨다
//                if(title == "" ||date == "" || content == ""  ){
//                    Toast.makeText(getApplicationContext(), "제목 날짜 내용 기입해주세요", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                //pk로 저장된 시간을 이용
//                SimpleDateFormat pkformat = new SimpleDateFormat ( "yyyyMMddHHmmss");
//                Date time = new Date();
//
//                BoardItem item = new BoardItem();
//                item.primarykey = pkformat.format(time);
//                item.uid = mAuth.getCurrentUser().getUid();
//                item.name  = mAuth.getCurrentUser().getDisplayName();
//                item.img  = String.valueOf(mAuth.getCurrentUser().getPhotoUrl());
//                item.title  = title;
//                item.date = date;
//                item.content = content;
//                item.sports = sp_sports.getSelectedItem().toString();
//                item.area = sp_area.getSelectedItem().toString();
//
//                //firebaseDatabase객체에 BoardItem에 객체를 저장
//                FirebaseDatabase.getInstance().getReference().child("BoardItem").child(pkformat.format(time)).setValue(item);
//                Toast.makeText(getApplicationContext(), "경기가 등록 되었습니다", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        });
    }
}