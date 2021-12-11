package com.example.torchbearer;


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
    private List<MarkerOptions> markerOptions;

    public User() {
        this.paths = new ArrayList<>();
        this.numOfPath = 0;
        this.markerOptions = new ArrayList<>();
    }

    public User(String username) {
        this.username = username;
        this.paths = new ArrayList<>();
        this.numOfPath = 0;
        this.markerOptions = new ArrayList<>();
    }

    public User(String username, List<List<LatLngForUser>> paths) {
        this.username = username;
        this.paths = paths;
        this.numOfPath = 0;
        this.markerOptions = new ArrayList<>();
    }

    public User(String username, List<List<LatLngForUser>> paths, int numOfPath) {
        this.username = username;
        this.paths = paths;
        this.numOfPath = numOfPath;
        this.markerOptions = new ArrayList<>();
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

    public List<MarkerOptions> getMarkers() {
        return markerOptions;
    }

    public void setMarkers(List<MarkerOptions> markers) {
        this.markerOptions = markers;
    }
}
