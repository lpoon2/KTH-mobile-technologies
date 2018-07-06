package com.example.lpoon2.assignment30;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link Stats#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Stats extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STATS_URL = "param1";

    // TODO: Rename and change types of parameters
    private String stats_url = "https://api.github.com/repos/nodejs/node/stats/participation";
    private ArrayList<Integer> commits = new ArrayList<>();

    public Stats() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Stats.
     */
    // TODO: Rename and change types and number of parameters
    public static Stats newInstance(String param1) {
        Stats fragment = new Stats();
        Bundle args = new Bundle();
        args.putString(STATS_URL, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GETCommit image = new GETCommit();
        Drawable drawable_img = null;

        // using Async Task to get drawable from the url
        try {
            Object result = image.execute(stats_url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_stats, container, false);
        ValueLineChart pc = (ValueLineChart) rootView.findViewById(R.id.cubiclinechart);

        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        for(int i=0; i< 52; i++){
            series.addPoint(new ValueLinePoint("week"+Integer.toString(i+1),commits.get(i)));
        }

        pc.addSeries(series);
        pc.startAnimation();

        return rootView;
    }

    /**
     * A private class that extends AsyncTask class. This class is set up for
     * making http request to Github api
     */
    private class GETCommit extends AsyncTask<String, Void, String> {

        /**
         * Sending http get in background. Because Andriod doesn't allow sending request in the main thread
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String[] params) {
            URL link = null;
            HttpURLConnection urlConnection = null;
            StringBuilder sb = new StringBuilder();

            //make connection
            try {
                link = new URL(stats_url);

                urlConnection = (HttpURLConnection) link
                        .openConnection();

                InputStream in = urlConnection.getInputStream();
                BufferedReader isw = new BufferedReader(new InputStreamReader(in));
                String str ="";

                if (in != null) {
                    while ((str = isw.readLine()) != null) {
                        sb.append(str + "\n");
                    }
                }

                // parse JSON object that represents user
                JSONObject json = new JSONObject(sb.toString());
                JSONArray arr = json.getJSONArray("all");
                for(int i = 0; i< arr.length(); i++) {
                    commits.add(arr.getInt(i));
                }

                } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

            }
            return sb.toString();

        }

    }



}
