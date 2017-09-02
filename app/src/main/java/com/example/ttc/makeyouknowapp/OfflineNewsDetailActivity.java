package com.example.ttc.makeyouknowapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

/**
 * Created by ttc on 2017/3/16.
 */

public class OfflineNewsDetailActivity extends AppCompatActivity {
   // private WebView mWebView;
    private final String TAG = "OfflineDetail";
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offline_news_detail);
        Intent intent = getIntent();
        String detailtitle = intent.getStringExtra(".title");
        String detailcontents = intent.getStringExtra(".body");
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_offline);
        mToolbar.setTitle(detailtitle);
        WebView detailbody = (WebView) findViewById(R.id.offline_detail_body);
        detailbody.getSettings().setDefaultTextEncodingName("UTF-8") ;
        detailbody.loadData(detailcontents, "text/html; charset=UTF-8", null);
    }
}
