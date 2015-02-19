package com.repeatableventures.android.sunshine;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by anthony on 2/11/15.
 */
public class WeatherDataParser {
    public static double getMaxTemperatureForDay(String rawData, int dayIndex) throws JSONException {
        double result = new JSONObject(rawData)
                .getJSONArray("list")
                .getJSONObject(dayIndex)
                .getJSONObject("temp")
                .getDouble("max");
        return result;
    }

    public static Collection<String> decodeJson(String json) {
        final Gson gson = new Gson();
        final Forecast weather = gson.fromJson(json, Forecast.class);

        final ArrayList<String> decodedJson = new ArrayList<String>();
        for (DailyForecast df : weather.list) {
            Date date = new Date((long) (df.dt * 1000L));
            String dateString = new SimpleDateFormat("EEE, MMM dd").format(date);
            decodedJson.add(String.format("%s - %s -  %s/%s", dateString, df.weather.main, df.temp.max, df.temp.min));
        }

        return decodedJson;
    }

    private class Forecast {
        public String cod;
        public String message;
        public List<DailyForecast> list;
    }

    private class DailyForecast {
        public Double dt;
        public Temperature temp;
        public Weather weather;
        public Double pressure;
        public Double humidity;
        public Double speed;
        public Double deg;
        public Double clouds;
    }

    private class Temperature {
        public Double day;
        public Double min;
        public Double max;
        public Double night;
        public Double eve;
        public Double morn;
    }

    private class Weather {
        public Double id;
        public String main;
        public String description;
        public String icon;
    }
}

