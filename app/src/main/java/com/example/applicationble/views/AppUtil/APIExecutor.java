package com.example.applicationble.views.AppUtil;

import android.content.Context;

import com.example.common_lib.AppConstants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * It will return and ApiService instance that can call login method or any other that you will define in ApiService interface file
 */
@SuppressWarnings("ALL")
public class APIExecutor {

    //private static final String BASE_URL = "https://heka.rpsapi.in"; // New Servery
    // production  url
    private static final String BASE_URL = "https://heka.rpsapi.in"; // New Server
    // dev url
   // private static final String BASE_URL = "https://heka-dev.rpsapi.in"; // New Server

    private ApiService apiService;
    Context context;



    public static ApiService getApiServiceWithHeader(final Context context) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
      //  if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(interceptor);
         //   }
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", AppConstants.AUTH_KEY); // <-- this is the important line
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        
        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .client(client.newBuilder().connectTimeout(2 * 60 * 1000, TimeUnit.SECONDS).readTimeout(2 * 60 * 1000, TimeUnit.SECONDS).writeTimeout(2 * 60 * 1000, TimeUnit.SECONDS).build()).build();
        return retrofit.create(ApiService.class);

    }



}
