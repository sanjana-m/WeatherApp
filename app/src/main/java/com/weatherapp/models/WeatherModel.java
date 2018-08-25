package com.weatherapp.models;

import com.google.gson.annotations.SerializedName;
import com.weatherapp.utils.GeneralUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by sanjana on 23/08/18.
 */

public class WeatherModel {
    public long getDate() {
        return date;
    }

    public MainModel getMain() {
        return main;
    }

    public List<WeatherData> getWeatherList() {
        return weatherList;
    }

    public WindData getWindData() {
        return windData;
    }

    public RainData getRainData() {
        return rainData;
    }

    public String getForecastDate() {
        return forecastDate;
    }

    public Date getParsedForecastDate() {
        if (!GeneralUtils.isStringEmpty(forecastDate)) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(forecastDate);
            } catch (ParseException e) {
                return null;
            }
        }
        return null;
    }

    public String getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

    @Override
    public String toString() {
        String weatherText = "";
        if (getMain() != null && !GeneralUtils.isStringEmpty(getMain().toString()))
            weatherText += getMain().toString() + "\n";
        if (getWeatherList() != null && !getWeatherList().isEmpty()) {
            WeatherData weatherData = getWeatherList().get(0);
            if (weatherData != null && !GeneralUtils.isStringEmpty(weatherData.toString()))
                weatherText += weatherData.toString() + "\n";
        }
        if (getWindData() != null && !GeneralUtils.isStringEmpty(getWindData().toString()))
            weatherText += getWindData().toString() + "\n";
        if (getRainData() != null && !GeneralUtils.isStringEmpty(getRainData().toString()))
            weatherText += getRainData().toString();
        return weatherText;
    }

    @SerializedName("dt")
    private long date;
    private MainModel main;
    @SerializedName("weather")
    private List<WeatherData> weatherList;
    @SerializedName("wind")
    private WindData windData;
    @SerializedName("rain")
    private RainData rainData;
    @SerializedName("dt_txt")
    private String forecastDate;
    private String displayDate;
}

