package com.example.lpoon2.assignment30;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String NAME = "param1";
    private static final String FULL_NAME = "";
    private static final String IMG_URL= "img";
    private static final String REPOS = "repos";
    private static final String FOLLOWER= "follower";
    private static final String FOLLOWING= "following";
    private static final String CREATED= "created";
    private static final String TOKEN = "?access_token=f1ebbe990f6f59ddd489df0206aa487be157e48c";

    //parameters for restoring values from the initialization stage
    private String name = "";
    private String user_name = "";
    private String url = "https://api.github.com/users/lpoon2";
    private String image_url = "";
    private String follower_url = "";
    private String following_url = "";
    private int followers = 0;
    private int following = 0;
    private int repos = 0;
    private String created = "";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user_name the url to the user profile page
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String name, String user_name, String img, int repos_num, int followers_num, int following_num , String created_date){
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(NAME, user_name);
        args.putString(IMG_URL, img);
        args.putString(FULL_NAME, name);
        args.putString(REPOS, Integer.toString(repos_num));
        args.putString(FOLLOWER, Integer.toString(followers_num));
        args.putString(FOLLOWING, Integer.toString(following_num));
        args.putString(CREATED, created_date);
        fragment.setArguments(args);
        return fragment;
    }

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user_name = getArguments().getString(NAME);
            name = getArguments().getString(FULL_NAME);
            image_url = getArguments().getString(IMG_URL);
            followers = Integer.parseInt(getArguments().getString(FOLLOWER));
            following = Integer.parseInt(getArguments().getString(FOLLOWING));
            repos = Integer.parseInt(getArguments().getString(REPOS));
            created = getArguments().getString(CREATED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView full_name = (TextView)rootView.findViewById(R.id.name);
        TextView name = (TextView)rootView.findViewById(R.id.user_name);
        TextView repo = (TextView)rootView.findViewById(R.id.repos);
        TextView follower = (TextView)rootView.findViewById(R.id.followers);
        TextView followingView = (TextView)rootView.findViewById(R.id.following);
        ImageView img = (ImageView)rootView.findViewById(R.id.avarter);
        TextView date = (TextView)rootView.findViewById(R.id.created_date);

        // set on click listener on text views
        repo.setOnClickListener(new onClickHandler());
        follower.setOnClickListener(new onClickHandler());
        followingView.setOnClickListener(new onClickHandler());

        GETImage image = new GETImage();
        Drawable drawable_img = null;

        // using Async Task to get drawable from the url
        try {
            drawable_img = image.execute(image_url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //assigning values to views, also make text responsive to screen size
        img.setImageDrawable(drawable_img);

        full_name.setText(this.name);
        full_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.textsize));

        name.setText(user_name);
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.textsize));

        date.setText("created at :" + created);
        date.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.textsize));

        repo.setText(Integer.toString(repos) + " repositories");
        repo.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.textsize));

        follower.setText(Integer.toString(followers) + " followers");
        follower.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.textsize));

        followingView.setText(Integer.toString(following) + " following");
        followingView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.textsize));

        return rootView;

    }


    /**
     * A private class that extends AsyncTask class. This class is set up for
     * making http request to Github api
     */
    private class GETImage extends AsyncTask<String, Void, Drawable> {

        /**
         * Sending http get in background. Because Andriod doesn't allow sending request in the main thread
         * @param params
         * @return
         */
        @Override
        protected Drawable doInBackground(String[] params) {
            Drawable d = null;
            String img_url  = params[0];
            try {
                InputStream is = (InputStream) new URL(img_url).getContent();
                d = Drawable.createFromStream(is, "src name");
                return d;
            } catch (Exception e) {
                return null;
            }

        }

    }

    class onClickHandler implements View.OnClickListener{
        @Override
        public void onClick(View view){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = null;
            char description = view.getContentDescription().charAt(0);
            switch (description){
                case 'r':
                    fragment = new Repositories().newInstance("https://api.github.com/users/"+user_name+"/repos"+TOKEN);
                    break;
                case 'e':
                    fragment = new Follower().newInstance("https://api.github.com/users/"+user_name+"/followers"+TOKEN);
                    break;
                case'i':
                    fragment = new Following().newInstance("https://api.github.com/users/"+user_name+"/following"+TOKEN);
                    break;
            }

            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

}
