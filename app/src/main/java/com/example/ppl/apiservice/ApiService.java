package com.example.ppl.apiservice;

import com.example.ppl.data.LoginRequest;
import com.example.ppl.data.LoginResponse;
import com.example.ppl.data.RequestBody;
import com.example.ppl.data.SerapanResponse;
import com.example.ppl.data.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {
    @GET("list_user")
    Call<List<User>> getlistuser();

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("serapan_universitas")
    Call<List<SerapanResponse>> getSerapanUniversitasByQuery(
            @Query("tahun") String tahun
    );

    @GET("serapan_universitas")
    Call<List<SerapanResponse>> getSerapanUniversitasByBody(
            @Body RequestBody requestBody
            );

    @GET("serapan_universitas")
    Call<List<SerapanResponse>> getSerapanUniversitas();

    @GET
    Call<List<SerapanResponse>> getSerapanUniversitasByUrl(@Url String url);
}
