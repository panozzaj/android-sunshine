package com.repeatableventures.android.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ForecastFragment extends Fragment {

    private final String TAG = ForecastFragment.class.getSimpleName();
    private ArrayAdapter<String> forecastAdapter;

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
            new FetchWeatherTask().execute("90210");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        //String[] data = {
        //        "Mon 6/23 - Sunny - 31/17",
        //        "Tue 6/24 - Foggy - 21/8",
        //        "Wed 6/25 - Cloudy - 22/17",
        //        "Thurs 6/26 - Rainy - 18/11",
        //        "Fri 6/27 - Foggy - 21/10",
        //        "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
        //        "Sun 6/29 - Sunny - 20/7"
        //};
        //List <String> weekForecast = new ArrayList<String>(Arrays.asList(data));
        forecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview);
                //weekForecast);

        ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);

        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, ArrayList<String>> {

        private final String TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            Log.w("here", "here");
            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.openweathermap.org")
                        .appendPath("data")
                        .appendPath("2.5")
                        .appendPath("forecast")
                        .appendPath("daily")
                        .appendQueryParameter("q", params[0] + ",USA")
                        .appendQueryParameter("mode", "json")
                        .appendQueryParameter("units", "metric")
                        .appendQueryParameter("cnt", "7");
                URL url = new URL(builder.build().toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            Log.w("there", "there");
            ArrayList<String> decoded = WeatherDataParser.decodeJson(forecastJsonStr);
            for (String d : decoded) {
                Log.w("decoded", d);
            }
            return decoded;
        }

        @Override
        protected void onPostExecute(ArrayList<String> dayForecastStrs) {
            Log.w("FetchWeatherTask", "in onPostExecute");
            if (dayForecastStrs != null) {
                forecastAdapter.clear();
                for (String dayForecastStr: dayForecastStrs) {
                    forecastAdapter.add(dayForecastStr);
                }
            }
        }
    }
}
