package com.weatherapp.network;

import com.weatherapp.models.ForecastModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sanjana on 23/08/18.
 */

public interface WeatherForecastService {
    @GET("forecast")
    Call<ForecastModel> getForecast(@Query("q") String city, @Query("mode") String mode, @Query("appid") String appId);
    @GET("forecast")
    Call<ForecastModel> getForecast(@Query("lat") String latitude, @Query("lon") String longitude, @Query("mode") String mode, @Query("appid") String appId);
}
