package com.repeatableventures.android.sunshine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ForecastFragment extends Fragment {

    private final String TAG = ForecastFragment.class.getSimpleName();

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.forecastfragment, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            Log.i(TAG, "tapped settings button");
            new FetchWeatherTask().execute("90210");
            Log.i(TAG, "kicked off task");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList weekForecast = new ArrayList();
        weekForecast.add("Today - Sunny - 88˚ / 63˚");
        weekForecast.add("Tomorrow - Foggy - 70˚ / 46˚");
        weekForecast.add("Tuesday - Sunny - 88˚ / 63˚");
        weekForecast.add("Wednesday - Sunny - 88˚ / 63˚");
        weekForecast.add("Thursday - Sunny - 88˚ / 63˚");
        weekForecast.add("Friday - Sunny - 88˚ / 63˚");
        weekForecast.add("Saturday - Cloudy - 44˚ / 31˚");
        weekForecast.add("Sun's Day - Cloudy - 54˚ / 31˚");
        weekForecast.add("Moon's Day - Froggy - 44˚ / 31˚");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                weekForecast);

        ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);

        return rootView;
    }
}
