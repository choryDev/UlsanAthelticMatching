package com.example.ulsanathelticmatching.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.ulsanathelticmatching.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SelectSportsActivity extends AppCompatActivity {

    final static String menu_name_array[] = {"축구","축구","축구","축구","축구","축구","축구","축구","축구"};
    final static int menu_img_array[] = {R.drawable.loading_test,R.drawable.loading_test,R.drawable.loading_test,
                                         R.drawable.loading_test,R.drawable.loading_test,R.drawable.loading_test,
                                         R.drawable.loading_test,R.drawable.loading_test,R.drawable.loading_test,};
    private ListView listView;
    ArrayList<MainMenuItem> menu_obj_list;
    MainMenuItemAdapters myadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sports);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        menu_obj_list = new ArrayList<MainMenuItem>();
        listView = (ListView)findViewById(R.id.MainActivity_listview);

        for(int i = 0;i<menu_name_array.length; i++){
            MainMenuItem menu_obj = new MainMenuItem(menu_name_array[i], BitmapFactory.decodeResource(getResources(),menu_img_array[i]));
            menu_obj_list.add(menu_obj);
        }

        myadapter = new MainMenuItemAdapters(getApplicationContext(),R.layout.main_sportsmenu_items, menu_obj_list);
        listView.setAdapter(myadapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class); // 다음넘어갈 화면
                Bitmap sendBitmap = menu_obj_list.get(position).image;

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                intent.putExtra("image",byteArray);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        ////////////////임시 로그아웃 ///////////////////////////////////////
        Button sign_out_btn = (Button)findViewById(R.id.sign_out_button);
        sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "임시 로그아웃", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
