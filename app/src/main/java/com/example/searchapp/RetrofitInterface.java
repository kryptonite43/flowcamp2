package com.example.searchapp;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @POST("/")
    Call<Void> executeSearch(@Body HashMap<String, String> map);

    @GET("/")
    Call<List<String>> executeMyRecord(@Query("email") String email);

    @PUT("/")
    Call<Void> executeDeleteMyRecord(@Body HashMap<String, String> map);
}
