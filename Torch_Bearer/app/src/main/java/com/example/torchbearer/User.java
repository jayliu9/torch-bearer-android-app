package com.example.torchbearer;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {

    private String username;
    private Map<String, List<LatLngForUser>> paths;
    private MyLocation location;
    private String token;
    private int numOfPath;

    public User() {
        this.paths = new HashMap<>();
        this.numOfPath = 0;
    }

    public User(String username) {
        this.username = username;
        this.paths = new HashMap<>();
        this.numOfPath = 0;
    }

    public User(String username, Map<String, List<LatLngForUser>> paths) {
        this.username = username;
        this.paths = paths;
        this.numOfPath = 0;
    }

    public User(String username, Map<String, List<LatLngForUser>> paths, int numOfPath) {
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

    public Map<String, List<LatLngForUser>> getPaths() {
        return paths;
    }

    public void setPaths(Map<String, List<LatLngForUser>> paths) {
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
}
