package com.example.torchbearer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    
    GoogleMap map;
    ToggleButton toggle;

    private DatabaseReference reference;
    private LocationManager manager;
    private Polyline gpsTrack;

    private List<PolylineOptions> pathOptions;
    private User user;
    private String username;
    private final int MIN_TIME = 500;
    private final int MIN_DISTANCE = 1;
    private boolean isOn;
    private RuntimeDatabase mDatabase;

    private Marker myMaker;
    //Camera change
    private HideOverlayView hideView;
    private List<Marker> visibleMarkers = new ArrayList<>();

    //circle
    Circle circle;

    //squar

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    static final LatLng V_ICE_SCREAMS = new LatLng(47.99728191304702, -122.1898995151709677);
    private PathMapView mMapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isOn = true;
                } else {
                    isOn = false;
                    savePath();
                    resetPolyline();
                }
            }
        });
//
//        initializeDb();
//        //user = new User("User-101");
//        //reference = mDatabase.getChildReference("User-101");
//        initializeCurrentUser("User-101");
//        reference = mDatabase.getChildReference("User-101");
//        username = "User-101";
//        //reference = FirebaseDatabase.getInstance().getReference().child("User-101");
//        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        //FirebaseDatabase.getInstance().getReference().setValue("This is Torch Bearer");
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//        getLocationUpdate();
//
//        readChanges();

        //Squar

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        initializeDb();
        initializeCurrentUser("User-101");
        reference = mDatabase.getChildReference("User-101");
        username = "User-101";
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mMapView = (PathMapView) findViewById(R.id.pathView);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setAllGesturesEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                LatLng seattle = new LatLng(47.99728191304702, -122.1898995151709677);
                myMaker = map.addMarker(new MarkerOptions().position(seattle).title("Marker")
                        .icon(BitMapFromVector(getApplicationContext(), R.drawable.ic_torch)));
                final ArrayList<LatLng> pathPoints = new ArrayList<>();
                pathPoints.add(new LatLng(47.99728191304702, -122.1898995151709677));
                pathPoints.add(new LatLng(47.99729343143402, -122.1915964365223883));
                pathPoints.add(new LatLng(47.997462367423275, -122.1892870924275978));
                pathPoints.add(new LatLng(47.99732798657649, -122.1888979488094147));
                pathPoints.add(new LatLng(47.99848364819607, -122.1887576019291894));
                pathPoints.add(new LatLng(47.99842989717891, -122.1903460734198053));
                pathPoints.add(new LatLng(47.99632203666474, -122.1900781384562662));
                pathPoints.add(new LatLng(47.99728959196756, -122.1898484799275026));

                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.RED);
                polylineOptions.width(4);
                gpsTrack = map.addPolyline(polylineOptions);
                mMapView.setPathPoints(pathPoints);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(V_ICE_SCREAMS,15));

            }
        });

        getLocationUpdate();
        readChanges();

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void resetPolyline() {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(4);
        gpsTrack = map.addPolyline(polylineOptions);
    }

    private void savePath() {
//        PolylineOptions newLineOptions = new PolylineOptions();
//        newLineOptions.color(Color.RED);
//        newLineOptions.width(4);
//        LatLng[] points = gpsTrack.getPoints().toArray(new LatLng[0]);
//        newLineOptions.add(points);
//        pathOptions.add(newLineOptions);
        int currNum = user.getNumOfPath();
        reference.child("paths").child("Path" + currNum).setValue(gpsTrack.getPoints());
        reference.child("numOfPath").setValue(currNum + 1);
    }

    private void initializeDb() {
        mDatabase = new RuntimeDatabase(MainActivity.this);
    }


    private void initializeCurrentUser(String usernameInput) {
        Bundle extras = getIntent().getExtras();
        String finalUsername = usernameInput;
        mDatabase.getChildReference(usernameInput).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User userInDB = dataSnapshot.getValue(User.class);
                if (userInDB == null) {
                    mDatabase.createUser(finalUsername);
                    user = new User(finalUsername);
                } else {
                    user = userInDB;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

//        if (true) {//extras != null) {
//            //username = extras.getString("username");
//            username = user.getUsername();
//            Log.i(TAG, "Current username is '" + username + "'.");
//        } else {
//            Log.e(TAG, "No username passed to activity!!!");
//        }
//        mDatabase.createUser(username);
    }


    private void readChanges() {
        reference.child("location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        MyLocation location = snapshot.getValue(MyLocation.class);
                        if (location != null) {
                            myMaker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                            mMapView.addPoint(new LatLng(location.getLatitude(), location.getLongitude()));
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getLocationUpdate() {
        if (manager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                } else {
                    Toast.makeText(this, "No Provider Enabled", Toast.LENGTH_SHORT).show();
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationUpdate();
            } else {
                Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        LatLng seattle = new LatLng(47.99728191304702, -122.1898995151709677);
        myMaker = map.addMarker(new MarkerOptions().position(seattle).title("Marker")
                .icon(BitMapFromVector(getApplicationContext(), R.drawable.ic_torch)));
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(seattle, 15.0f));

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(4);
        gpsTrack = map.addPolyline(polylineOptions);

        initializePastPath();

        map.moveCamera(CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(new LatLng(47, -122), 15)));
    }

    private void initializePastPath() {
        pathOptions = new ArrayList<>();
        mDatabase.showPastPath(username, pathOptions, new PolylineOptionsCallBack() {
            @Override
            public void onCallBack(List<PolylineOptions> paths) {
                for (PolylineOptions pathOption : pathOptions) {
                    pathOption.color(Color.RED);
                    pathOption.width(4);
                    Polyline path = map.addPolyline(pathOption);
                }
            }
        });
    }


    private BitmapDescriptor BitMapFromVector(Context applicationContext, int ic_torch) {
        Drawable vectorDrawable = ContextCompat.getDrawable(applicationContext, ic_torch);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null) {
            saveLocation(location);
            if (isOn)
                updateTrack(location);
            map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        } else {
            Toast.makeText(this, "No location", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveLocation(Location location) {
        reference.child("location").setValue(location);
    }

    private void updateTrack(Location location) {
        List<LatLng> points = gpsTrack.getPoints();
        points.add(new LatLng(location.getLatitude(), location.getLongitude()));
        gpsTrack.setPoints(points);
        mMapView.setPathPoints(points);

//        circle = drawCircle(new LatLng(location.getLatitude(), location.getLongitude()));

    }
}