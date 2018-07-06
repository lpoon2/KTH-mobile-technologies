package com.example.lpoon2.assignment30;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link Following#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Following extends Fragment {  // url for the lit of followers
    private static final String followers_url = "param1";

    private String url;

    // different arrays for storing user's information
    private static ArrayList<String> follower_names = new ArrayList<>();
    private static ArrayList<String> imgs_urls = new ArrayList<>();
    private static ArrayList<String> html_urls = new ArrayList<>();
    private List<FollowerObj> followerList = new ArrayList<>();
    private List<Drawable> drawableList = new ArrayList<>();

    private String TOKEN = "?access_token=f1ebbe990f6f59ddd489df0206aa487be157e48c";

    // view and adapter
    private Button btn;
    private RecyclerView recyclerView;
    private FollowerAdapter fadtr;
    private EditText search;
    private ProgressBar progressBar;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Follower.
     */
    public static Following newInstance(String param1) {
        Following fragment = new Following();
        Bundle args = new Bundle();
        args.putString(followers_url, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public Following() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // restore the value in follower_url in url
        if (getArguments() != null) {
            url = getArguments().getString(followers_url);
            GETFollowers repos = new GETFollowers();
            // update the main content by replacing fragments
            try {
                Object result = repos.execute(url, "0").get();
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
        // Inflate the layout for this fragment
        View rootView=  inflater.inflate(R.layout.fragment_followers, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.follower_list);
        search = (EditText) rootView.findViewById(R.id.search);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        btn = (Button) rootView.findViewById(R.id.search_buttoon);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Search(search.getText().toString());
            }
        });

        //set up adapter and recycler view
        fadtr = new FollowerAdapter(followerList, drawableList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fadtr);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new Repositories.ClickListener() {

            // onclick handler
            @Override
            public void onClick(View view, int position) {
                FollowerObj f = followerList.get(position);
//                Toast.makeText(getActivity(), f.getName() + " is selected!", Toast.LENGTH_SHORT).show();
                Fragment fragment = null;
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FollowerObj follower = followerList.get(position);
                fragment = new Profile().newInstance(follower.getFull_name(), follower.getName(), follower.getImg(), Integer.parseInt(follower.getNum_repos())
                        , Integer.parseInt(follower.getFollowers()),Integer.parseInt(follower.getFollowing()), follower.getCreated_date());
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            }

            @Override
            public void onLongClick(View view, int position) {
                //TODO: implement long-click behavior
            }
        }));
        return rootView;
    }
    /**
     * A private class that extends AsyncTask class. This class is set up for
     * making http request to Github api
     */
    private class GETFollowers extends AsyncTask<String, Void, String> {

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

                //open http connection
                urlConnection = (HttpURLConnection) link.openConnection();

                //get string from the response
                InputStream in = urlConnection.getInputStream();
                BufferedReader isw = new BufferedReader(new InputStreamReader(in));
                String str ="";
                if (in != null) {
                    while ((str = isw.readLine()) != null) {
                        sb.append(str + "\n");
                    }
                }

                JSONArray repositories;
                if (params[1].compareTo("0") == 0)
                    repositories = new JSONArray(sb.toString());
                else {
                    followerList.clear();
                    JSONObject search_res = new JSONObject(sb.toString());
                    repositories = search_res.getJSONArray("items");
                }
                JSONObject single = null;
                Drawable d = null;

                //create follower object
                for(int i = 0; i< repositories.length(); i++){
                    single = repositories.getJSONObject(i);
                    follower_names.add(single.getString("login"));
                    url = " https://api.github.com/users/"+ single.getString("login")+TOKEN;
                    imgs_urls.add(single.getString("avatar_url"));
                    html_urls.add(single.getString("html_url"));
                    FollowerObj f = getFollowerPofile(url);
                    followerList.add(f);

                    InputStream is = (InputStream) new URL(single.getString("avatar_url")).getContent();
                    d = Drawable.createFromStream(is, "src name");
                    f.setImage(d);
                    drawableList.add(d);
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

    public FollowerObj getFollowerPofile(String follow_url){
        URL link = null;
        StringBuilder sb = new StringBuilder();
        FollowerObj f = null;
        try {

            link = new URL(follow_url);

            //open http connection
            HttpURLConnection urlConnection = (HttpURLConnection) link.openConnection();

            //get string from the response
            InputStream in = urlConnection.getInputStream();
            BufferedReader isw = new BufferedReader(new InputStreamReader(in));
            String str ="";

            if (in != null) {
                while ((str = isw.readLine()) != null) {
                    sb.append(str + "\n");
                }
            }

            JSONObject json = new JSONObject(sb.toString());

            f = new FollowerObj(json.getString("name"), json.getString("login"),json.getString("html_url"), json.getString("avatar_url"),
                    json.getString("public_repos"), Integer.toString(json.getInt("followers")),Integer.toString(json.getInt("following")),json.getString("created_at"));

            return f;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return f ;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (item.getItemId() == R.id.action_example) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = new Notification().newInstance("https://api.github.com/notifications?access_token=f1ebbe990f6f59ddd489df0206aa487be157e48c");
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return true;
        }

        if (id == R.id.action_settings) {
            Toast.makeText(getActivity(), getActivity().getLocalClassName(),
                    Toast.LENGTH_SHORT).show();
            return true;
        }
        if(id == R.id.signout){
            FirebaseAuth.getInstance().signOut();
        }
        if(id == R.id.store){

            FirebaseDatabase database = FirebaseDatabase.getInstance();

            for(int i = 0; i < followerList.size(); i++){
                DatabaseReference myRef = database.getReference("following/lpoon2");
                myRef.push().setValue(followerList.get(i));
            }

            //String full_name, String name, String url, String img, String num_repos, String followers, String following, String created_date
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * Filter list content in recycler view
     * @param text
     */
    void filter(String text){
        List<FollowerObj> temp = new ArrayList();
        for(FollowerObj d: this.followerList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getName().toLowerCase().contains(text.toLowerCase()) || d.getFull_name().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        //update recyclerview
        fadtr.updateList(temp);
    }

    void Search(String query){
        url = "https://api.github.com/search/users?q=" + query;
        GETFollowers repos = new GETFollowers();
        // update the main content by replacing fragments
        try {
            Object result = repos.execute(url,"1").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        List<FollowerObj> temp = new ArrayList();

        for(FollowerObj d: this.followerList){
            temp.add(d);
        }
        progressBar.setVisibility(View.GONE);
        //update recyclerview
        fadtr.updateList(temp);
    }
}
