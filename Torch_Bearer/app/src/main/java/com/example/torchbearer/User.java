package com.example.torchbearer;

public class User {
    private String username;
    private String email;
    private String phoneNum;

    public User() {
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
}
