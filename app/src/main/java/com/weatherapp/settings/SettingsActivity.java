package com.weatherapp.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.weatherapp.R;

/**
 * Simple preference activity which loads the preference XML contaning choice of temperature metric
 */

public class SettingsActivity extends PreferenceActivity {
    public static final String TEMPERATURE_OPTION = "temperatureChoice";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
