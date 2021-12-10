package com.example.torchbearer;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public interface TransparentLineCallBack {
    void onCallBack(List<List<LatLng>> paths);
}
