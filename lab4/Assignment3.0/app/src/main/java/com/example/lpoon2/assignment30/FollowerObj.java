package com.example.lpoon2.assignment30;

/**
 * Created by lpoon2 on 10/23/2017.
 */

import android.graphics.drawable.Drawable;

/**
 * FollowerONj stores user object which contains name, image, num_repos etc.
 * User objects are shown in following and follower fragments
 */
public class FollowerObj {
    private String full_name;
    private String name;
    private String url;
    private String img;
    private String num_repos;
    private String followers;
    private String following;
    private String created_date;
    private Drawable image;


    //constructor
    public FollowerObj(String full_name, String name, String url, String img, String num_repos, String followers, String following, String created_date) {
        this.full_name = full_name;
        this.name = name;
        this.url = url;
        this.img = img;
        this.num_repos = num_repos;
        this.followers = followers;
        this.following = following;
        this.created_date = created_date;
    }


    public String getNum_repos() {
        return num_repos;
    }

    public void setNum_repos(String num_repos) {
        this.num_repos = num_repos;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }
    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }


}
