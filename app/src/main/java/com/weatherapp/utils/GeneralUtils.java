package com.weatherapp.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by sanjana on 23/08/18.
 */

public class GeneralUtils {
    public static String getThreeDecimalPlaces(double value) {
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(value);
    }

    public static boolean isStringEmpty(String value) {
        return value == null || "".equalsIgnoreCase(value.trim());
    }
}
