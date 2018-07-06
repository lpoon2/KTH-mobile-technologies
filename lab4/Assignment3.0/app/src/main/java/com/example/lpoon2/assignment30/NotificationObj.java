package com.example.lpoon2.assignment30;

/**
 * Created by lpoon2 on 11/3/2017.
 */

public class NotificationObj {
    private String title, date, reson;
    private Boolean unread;

    public NotificationObj(String title, String date, String reson, Boolean unread) {
        this.title = title;
        this.date = date;
        this.reson = reson;
        this.unread = unread;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReson() {
        return reson;
    }

    public void setReson(String reson) {
        this.reson = reson;
    }

    public Boolean getUnread() {
        return unread;
    }

    public void setUnread(Boolean unread) {
        this.unread = unread;
    }
}
