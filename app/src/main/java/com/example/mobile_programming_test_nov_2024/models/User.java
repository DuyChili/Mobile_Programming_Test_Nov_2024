package com.example.mobile_programming_test_nov_2024.models;

public class User {
    private String login;
    private String avatar_url;
    private String html_url;
    private String location;
    private int followers;
    private int following;
    private int id;

    // Constructor, getter and setter methods

    public User(String login, String avatarUrl, String htmlUrl, String location, int followers, int following , int id) {
        this.login = login;
        this.avatar_url = avatarUrl;
        this.html_url = htmlUrl;
        this.location = location;
        this.followers = followers;
        this.following = following;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public String getAvatarUrl() {
        return avatar_url;
    }

    public String getHtmlUrl() {
        return html_url;
    }

    public String getLocation() {
        return location;
    }

    public int getFollowers() {
        return followers;
    }

    public int getFollowing() {
        return following;
    }
}


