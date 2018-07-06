package com.example.lpoon2.assignment30;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by lpoon2 on 10/20/2017.
 */

public class ReposAdapter extends RecyclerView.Adapter<ReposAdapter.MyViewHolder>{

    private List<Repo> reposList;
    private String name = "";
    private String TOKEN = "?access_token=f1ebbe990f6f59ddd489df0206aa487be157e48c";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, owner, description;
        public CheckBox box;
        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.title);
            owner = (TextView) view.findViewById(R.id.genre);
            description = (TextView) view.findViewById(R.id.year);
            box = (CheckBox) view.findViewById(R.id.checkBox);
        }
    }

    public ReposAdapter(List<Repo> moviesList) {
        this.reposList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.repo_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Repo repo = reposList.get(position);
        holder.name.setText(repo.getName());
        holder.owner.setText(repo.getOwner()+ "-" + repo.getDescription());
        this.name=repo.getOwner();

        // set on click listener for checkbox
        holder.box.setTag(reposList.get(position));
        holder.box.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                Repo r = (Repo) cb.getTag();
                StarRepo repos = new StarRepo();
                // update the main content by replacing fragments
                try {
                    Object result = repos.execute(name + "/" + r.getName()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Toast.makeText(
                        v.getContext(),
                        "Repo starred :"  + r.getName() + "is check"
                                + cb.isChecked(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reposList.size();
    }


    /**
     * function for updating repo list
     * @param list
     */
    public void updateList(List<Repo> list){
        this.reposList = list;
        notifyDataSetChanged();
    }

    /**
     * A private class that extends AsyncTask class. This class is set up for
     * making http request to Github api
     */
    private class StarRepo extends AsyncTask<String, Void, String> {

        /**
         * Sending http get in background. Because Andriod doesn't allow sending request in the main thread
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String[] params) {

            URL link, check_link;
            HttpURLConnection urlConnection = null;
            StringBuilder sb = new StringBuilder();
            String star_url = "https://api.github.com/user/starred/"+ params[0]+TOKEN ;
            //make connection
            try {

                check_link = new URL(star_url);
                urlConnection = (HttpURLConnection) check_link.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                int code = urlConnection.getResponseCode();

                if (code == 204){
                    unstar(star_url);
                }
                else{
                    star(star_url);
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

        /**
         * Starring a repository by checking the checkbox
         * @param star_url
         */
        public void star(String star_url){

            try {
                URL link = new URL(star_url);

                HttpURLConnection urlConnection = (HttpURLConnection) link.openConnection();

                // initializing PUT method - setting content length to 0
                urlConnection.setRequestProperty("Content-Length", "0");
                urlConnection.setRequestMethod("PUT");

                OutputStreamWriter out = new OutputStreamWriter(
                        urlConnection.getOutputStream());
                urlConnection.connect();
                int code = urlConnection.getResponseCode();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

        /**
         * Unstar the repository if it has been starred already
         * @param unstar_url
         */
        public void unstar(String unstar_url){

            try {
                URL link = new URL(unstar_url);
                HttpURLConnection urlConnection = (HttpURLConnection) link.openConnection();
                urlConnection.setRequestMethod("DELETE");
                urlConnection.connect();
                int code = urlConnection.getResponseCode();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }


    }

}
