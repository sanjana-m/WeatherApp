package com.weatherapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.weatherapp.dashboard.DashboardActivity;
import com.weatherapp.dashboard.WeatherAdapter;
import com.weatherapp.models.WeatherModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.lang.ref.WeakReference;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class WeatherAdapterTest {
    private TestableListAdapter adapter;
    private WeatherAdapter.WeatherViewHolder holder;
    private View mockView;

    @Before
    public void setUp() throws Exception {
        WeatherModel model1 = new WeatherModel();
        model1.setDisplayDate("Today's weather");
        WeatherModel model2 = new WeatherModel();
        model2.setDisplayDate("Tomorrow's weather");
        WeatherModel model3 = new WeatherModel();
        model3.setDisplayDate("Day after's weather");
        adapter = new TestableListAdapter(new WeakReference<Activity>(mock(DashboardActivity.class)), asList(model1, model2, model3));
        mockView = mock(View.class);
    }

    @Test
    public void itemCount() {
        adapter.setModels(null);
        assert adapter.getItemCount() == 0;

        WeatherModel model = new WeatherModel();
        adapter.setModels(asList(model, model, model));
        assert adapter.getItemCount() == 3;
    }

    @Test
    public void getItemAtPosition() {
        WeatherModel firstWeatherModel = new WeatherModel();
        WeatherModel secondWeatherModel = new WeatherModel();
        adapter.setModels(asList(firstWeatherModel, secondWeatherModel));
        assertThat(adapter.getItemAtPosition(0)).isEqualTo(firstWeatherModel);
        assertThat(adapter.getItemAtPosition(1)).isEqualTo(secondWeatherModel);
    }

    @Test
    public void onBindViewHolder_setsTextAndClickEventForWeatherModelView() {
        WeatherModel model = new WeatherModel();
        model.setDisplayDate("Today");

        adapter.setModels(asList(model));

        LayoutInflater inflater = LayoutInflater.from(RuntimeEnvironment.application);
        View listItemView = inflater.inflate(R.layout.view_weather_details, null, false);
        holder = new WeatherAdapter.WeatherViewHolder(listItemView);

        adapter.onBindViewHolder(holder, 0);

        assertThat(holder.weatherDay.getText().toString()).isEqualTo("Today");
        holder.mainView.performClick();
        assertThat(holder.moreView.getVisibility()).isEqualTo(View.VISIBLE);

        holder.mainView.performClick();
        assertThat(holder.moreView.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void onCreateViewHolder_returnsNewWeatherModelViewHolderOfCorrectLayout() {
        WeatherModel model = new WeatherModel();
        model.setDisplayDate("Today");

        LayoutInflater inflater = LayoutInflater.from(RuntimeEnvironment.application);
        mockView = inflater.inflate(R.layout.view_weather_details, null, false);

        TestableListAdapter testableAdapter = new TestableListAdapter(new WeakReference<Activity>(mock(DashboardActivity.class)), asList(model));
        testableAdapter.setMockView(mockView);
        WeatherAdapter.WeatherViewHolder modelViewHolder = testableAdapter.onCreateViewHolder(new FrameLayout(RuntimeEnvironment.application), 0);
        assertThat(modelViewHolder.itemView).hasSameClassAs(mockView);
    }

    //Here we subclass and override the test subject again so we can use a mock view for testing, instead of the real one.
    static class TestableListAdapter extends WeatherAdapter {
        View mockView;

        TestableListAdapter(WeakReference<Activity> activity, List<WeatherModel> models) {
            super(activity, models);
        }

        void setModels(List<WeatherModel> models) {
            this.models = models;
        }

        void setMockView(View mockView) {
            this.mockView = mockView;
        }

        WeatherModel getItemAtPosition(int position) {
            if (models == null || models.isEmpty() || position >= getItemCount())
                return null;
            return models.get(position);
        }
    }
}