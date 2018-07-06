package com.example.lpoon2.assignment30;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lpoon2 on 10/23/2017.
 */

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.MyViewHolder>  {

    // arrays for storing images and followerOBj
    private List<FollowerObj> followerssList;
    private List<Drawable> drawableList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, url;
        public ImageView img;
        public EditText search;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            url  = (TextView) view.findViewById(R.id.url);
            img = (ImageView) view.findViewById(R.id.imageView);
            search = (EditText) view.findViewById(R.id.search);
        }
    }

    public FollowerAdapter(List<FollowerObj> moviesList, List<Drawable> drawableList) {
        this.followerssList = moviesList;
        this.drawableList = drawableList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.follower_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FollowerObj repo = followerssList.get(position);
        holder.name.setText(repo.getFull_name());
        holder.url.setText(repo.getName());
        holder.img.setImageDrawable(repo.getImage());

    }

    @Override
    public int getItemCount() {
        return followerssList.size();
    }


    /**
     * function for updating repo list
     * @param list
     */
    public void updateList(List<FollowerObj> list){
        this.followerssList = list;
        notifyDataSetChanged();
    }



}
