package com.example.lpoon2.assignment30;

/**
 * Created by lpoon2 on 10/20/2017.
 */

public class Repo {
    private String name, owner, description,url;

    public Repo(String name, String owner, String description, String url) {
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
