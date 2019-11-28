package com.example.ulsanathelticmatching.main;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.board.BoardAdapters;
import com.example.ulsanathelticmatching.board.BoardDescActivity;
import com.example.ulsanathelticmatching.board.WriteActivity;
import com.example.ulsanathelticmatching.sign.SignInActivity;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listView;

    private FirebaseAuth mAuth;

    private TextView authName;
    private TextView authEmail;
    private ImageView authAvatar;

    BoardAdapters myadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        listView = (ListView)findViewById(R.id.MainActivity_listview);

        myadapter = new BoardAdapters(getApplicationContext(),R.layout.activity_board_items);
        listView.setAdapter(myadapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BoardDescActivity.class); // 다음넘어갈 화면
                intent.putExtra("index", position);
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
            Intent i = new Intent(this, WriteActivity.class);
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