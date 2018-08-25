package com.weatherapp.utils;

import com.weatherapp.BuildConfig;

/**
 * Created by sanjana on 23/08/18.
 */

public class Constants {
    public static final boolean DEBUG = BuildConfig.DEBUG;

    public static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    // status codes
    public static final int SUCCESS = 200;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int SERVER_ERROR = 500;
}
