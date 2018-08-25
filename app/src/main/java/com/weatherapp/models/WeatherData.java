package com.weatherapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sanjana on 23/08/18.
 */
public class WeatherData {
    public int getId() {
        return id;
    }

    public String getWeatherType() {
        return weatherType;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    @Override
    public String toString() {
        return weatherType + ", " + weatherDescription;
    }

    private int id;
    @SerializedName("main")
    private String weatherType;
    @SerializedName("description")
    private String weatherDescription;
}
