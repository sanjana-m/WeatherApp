package com.weatherapp.models;

import com.google.gson.annotations.SerializedName;
import com.weatherapp.utils.GeneralUtils;

import java.util.Locale;

/**
 * Created by sanjana on 23/08/18.
 */
public class RainData {
    public double getRain() {
        return rain;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "Rain \u2014 %.3fmm", rain);
    }

    @SerializedName("3h")
    private double rain;
}
