package com.example.torchbearer;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface ClickedStateCallBack {
    void onCallBack(List<LatLng> clicked);
}