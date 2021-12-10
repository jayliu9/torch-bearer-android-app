package com.example.torchbearer;


public class User {
    private String userId;
    private String username;
    private String email;
    private String phoneNum;
    private String token;

    public User(String userId) {
        this.username = userId;
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User(String username, String email, String phoneNum) {
        this.username = username;
        this.email = email;
        this.phoneNum = phoneNum;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhoneNum() {
        return this.phoneNum;
    }


    public String getToken() {
        return token;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }


    public void setToken(String token) {
        this.token = token;
    }
}
