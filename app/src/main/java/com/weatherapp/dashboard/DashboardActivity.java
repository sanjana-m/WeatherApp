package com.weatherapp.dashboard;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.weatherapp.R;
import com.weatherapp.models.ForecastModel;
import com.weatherapp.models.WeatherModel;
import com.weatherapp.network.RetrofitClient;
import com.weatherapp.network.WeatherForecastService;
import com.weatherapp.settings.SettingsActivity;
import com.weatherapp.utils.Constants;
import com.weatherapp.utils.GeneralUtils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This is the main class and launcher activity.
 * This makes a call to the weather API with the city set to Chennai, which can be changed by the Spinner on top of the page
 */

public class DashboardActivity extends AppCompatActivity {
    private static String TAG = DashboardActivity.class.getSimpleName();

    Unbinder unbinder;

    @BindView(R.id.spinner_city) Spinner city;
    @BindView(R.id.tv_weather_time) TextView time;
    @BindView(R.id.rv_weather) RecyclerView weather;

    private final int PERMISSION_FINE_LOCATION = 101;

    // Stores the state of weather cards, if true - extra view is shown, if false - extra view is hidden
    private boolean[] visibleStates;

    // Retrofit caller
    private WeatherForecastService weatherForecastService;

    // Location service
    private AppLocationService appLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        try {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null)
                actionBar.setTitle("Weather forecast");
        } catch (NullPointerException e) {
            Log.e(TAG, "Action bar is null");
        }

        unbinder = ButterKnife.bind(this);

        visibleStates = (savedInstanceState == null
                || !savedInstanceState.containsKey("cardsOpen")
                || savedInstanceState.getBooleanArray("cardsOpen") == null)
                ? new boolean[10]
                : savedInstanceState.getBooleanArray("cardsOpen");

        appLocationService = new AppLocationService(getApplicationContext());
        weatherForecastService = RetrofitClient.getClient().create(WeatherForecastService.class);

        initUI();
    }

    private void initUI() {
        final String[] cityList = getResources().getStringArray(R.array.city_list);

        if (city == null)
            city = findViewById(R.id.spinner_city);

        city.setPrompt("Choose your city");

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= cityList.length)
                    position = 0;
                final String cityName = cityList[position];
                if (position == cityList.length - 1) {
                    // Request permissions if not there
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
                            requestPermissions(permissions, PERMISSION_FINE_LOCATION);
                        } else {
                            // get location from network provider
                            double[] coords = getDataFromLocation(LocationManager.NETWORK_PROVIDER);
                            if (coords != null && coords.length == 2) {
                                getForecastData(getForecastCall(coords[0], coords[1]));
                            } else {
                                // otherwise get location from GPS provider
                                coords = getDataFromLocation(LocationManager.GPS_PROVIDER);
                                if (coords != null && coords.length == 2) {
                                    getForecastData(getForecastCall(coords[0], coords[1]));
                                } else {
                                    // otherwise, display error
                                    Toast.makeText(getApplicationContext(), "There seems to be an error in obtaining your location.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                } else {
                    getForecastData(getForecastCall(cityName));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getForecastData(getForecastCall("Chennai"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean temperatureOption = sharedPref.getBoolean(SettingsActivity.TEMPERATURE_OPTION, false);

        if (weather != null && weather.getAdapter() != null) {
            WeatherAdapter adapter = (WeatherAdapter) weather.getAdapter();
            adapter.setTemperatureOption(temperatureOption ? 1 : 0);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState == null)
            outState = new Bundle();

        if (weather != null && weather.getAdapter() != null) {
            WeatherAdapter adapter = (WeatherAdapter) weather.getAdapter();
            outState.putBooleanArray("cardsOpen", adapter.getVisibleStates());
        }
        super.onSaveInstanceState(outState);
    }

    private double[] getDataFromLocation(String provider) {
        if (GeneralUtils.isStringEmpty(provider))
            return null;
        Location location = appLocationService.getLocation(getApplicationContext(), provider);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.e("Latitude: " + location.getLatitude(), " Longitude: " + location.getLongitude());
            double[] coords = {latitude, longitude};
            return coords;
        }
        return null;
    }

    private Call<ForecastModel> getForecastCall(String city) {
        return weatherForecastService.getForecast((GeneralUtils.isStringEmpty(city) ? "Chennai" : city.trim().toLowerCase()),
                "json",
                getResources().getString(R.string.api_key));
    }

    private Call<ForecastModel> getForecastCall(double latitude, double longitude) {
        return weatherForecastService.getForecast(String.format(Locale.ENGLISH, "%.3f", latitude),
                String.format(Locale.ENGLISH, "%.3f", longitude),
                "json",
                getResources().getString(R.string.api_key));
    }

    private void getForecastData(Call<ForecastModel> weatherForecastData) {
        weatherForecastData.enqueue(new Callback<ForecastModel>() {
            @Override
            public void onResponse(Call<ForecastModel> call, Response<ForecastModel> response) {
                if (response.code() != Constants.SUCCESS) {
                    processError(response.code(), response.message());
                } else {
                    if (response.body() == null) {
                        processError(0, "Response body is null");
                    } else {
                        ForecastModel forecastModel = response.body();
                        if (forecastModel == null || forecastModel.getNextDaysForecast(3) == null) {
                            processError(0, "There are no forecasts");
                        } else {
                            if (forecastModel.getWeatherList() != null && !forecastModel.getWeatherList().isEmpty())
                                time.setText((new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)).format(new Date()));

                            // Get today's and next 3 days' forecast from the list of forecasts
                            List<WeatherModel> list = forecastModel.getNextDaysForecast(3);
                            WeatherAdapter adapter = new WeatherAdapter(new WeakReference<Activity>(DashboardActivity.this), list);

                            // Set visible states
                            if (visibleStates != null && visibleStates.length == list.size())
                                adapter.setVisibleStates(visibleStates);

                            // Check if temperature is to be displayed in Celsius or Fahrenheit
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            boolean temperatureOption = sharedPref.getBoolean(SettingsActivity.TEMPERATURE_OPTION, false);
                            adapter.setTemperatureOption(temperatureOption ? 1 : 0);

                            // Set it in RecyclerView
                            weather.setAdapter(adapter);
                            weather.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        }
                    }
                }
            }

            private void processError(final int errorCode, final String errorResponse) {
                if (!isFinishing() || !isDestroyed()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DashboardActivity.this,
                                    (errorCode == 0 ? "" : errorCode) + " - " + errorResponse,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ForecastModel> call, Throwable t) {
                call.cancel();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions.length == 0)
            return;
        switch (requestCode) {
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (city != null && city.getAdapter() != null && city.getAdapter().getCount() > 0) {
                        // If permission is granted, click the last item in city spinner
                        city.setSelection(city.getAdapter().getCount() - 1);
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null)
            unbinder.unbind();
        super.onDestroy();
    }
}
