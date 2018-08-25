package com.weatherapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by sanjana on 23/08/18.
 */

public class ForecastModel {
    public List<WeatherModel> getWeatherList() {
        return weatherList;
    }

    public CityModel getCountry() {
        return city;
    }

    public List<WeatherModel> getNextDaysForecast(int num) {
        if (weatherList == null || weatherList.isEmpty())
            return new ArrayList<>();

        List<WeatherModel> filteredList = new ArrayList<>();

        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);

        if (c.get(Calendar.MINUTE) >= 30)
            c.add(Calendar.HOUR, 1);

        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        // Rounding to the nearest 3rd hour
        if (c.get(Calendar.HOUR) % 3 == 1)
            c.add(Calendar.HOUR, 2);
        else if (c.get(Calendar.HOUR) % 3 == 2)
            c.add(Calendar.HOUR, 1);

        int nextDay = 0;
        // If nearest 3rd hour is the next day, add one day to inner loop processing
        if(c.get(Calendar.DATE) - (Calendar.getInstance().get(Calendar.DATE)) == 1)
            nextDay = 1;

        for (WeatherModel weatherModel : weatherList) {
            if (filteredList.size() >= num + 1)
                break;
            Date weatherDate = weatherModel.getParsedForecastDate();
            if (weatherDate != null) {
                // get first instance of weather with given hour, add to filtered list, then add a day to calendar
                if (weatherDate.equals(c.getTime()) || weatherDate.after(c.getTime())) {
                    switch (weatherDate.getDate() - ((new Date()).getDate()) - nextDay) {
                        case 0:
                            weatherModel.setDisplayDate("Today's weather");
                            break;
                        case 1:
                            weatherModel.setDisplayDate("Tomorrow's weather");
                            break;
                        case 2:
                            weatherModel.setDisplayDate("Day after\'s weather");
                            break;
                        case 3:
                            weatherModel.setDisplayDate("Weather in three days");
                            break;
                    }

                    filteredList.add(weatherModel);
                    c.add(Calendar.DATE, 1);
                }
            }
        }
        return filteredList;
    }

    @SerializedName("list")
    private List<WeatherModel> weatherList;
    private CityModel city;
}
