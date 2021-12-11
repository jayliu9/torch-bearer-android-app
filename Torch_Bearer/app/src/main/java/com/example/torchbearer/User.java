package com.example.torchbearer;


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

    public User() {
        this.paths = new ArrayList<>();
        this.numOfPath = 0;
    }

    public User(String username) {
        this.username = username;
        this.paths = new ArrayList<>();
        this.numOfPath = 0;
    }

    public User(String username, List<List<LatLngForUser>> paths) {
        this.username = username;
        this.paths = paths;
        this.numOfPath = 0;
    }

    public User(String username, List<List<LatLngForUser>> paths, int numOfPath) {
        this.username = username;
        this.paths = paths;
        this.numOfPath = numOfPath;
    }


    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", paths=" + paths +
                ", location=" + location +
                ", token='" + token + '\'' +
                ", numOfPath=" + numOfPath +
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

    public void pathIncrease() {
        this.numOfPath++;
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
}
