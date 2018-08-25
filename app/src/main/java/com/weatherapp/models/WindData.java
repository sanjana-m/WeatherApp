package com.weatherapp.models;

import com.google.gson.annotations.SerializedName;
import com.weatherapp.utils.GeneralUtils;

import java.util.Locale;

/**
 * Created by sanjana on 23/08/18.
 */
public class WindData {
    public double getSpeed() {
        return speed;
    }

    public double getDegree() {
        return degree;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "Speed: %.3fm/s, angle: %.3f\u00B0", speed, degree);
    }

    private double speed;
    @SerializedName("deg")
    private double degree;
}
