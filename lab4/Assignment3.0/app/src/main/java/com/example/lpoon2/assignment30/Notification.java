package com.example.lpoon2.assignment30;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class Notification extends Fragment {

    // TODO: Customize parameter argument names
    private static final String MSG_URL = "column-count";
    // TODO: Customize parameters
    private String url = "";
    private List<NotificationObj> notificationsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotificationAdapter nadtr;
    //private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public Notification() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static Notification newInstance(String msg) {
        Notification fragment = new Notification();
        Bundle args = new Bundle();
        args.putString(MSG_URL, msg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            url = getArguments().getString(MSG_URL);
            GETNotifications repos = new GETNotifications();
            // update the main content by replacing fragments
            try {
                Object result = repos.execute(url).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=  inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.notification_list);

        nadtr = new NotificationAdapter(notificationsList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(nadtr);
        return rootView;
    }


    /**
     * A private class that extends AsyncTask class. This class is set up for
     * making http request to Github api
     */
    private class GETNotifications extends AsyncTask<String, Void, String> {

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

            try {
                link = new URL(params[0]);

                urlConnection = (HttpURLConnection) link
                        .openConnection();

                //reading input from stream
                InputStream in = urlConnection.getInputStream();
                BufferedReader isw = new BufferedReader(new InputStreamReader(in));
                String str ="";
                if (in != null) {
                    while ((str = isw.readLine()) != null) {
                        sb.append(str + "\n");
                    }
                }

                //populating array of user information
                JSONArray repositories = new JSONArray(sb.toString());
                JSONObject single = null;
                JSONObject subject = null;

                for(int i = 0; i< repositories.length(); i++){
                    NotificationObj n = new NotificationObj("","","",true);
                    single = repositories.getJSONObject(i);
                    n.setUnread(single.getBoolean("unread"));
                    n.setDate(single.getString("updated_at"));
                    subject = single.getJSONObject("subject");
                    n.setTitle(subject.getString("title"));
                    notificationsList.add(n);
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
