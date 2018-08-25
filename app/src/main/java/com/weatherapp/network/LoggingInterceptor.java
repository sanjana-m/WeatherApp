package com.weatherapp.network;

import android.util.Log;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sanjana on 23/08/18.
 */
public class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        try {
            long t1 = System.nanoTime();
            Log.e("REQUEST", String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            Log.e("RECEIVED", String.format(Locale.ENGLISH,
                    "Received response for %s in %.1fms%n%s\n%s",
                    response.request().url(), (t2 - t1) / 1e6d,
                    response.headers(),
                    response.toString()));

            return response;
        } catch (Exception e) {
            Log.e("LoggingInterceptor", e.getMessage());
        }
        return chain.proceed(request);
    }
}