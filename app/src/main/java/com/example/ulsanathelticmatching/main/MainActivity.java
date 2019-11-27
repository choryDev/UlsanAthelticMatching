package com.example.ulsanathelticmatching.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.sign.SignInActivity;
import com.example.ulsanathelticmatching.sign.SplashActivity;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.View;
import androidx.core.view.GravityCompat;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final static String menu_name_array[] = {"축구","축구","축구","축구","축구","축구","축구","축구","축구"};
    final static int menu_img_array[] = {R.drawable.loading_test,R.drawable.loading_test,R.drawable.loading_test,
            R.drawable.loading_test,R.drawable.loading_test,R.drawable.loading_test,
            R.drawable.loading_test,R.drawable.loading_test,R.drawable.loading_test,};
    private ListView listView;

    private FirebaseAuth mAuth;

    private TextView authName;
    private TextView authEmail;
    private ImageView authAvatar;

    ArrayList<MainMenuItem> menu_obj_list;
    MainMenuItemAdapters myadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

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
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class); // 다음넘어갈 화면
                Bitmap sendBitmap = menu_obj_list.get(position).image;

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                intent.putExtra("image",byteArray);
                startActivity(intent);
            }
        });

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        View view = navigationView.getHeaderView(0);

        authName = (TextView)view.findViewById(R.id.auth_name);
        authEmail = (TextView)view.findViewById(R.id.auth_email);
        authAvatar = (ImageView)view.findViewById(R.id.auth_avatar);
        authName.setText(mAuth.getCurrentUser().getDisplayName());
        authEmail.setText(mAuth.getCurrentUser().getEmail());

        Glide
                .with(this)
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .circleCrop()
                .placeholder(R.drawable.logo)
                .into(authAvatar);

       Log.d("애러 내용", String.valueOf(mAuth.getCurrentUser().getPhotoUrl()));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent i = new Intent(getApplicationContext(), SplashActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        }else if (id == R.id.nav_send) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Intent i = new Intent(this, SignInActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}