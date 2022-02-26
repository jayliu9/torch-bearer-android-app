package com.example.torchbearer;


import com.example.torchbearer.achievement.Achievement;
import com.example.torchbearer.achievement.AchievementMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {

    private String username;
    private List<List<LatLngForUser>> paths;
    private MyLocation location;
    private String token;
    private int numOfPath;
    private double totalLength;
    private int logCount;
    private String userId;
    private String email;
    private String phoneNum;
    private List<MarkerOptions> markerOptions;
    private List<LatLngForUser> clicked;
    private String ProfileImageUrl;
    private Map<String, Achievement> achievedMap;

    public User() {
        this.paths = new ArrayList<>();
        this.numOfPath = 0;
        this.markerOptions = new ArrayList<>();
        this.clicked = new ArrayList<>();
        this.achievedMap = new HashMap<>();
    }

    public User(String username) {
        this.username = username;
        this.paths = new ArrayList<>();
        this.numOfPath = 0;
        this.markerOptions = new ArrayList<>();
        this.clicked = new ArrayList<>();
        this.achievedMap = new HashMap<>();
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.paths = new ArrayList<>();
        this.numOfPath = 0;
        this.markerOptions = new ArrayList<>();
        this.clicked = new ArrayList<>();
        this.achievedMap = new HashMap<>();
    }

    public User(String username, String email, String phoneNum) {
        this.username = username;
        this.email = email;
        this.phoneNum = phoneNum;
        this.paths = new ArrayList<>();
        this.numOfPath = 0;
        this.markerOptions = new ArrayList<>();
        this.clicked = new ArrayList<>();
        this.achievedMap = new HashMap<>();
    }

    public User(String username, List<List<LatLngForUser>> paths) {
        this.username = username;
        this.paths = paths;
        this.numOfPath = 0;
        this.markerOptions = new ArrayList<>();
        this.clicked = new ArrayList<>();
        this.achievedMap = new HashMap<>();
    }

    public User(String username, List<List<LatLngForUser>> paths, int numOfPath) {
        this.username = username;
        this.paths = paths;
        this.numOfPath = numOfPath;
        this.markerOptions = new ArrayList<>();
        this.clicked = new ArrayList<>();
        this.achievedMap = new HashMap<>();
    }


    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", paths=" + paths +
                ", location=" + location +
                ", token='" + token + '\'' +
                ", numOfPath=" + numOfPath +
                ", markers=" + markerOptions +
                ", clicked=" + clicked +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<List<LatLngForUser>> getPaths() {
        return paths;
    }

    public void setPaths(List<List<LatLngForUser>> paths) {
        this.paths = paths;
    }

    public MyLocation getLocation() {
        return location;
    }

    public void setLocation(MyLocation location) {
        this.location = location;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getNumOfPath() {
        return numOfPath;
    }

    public void setNumOfPath(int numOfPath) {
        this.numOfPath = numOfPath;
    }

    public double getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(double totalLength) {
        this.totalLength = totalLength;
    }

    public int getLogCount() {
        return logCount;
    }

    public void setLogCount(int logCount) {
        this.logCount = logCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public List<MarkerOptions> getMarkerOptions() {
        return markerOptions;
    }

    public void setMarkerOptions(List<MarkerOptions> markerOptions) {
        this.markerOptions = markerOptions;
    }

    public String getProfileImageUrl() {
        return ProfileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        ProfileImageUrl = profileImageUrl;
    }

    public List<LatLngForUser> getClicked() {
        return clicked;
    }

    public void setClicked(List<LatLngForUser> clicked) {
        this.clicked = clicked;
    }

    public Map<String, Achievement> getAchievedMap() {
        return achievedMap;
    }

    public void setAchievedMap(Map<String, Achievement> achievedMap) {
        this.achievedMap = achievedMap;
    }
}
