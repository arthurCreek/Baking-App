package com.example.android.projectbakingapp.Query;

import android.content.Context;
import android.net.ConnectivityManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitController {

    public static Retrofit retrofit = null;
    public static long CONNECTION_TIMEOUT = 10000;
    public static String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";

    public static Retrofit getRetrofit(Context context){

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() == null &&
                !cm.getActiveNetworkInfo().isConnectedOrConnecting()){
            return null;
        }

        if (retrofit == null){
            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

            okHttpBuilder.addInterceptor(loggingInterceptor);
            okHttpBuilder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpBuilder.build())
                    .build();

        }

        return retrofit;
    }

}
