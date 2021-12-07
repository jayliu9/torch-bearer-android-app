package com.example.torchbearer;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String username;
    private List<PolylineOptions> paths;
    private MyLocation location;
    private String token;
    private int numOfPath;

    public User() {
        this.paths = new ArrayList<>();
        this.numOfPath = 0;
    }

    public User(String username) {
        this.username = username;
        this.paths = new ArrayList<>();
        this.numOfPath = 0;
    }

    public User(String username, List<PolylineOptions> paths) {
        this.username = username;
        this.paths = paths;
        this.numOfPath = 0;
    }

    public User(String username, List<PolylineOptions> paths, int numOfPath) {
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
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<PolylineOptions> getPaths() {
        return paths;
    }

    public void setPaths(List<PolylineOptions> paths) {
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
