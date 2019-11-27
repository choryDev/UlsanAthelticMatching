package com.example.ulsanathelticmatching.board;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ulsanathelticmatching.R;

public class WriteActivity extends AppCompatActivity {
    Button btn_date,btn_save, btn_cancel;
    TextView tv_date;
    EditText edt_title, edt_content;
    DatePickerDialog.OnDateSetListener callbackMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        btn_date = (Button)findViewById(R.id.select_date);
        btn_save = (Button)findViewById(R.id.btn_save);
        btn_save = (Button)findViewById(R.id.btn_cancel);
        edt_title = (EditText)findViewById(R.id.edt_title);
        edt_content = (EditText)findViewById(R.id.edt_content);
        tv_date = (TextView)findViewById(R.id.tv_date);

        this.InitializeListener();

        //다이얼로그
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(getApplicationContext(), callbackMethod, 2019, 5, 24);
                dialog.show();
            }
        });

    }

    //날짜 클릭시
    public void InitializeListener(){
        callbackMethod = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                tv_date.setText(year+"년"+monthOfYear+"월"+dayOfMonth+"일");
            }
        };
    }
}
