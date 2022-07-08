package com.example.searchapp;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/")
    Call<SearchResult> executeSearch(@Body HashMap<String, String> map);
}
