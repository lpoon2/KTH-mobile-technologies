package com.example.lpoon2.assignment30;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

//https://www.androidhive.info/2016/01/android-working-with-recycler-view/
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * interface
 * to handle interaction events.
 * Use the {@link Repositories#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Repositories extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String repos_url= "param1";
    private static ArrayList<String> repo_names = new ArrayList<>();
    private static ArrayList<String> owner_names = new ArrayList<>();
    private static ArrayList<String> descriptions = new ArrayList<>();
    private static ArrayList<String> repos_urls = new ArrayList<>();

    private Button btn;
    private List<Repo> repoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EditText search;
    private ReposAdapter radtr;
    // TODO: Rename and change types of parameters
    private String url;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Repositories.
     */
    // TODO: Rename and change types and number of parameters
    public static Repositories newInstance(String param1){
        Repositories fragment = new Repositories();
        Bundle args = new Bundle();
        args.putString(repos_url, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public Repositories() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            url = getArguments().getString(repos_url);
            GETRepos repos = new GETRepos();
            // update the main content by replacing fragments
            try {
                Object result = repos.execute(url,"0").get();
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
        final View rootview = inflater.inflate(R.layout.fragment_repositories, container, false);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.repo_list);
        search = (EditText) rootview.findViewById(R.id.search2);

        btn = (Button) rootview.findViewById(R.id.search_buttoon);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Search(search.getText().toString());
            }

        });

        radtr = new ReposAdapter(repoList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(radtr);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new Repositories.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                Repo r = repoList.get(position);
                //Toast.makeText(getActivity(), r.getName() + " is selected!", Toast.LENGTH_SHORT).show();
                Fragment fragment = null;
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                //go to next fragment by clicking on recycler item
                fragment = new RepoDetail().newInstance(repos_urls.get(position));
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return rootview;
    }



    /**
     * A private class that extends AsyncTask class. This class is set up for
     * making http request to Github api
     */
    private class GETRepos extends AsyncTask<String, Void, String> {

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
                JSONArray repositories;
                if (params[1].compareTo("0") == 0)
                    repositories = new JSONArray(sb.toString());
                else {
                    repoList.clear();
                    JSONObject search_res = new JSONObject(sb.toString());
                    repositories = search_res.getJSONArray("items");
                }
                JSONObject single = null;
                JSONObject owner = null;

                for(int i = 0; i< repositories.length(); i++){
                    single = repositories.getJSONObject(i);
                    owner = single.getJSONObject("owner");
                    repo_names.add(single.getString("name"));
                    owner_names.add(owner.getString("login"));
                    descriptions.add(single.getString("description"));
                    repos_urls.add(single.getString("html_url"));
                    repoList.add(new Repo(single.getString("name"),owner.getString("login"), single.getString("description"), single.getString("html_url")));
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



    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }
    /**
     * A class to handle touch event in the recycle view
     */
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private Repositories.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final Repositories.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
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

            for(int i = 0; i < repoList.size(); i++){
                DatabaseReference myRef = database.getReference("repos/"+owner_names.get(i));
                myRef.push().setValue(repoList.get(i));
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
        List<Repo> temp = new ArrayList();
        for(Repo d: this.repoList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getName().toLowerCase().contains(text.toLowerCase()) || d.getDescription().toLowerCase().contains(text.toLowerCase())
                    || d.getOwner().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        //update recyclerview
        radtr.updateList(temp);
    }


    void Search(String query){
        url = "https://api.github.com/search/repositories?q=" + query;
        GETRepos repos = new GETRepos();
        // update the main content by replacing fragments
        repoList.clear();
        try {
            Object result = repos.execute(url,"1").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        List<Repo> temp = new ArrayList();

        for(Repo d: this.repoList){
            temp.add(d);
        }
        //update recyclerview
        radtr.updateList(temp);
    }
}
