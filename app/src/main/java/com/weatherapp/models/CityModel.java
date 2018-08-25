package com.weatherapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sanjana on 23/08/18.
 */

public class CityModel {
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public long getPopulation() {
        return population;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    private long id;
    private String name;
    private String country;
    private long population;
    @SerializedName("coord")
    private Coordinates coordinates;

    public class Coordinates {
        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        @SerializedName("lat")
        private double latitude;
        @SerializedName("lon")
        private double longitude;
    }
}
