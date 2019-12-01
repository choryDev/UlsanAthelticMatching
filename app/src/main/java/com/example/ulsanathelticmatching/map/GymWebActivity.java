package com.example.ulsanathelticmatching.map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.ulsanathelticmatching.R;

public class GymWebActivity extends AppCompatActivity {

    WebView webView;
    String name, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_web);

        webView = (WebView)findViewById(R.id.webView);

        //웹뷰 객체 속성
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        Intent intent = getIntent(); /*데이터 수신*/
        name = intent.getExtras().getString("name"); /*String형*/

        url = "http://www.google.com/search?q="+name;
        webView.loadUrl(url);
    }
}
