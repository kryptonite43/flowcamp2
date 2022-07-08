package com.example.searchapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import javax.xml.transform.Result;

public class SubActivity extends AppCompatActivity { // 검색창 뜨는 액티비티
    private String strNick, strProfileImg, strEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        Intent intent = getIntent();
        strNick = intent.getStringExtra("name");
        strProfileImg = intent.getStringExtra("profileImg");
        strEmail = intent.getStringExtra("email");

        TextView tv_nick = findViewById(R.id.tv_nickname);
        TextView tv_email = findViewById(R.id.tv_email);
        ImageView iv_profile = findViewById(R.id.iv_profile);

        // 닉네임, 이메일, 프로필이미지
        tv_nick.setText(strNick);
        tv_email.setText(strEmail);
        Glide.with(this).load(strProfileImg).into(iv_profile);

        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        // 로그아웃 성공 시 수행하는 지점
                        finish(); // 현재 액티비티 종료
                    }
                });
            }
        });
        TextView tv = findViewById(R.id.text_hist1);
        EditText search = findViewById(R.id.what) ;
        Button searchb = findViewById(R.id.searchit);
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==KeyEvent.ACTION_DOWN){
                    Log.e("enter", "enter");
                    tv.setText(search.getText().toString());
                    Intent intent = new Intent(SubActivity.this, ResultActivity.class);
                    intent.putExtra("search", search.getText().toString());
                    intent.putExtra("email",strEmail);
                    startActivity(intent);
                }
                return true;
            }
        });
        searchb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.setText(search.getText().toString());
                Intent intent = new Intent(SubActivity.this, ResultActivity.class);
                intent.putExtra("search", search.getText().toString());
                intent.putExtra("email",strEmail);
                startActivity(intent);
            }
        });


    }
}