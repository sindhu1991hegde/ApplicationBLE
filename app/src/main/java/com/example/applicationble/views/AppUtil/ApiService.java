package com.example.applicationble.views.AppUtil;


import retrofit2.Call;
import retrofit2.http.GET;


@SuppressWarnings("ALL")
public interface ApiService {


    @GET("auth/getMqttCertificates")
    Call<Status> getMqttCertificates();



}

