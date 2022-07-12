package com.example.searchapp;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;


public class CustomApplication extends Application
{
    private static volatile CustomApplication instance = null;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        KakaoSdk.init(this, "a7b2cee074a1effe4d3f1ff0d936acc2");
    }
}