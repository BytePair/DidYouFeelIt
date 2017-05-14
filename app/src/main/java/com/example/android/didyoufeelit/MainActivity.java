/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.didyoufeelit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Displays the perceived strength of a single earthquake event based on responses from people who
 * felt the earthquake.
 **/
public class MainActivity extends AppCompatActivity {


    /** URL for earthquake data from the USGS dataset **/
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2016-01-01&endtime=2016-05-02&minfelt=50&minmagnitude=5";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create new earthquake task to fetch data in background
        EarthquakeAsyncTask earthquakeAsyncTask = new EarthquakeAsyncTask();
        earthquakeAsyncTask.execute(USGS_REQUEST_URL);
    }


    /** Create Async Class to fetch earthquake data **/
    private class EarthquakeAsyncTask extends AsyncTask<String, Void, Event> {

        /**
         * This method is invoked (or called) on a background thread, so we can perform
         * long-running operations like making a network request.
         *
         * It is NOT okay to update the UI from a background thread, so we just return an
         * {@link Event} object as the result.
         **/
        @Override
        protected Event doInBackground(String... urlString) {

            // Make sure we got at least 1 string
            if (urlString.length < 1 || urlString[0] == null) {
                // Have to return null because onPostExecute still expects an object
                return null;
            }

            // Perform the HTTP request for earthquake data and process the response.
            return Utils.fetchEarthquakeData(urlString[0]);
        }

        /**
         * This method is invoked on the main UI thread after the background work has been
         * completed.
         *
         * It IS okay to modify the UI within this method. We take the {@link Event} object
         * (which was returned from the doInBackground() method) and update the views on the screen.
         **/
        @Override
        protected void onPostExecute(Event earthquake) {

            // If event is null, do not attempt to update ui
            if (earthquake == null) {
                // just inform user via toast we couldn't find anything
                Toast.makeText(getApplicationContext(), "No earthquake data found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update the information displayed to the user
            updateUi(earthquake);
        }
    }


    /**
     * Update the UI with the given earthquake information.
     **/
    private void updateUi(Event earthquake) {
        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(earthquake.title);

        TextView tsunamiTextView = (TextView) findViewById(R.id.number_of_people);
        tsunamiTextView.setText(getString(R.string.num_people_felt_it, earthquake.numOfPeople));

        TextView magnitudeTextView = (TextView) findViewById(R.id.perceived_magnitude);
        magnitudeTextView.setText(earthquake.perceivedStrength);
    }
}
