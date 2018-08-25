package com.weatherapp.models;

import com.google.gson.annotations.SerializedName;
import com.weatherapp.utils.GeneralUtils;

import java.util.Locale;

/**
 * Created by sanjana on 23/08/18.
 */
public class MainModel {
    public double getTemperature(int option) {
        switch(option) {
            case 0: return temperature - 273.15;
            case 1: return (temperature * (9 / 5)) - 459.67;
            default: return temperature;
        }
    }

    public String getTemperatureString(int option) {
        switch (option) {
            case 0: // CELSIUS
                return GeneralUtils.getThreeDecimalPlaces(temperature - 273.15) + "\u2103";
            case 1: // FAHRENHEIT
                return GeneralUtils.getThreeDecimalPlaces(((temperature  - 273.15) * (9/5)) + 32) + "\u2109";
            default:
                return GeneralUtils.getThreeDecimalPlaces(temperature);
        }
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public double getPressure() {
        return pressure;
    }

    public double getSeaLevel() {
        return seaLevel;
    }

    public double getGroundLevel() {
        return groundLevel;
    }

    public double getHumidity() {
        return humidity;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH,
                "Temperature \u2014 %s\n" +
                        "Pressure \u2014 %.3fhPa\n" +
                        "Humidity \u2014 %.2f%%",
                getTemperatureString(0),
                pressure,
                humidity);
    }

    @SerializedName("temp")
    private double temperature;
    @SerializedName("temp_min")
    private double minTemperature;
    @SerializedName("temp_max")
    private double maxTemperature;
    private double pressure;
    @SerializedName("sea_level")
    private double seaLevel;
    @SerializedName("grnd_level")
    private double groundLevel;
    private double humidity;
}
