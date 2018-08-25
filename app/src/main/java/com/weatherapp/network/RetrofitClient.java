package com.weatherapp.network;

import com.weatherapp.utils.Constants;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sanjana on 23/08/18.
 */

public class RetrofitClient {
    private static Retrofit retrofit;

    private static OkHttpClient client;

    static {
        initializeOkHttpClient();
        initializeRetrofitClient();
    }

    private static void initializeOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if(Constants.DEBUG)
                builder.addInterceptor(new LoggingInterceptor());
        client = builder.build();
    }

    private static void initializeRetrofitClient() {
        if(client == null)
            initializeOkHttpClient();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public static Retrofit getClient() {
        if(client == null)
            initializeOkHttpClient();
        if(retrofit == null)
            initializeRetrofitClient();
        return retrofit;
    }
}
