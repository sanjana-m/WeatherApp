package com.weatherapp.settings;

import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.weatherapp.R;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {
    public static final String TEMPERATURE_OPTION = "temperatureChoice";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
