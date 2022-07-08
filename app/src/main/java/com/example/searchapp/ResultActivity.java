package com.example.searchapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener{
    private String search, email, nick, profimg;
    private WebView webView;
    private ArrayList<String> urls;
    Button home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        urls = new ArrayList<String>();
        urls.add("https://www.google.com/search?q="); // google urls
        urls.add("https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query="); // naver url
        urls.add("https://www.youtube.com/results?search_query="); // youtube url
        urls.add("https://ko.m.wikipedia.org/w/index.php?search="); // wiki url

        search = intent.getStringExtra("search");
        nick = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        profimg = intent.getStringExtra("profileImg");
        Log.e("email", String.valueOf(email));
        Log.e("result", search);

        EditText tv = findViewById(R.id.restv);
        Button google = findViewById(R.id.google);
        Button naver = findViewById(R.id.naver);
        Button youtube = findViewById(R.id.youtube);
        Button wiki = findViewById(R.id.wiki);
        ListView lv = findViewById(R.id.listview);
        lv.bringToFront();
        home = findViewById(R.id.home);
        home.setOnClickListener(this::onClick);
        tv.setText(search);
        tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        tv.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) { // 엔터 치면 실행
                if (i==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==KeyEvent.ACTION_DOWN){
                    Log.e("enter", "enter");
                    search = tv.getText().toString();
                    Intent intent = new Intent(ResultActivity.this, ResultActivity.class);
                    intent.putExtra("search", search);
                    intent.putExtra("name",nick);
                    intent.putExtra("email",email);
                    intent.putExtra("profileImg",profimg);
                    startActivity(intent);
                }
                return true;
            }
        });
        webView = findViewById(R.id.webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        search = search.replace(" ","+");
        webView.loadUrl(urls.get(0) +search);

        google.setOnClickListener(this);
        naver.setOnClickListener(this);
        youtube.setOnClickListener(this);
        wiki.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        search = search.replace(" ","+");
        switch (view.getId()) {
            case R.id.google:
                webView.loadUrl(urls.get(0) +search);
                break;
            case R.id.naver:
                webView.loadUrl(urls.get(1)+search);
                break;
            case R.id.youtube:
                webView.loadUrl(urls.get(2)+search);
                break;
            case R.id.wiki:
                webView.loadUrl(urls.get(3)+search);
                break;
            case R.id.home:
//                home.setVisibility(View.VISIBLE);
                Intent intent = new Intent(ResultActivity.this, SubActivity.class);
                intent.putExtra("search", search);
                intent.putExtra("name",nick);
                intent.putExtra("email",email);
                intent.putExtra("profileImg",profimg);
                startActivity(intent);
        }
    }
}