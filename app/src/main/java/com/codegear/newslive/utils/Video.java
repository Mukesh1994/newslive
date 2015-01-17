package com.codegear.newslive.utils;

public class Video {
    private String title, stream, user, length;

    public Video() {
    }

    public Video(String name, String stream, String user, String length) {
        this.title = name;
        this.stream = stream;
        this.user = user;
        this.length = length;
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

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }


}
