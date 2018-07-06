package com.example.lpoon2.assignment30;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

// firebase tutorial https://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/
public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private String user_name = "";
    private String url = "https://api.github.com/users/lpoon2?access_token=f1ebbe990f6f59ddd489df0206aa487be157e48c";
    private String TOKEN = "?access_token=f1ebbe990f6f59ddd489df0206aa487be157e48c";
    private String image_url = "";
    private String name ="";
    private String follower_url = "";
    private String following_url = "";
    private String repos_url = "";
    private int followers = 0;
    private int following = 0;
    private int repos = 0;
    private String created = "";
    private Boolean opened = false;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    /**
     * The main handler for drawer navigation
     * Users will be redirected to different fragmenrs as they click on corresponding positions
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {

        GETProfile profile = new GETProfile();
        // update the main content by replacing fragments

        if(!opened)
        try {
            Object result = profile.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        // switch to another fragment by tabbing on the corresponding tab
        switch(position) {

            case 0:
                fragment = Profile.newInstance(name, user_name, image_url,repos,followers, following,created);
                break;
            case 1:
                fragment = Repositories.newInstance(repos_url+TOKEN);
                break;
            case 2:
                fragment = Following.newInstance(following_url+TOKEN);
                break;
            case 3:
                fragment = Follower.newInstance(follower_url+TOKEN);
                break;
            case 4:
                fragment = Stats.newInstance("");
        }

        //begin fragment
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    /**
     * This method is supposed to change the title on the page, depending on which fragment you are currently in
     * @param number
     */
    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
            case 5:
                mTitle = "Stats";

        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * on-click handler for menu items
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), this.name,
                    Toast.LENGTH_SHORT).show();
            return true;
        }
        // signout action
        if(id == R.id.signout){
            FirebaseAuth.getInstance().signOut();
        }
        // store data to firebase
        if(id == R.id.store){

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("users");

            //String full_name, String name, String url, String img, String num_repos, String followers, String following, String created_date
            FollowerObj f_obj = new FollowerObj(name, user_name, url, image_url, Integer.toString(repos), Integer.toString(followers), Integer.toString(following), created);
            myRef.push().setValue(f_obj);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * An on-click handler for the profile page
     * Users will be navigated to different pages when they tab on corresponding positions
     * @param v
     */
    public void toNextFragment(View v){
        char description = v.getContentDescription().charAt(0);
        switch (description){
            case 'r':
                onNavigationDrawerItemSelected(1);
                break;
            case 'e':
                onNavigationDrawerItemSelected(3);
                break;
            case'i':
                onNavigationDrawerItemSelected(2);
                break;

        }
    }

    /**
     * A private class that extends AsyncTask class. This class is set up for
     * making http request to Github api
     */
    private class GETProfile extends AsyncTask<String, Void, String> {

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
                link = new URL(url);

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
                image_url = json.getString("avatar_url");
                url = json.getString("url");
                user_name = json.getString("login");
                name = json.getString("name");
                follower_url = json.getString("followers_url");
                following_url = "https://api.github.com/users/lpoon2/following";
                //following_url = json.getString("following_url");
                repos_url = json.getString("repos_url");
                repos = json.getInt("public_repos");
                followers = json.getInt("followers");
                following = json.getInt("following");
                created = json.getString("created_at");

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
