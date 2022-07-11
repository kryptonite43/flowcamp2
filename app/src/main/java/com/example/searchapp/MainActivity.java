package com.example.searchapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import android.widget.ImageButton;
import android.widget.Toast;


import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;

import java.security.MessageDigest;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(AuthApiClient.getInstance().hasToken()) {
            UserApiClient.getInstance().accessTokenInfo((accessTokenInfo, throwable) -> {
                if (throwable != null) {
                    Toast.makeText(this, "토큰 가져오기 실패", Toast.LENGTH_SHORT).show();
                }
                else if (accessTokenInfo != null) {
                    // 이미 로그인 된 상태
                    getUserInfo();
                }
                return null;
            } );
        }
        ImageButton kakao_login_button = findViewById(R.id.kakao_login_button);
        kakao_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)){
                    Log.e("onclick", "onclick");
                    login();
                }
                else {
                    Log.e("로그인",".........");
                    accountLogin();
                }
            }
        });
       //getAppKeyHash();
    }

    public void login() {
        String TAG = "login()";
        UserApiClient.getInstance().loginWithKakaoTalk(MainActivity.this, (oAuthToken,error) -> {
            Log.e("loginerror?","login");

                Log.e(TAG, "로그인 성공(토큰): "+oAuthToken.getAccessToken());
                getUserInfo();

            return null;
        });
    }

    public void accountLogin(){
        String TAG = "accountLogin()";
        Log.e("accountlogin"," ");
        UserApiClient.getInstance().loginWithKakaoAccount(MainActivity.this,(oAuthToken, error) -> {
                if (error != null) {
                    Log.e("loginkakao","s");
                }
                Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
                getUserInfo();

            return null;
        });
    }

    public void getUserInfo(){
        String TAG = "getUserInfo()";
        UserApiClient.getInstance().me((user, meError) -> {
            if (meError != null) {
                Log.e(TAG, "사용자 정보 요청 실패", meError);
            } else {
                System.out.println("로그인 완료");
                Log.i(TAG, user.toString());

                Log.i(TAG, "사용자 정보 요청 성공" +
                        "\n회원번호: "+user.getId() +
                        "\n이메일: "+ Objects.requireNonNull(user.getKakaoAccount()).getEmail());

                Account user1 = user.getKakaoAccount();
                System.out.println("사용자 계정" + user1);

                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                intent.putExtra("name", Objects.requireNonNull(user1.getProfile()).getNickname());
                intent.putExtra("profileImg", user1.getProfile().getProfileImageUrl());
                intent.putExtra("email", user1.getEmail());
                startActivity(intent);
                finish();
                Toast.makeText(MainActivity.this, "환영합니다 !!", Toast.LENGTH_SHORT).show();
            }
            return null;
        });
    }

    // 카카오 로그인 시 필요한 해시키 얻기
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }
    }

    //@Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
////        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) // main으로 돌아올때 값 전달
////            super.onActivityResult(requestCode, resultCode, data);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Session.getCurrentSession().removeCallback(mSessionCallback); // 액티비티 죽었을 때 객체 해제
    }
}