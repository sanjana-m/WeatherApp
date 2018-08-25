package com.weatherapp.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weatherapp.R;
import com.weatherapp.models.MainModel;
import com.weatherapp.models.WeatherModel;
import com.weatherapp.utils.GeneralUtils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * RecyclerViewAdapter to display a list of cards containing forecast details
 * Created by sanjana on 23/08/18.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private WeakReference<Activity> activity;
    protected List<WeatherModel> models;
    private boolean[] visibleStates;

    private int option = 0;

    enum WEATHER {
        FREEZING, COLD, COOL, PLEASANT, WARM, HOT, TORRID
    }

    private String[] colors = {"#4242f4", "#2985db", "#adecf7", "#f4f142", "#efb55d", "#f7681b", "#e83737"};

    public WeatherAdapter(WeakReference<Activity> activity, List<WeatherModel> models) {
        this.activity = activity;
        this.models = models;
        this.visibleStates = new boolean[models == null ? 0 : models.size()];
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_weather_details, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final WeatherViewHolder holder, final int position) {
        if (getItemCount() == 0 || models == null || position >= models.size())
            return;

        final WeatherModel model = models.get(position);
        final MainModel mainModel = model.getMain();

        try {
            holder.weatherDay.setText(model.getDisplayDate());
        } catch (NullPointerException e) {
            holder.weatherDay.setText((activity != null && activity.get() != null && !GeneralUtils.isStringEmpty(activity.get().getString(R.string.na)))
                    ? activity.get().getString(R.string.weather)
                    : "Weather");
        }

        String na = (activity != null && activity.get() != null && !GeneralUtils.isStringEmpty(activity.get().getString(R.string.na)))
                ? activity.get().getString(R.string.na)
                : "NA";
        try {
            holder.temperatureValue.setText(mainModel.getTemperatureString(option));
        } catch (NullPointerException e) {
            holder.temperatureValue.setText("\u2014");
        }

        // Try setting temperature color
        try {
            double temperature = mainModel.getTemperature(0);

            int color = 0;
            int colorPosition = -1;

            if (temperature < 0.0f)
                colorPosition = WEATHER.FREEZING.ordinal();
            else if (temperature < 10)
                colorPosition = WEATHER.COLD.ordinal();
            else if (temperature < 20)
                colorPosition = WEATHER.COOL.ordinal();
            else if (temperature < 25)
                colorPosition = WEATHER.PLEASANT.ordinal();
            else if (temperature < 30)
                colorPosition = WEATHER.WARM.ordinal();
            else if (temperature < 40)
                colorPosition = WEATHER.HOT.ordinal();
            else
                colorPosition = WEATHER.TORRID.ordinal();

            holder.temperatureValue.setTextColor(Color.parseColor(colors[colorPosition]));
        } catch (Exception e) {
            holder.temperatureValue.setTextColor(Color.parseColor("#000000"));
        }

        try {
            holder.descriptionValue.setText(model.getWeatherList().get(0).getWeatherType());
        } catch (NullPointerException e) {
            holder.descriptionValue.setText(na);
        }

        try {
            holder.longDescriptionValue.setText(model.getWeatherList().get(0).getWeatherDescription());
        } catch (NullPointerException e) {
            holder.longDescriptionValue.setText(na);
        }

        // If there is a visible state, show more view
        if ((visibleStates != null
                && position < visibleStates.length
                && visibleStates[position])) {
            holder.moreView.setVisibility(View.VISIBLE);
            holder.moreIcon.setAlpha(0.37f);
        } else {
            holder.moreIcon.setAlpha(1f);
            holder.moreView.setVisibility(View.GONE);
        }

        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.moreView.getVisibility() == View.VISIBLE) {
                    holder.moreIcon.setAlpha(1f);
                    holder.moreView.setVisibility(View.GONE);
                    if (position < visibleStates.length)
                        visibleStates[position] = false;
                } else {
                    holder.moreIcon.setAlpha(0.37f);
                    holder.moreView.setVisibility(View.VISIBLE);
                    if (position < visibleStates.length)
                        visibleStates[position] = true;
                }
            }
        });


        try {
            holder.humidityValue.setText(String.format(Locale.ENGLISH, "%.2f%%", mainModel.getHumidity()));
        } catch (NullPointerException e) {
            holder.humidityValue.setText("\u2014");
        }

        try {
            holder.pressureValue.setText(String.format(Locale.ENGLISH, "%.3fhPa", mainModel.getPressure()));
        } catch (NullPointerException e) {
            holder.pressureValue.setText("\u2014");
        }

        try {
            holder.rainValue.setText(String.format(Locale.ENGLISH, "%.3fmm", model.getRainData().getRain()));
        } catch (NullPointerException e) {
            holder.rainValue.setText("\u2014");
        }

        try {
            holder.windValue.setText(model.getWindData().toString());
        } catch (NullPointerException e) {
            holder.windValue.setText("\u2014");
        }

        holder.shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String formattedDate = (new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(model.getParsedForecastDate()));

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, String.format(Locale.ENGLISH,
                        "The weather forecast on %s is: \n%s",
                        formattedDate,
                        model.toString()));
                if (activity.get() != null)
                    activity.get().startActivity(Intent.createChooser(intent, "Share using"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return (models == null || models.isEmpty()) ? 0 : models.size();
    }

    public boolean[] getVisibleStates() {
        return visibleStates;
    }

    public void setVisibleStates(boolean[] visibleStates) {
        if (visibleStates == null || visibleStates.length != models.size())
            this.visibleStates = new boolean[models.size()];
        else
            this.visibleStates = visibleStates;
        notifyDataSetChanged();
    }

    public void setTemperatureOption(int option) {
        this.option = option;
        notifyDataSetChanged();
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        // The direct child of the CardView
        @BindView(R.id.ll_weather_main) public LinearLayout mainView;

        @BindView(R.id.tv_weather_day) public TextView weatherDay;

        @BindView(R.id.tv_temperature_value) TextView temperatureValue;
        @BindView(R.id.tv_desc_value) TextView descriptionValue;
        @BindView(R.id.tv_long_desc_value) TextView longDescriptionValue;

        @BindView(R.id.iv_weather_more) ImageView moreIcon;

        // Hidden by default, if visible, this displays additional weather data, represented by the fields below
        @BindView(R.id.ll_weather_extra) public LinearLayout moreView;

        @BindView(R.id.tv_humidity_value) TextView humidityValue;
        @BindView(R.id.tv_pressure_value) TextView pressureValue;
        @BindView(R.id.tv_rain_value) TextView rainValue;
        @BindView(R.id.tv_wind_value) TextView windValue;

        // Shares weather forecast in plaintext to sharing applications
        @BindView(R.id.iv_share) ImageView shareIcon;

        public WeatherViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
