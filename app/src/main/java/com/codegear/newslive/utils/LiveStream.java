package com.codegear.newslive.utils;

import java.util.Date;

public class LiveStream {
    private String title, stream, user;
    private Date started_at;

    public LiveStream() {
    }

    public LiveStream(String name, String stream, String user, Date started_at) {
        this.title = name;
        this.stream = stream;
        this.user = user;
        this.started_at = started_at;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDate() {
        return started_at;
    }

    public void setDate(Date date) {
        this.started_at = date;
    }


}
