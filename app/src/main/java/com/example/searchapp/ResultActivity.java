package com.example.searchapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    private String search, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();

        search = intent.getStringExtra("search");
        email = intent.getStringExtra("email");
        Log.e("email", String.valueOf(email));
        Log.e("result", String.valueOf(search));
        TextView tv = findViewById(R.id.restv);
        tv.setText(search);

        //private WebView webView;
    }
}