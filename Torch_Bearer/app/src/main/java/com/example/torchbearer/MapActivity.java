package com.example.torchbearer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.text.Layout;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.torchbearer.achievement.Achievement;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;

import com.example.torchbearer.Notifications.SendNotification;
import com.example.torchbearer.achievement.AchievementMap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.example.torchbearer.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    GoogleMap map;
    ToggleButton toggle;

    private DatabaseReference reference;
    private LocationManager manager;

    private FirebaseAuth firebaseAuth;
    private String userId;
    private String userToken;
    private String achievement;

    private User user;
    private final int MIN_TIME = 500;
    private final int MIN_DISTANCE = 1;
    private boolean isOn;
    private RealtimeDatabase mDatabase;

    private Marker myMaker;
    private List<Marker> markers;
    private List<MarkerOptions> markerOptions;
    private List<Boolean> markersVisible;

    //squar

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    static final LatLng V_ICE_SCREAMS = new LatLng(47.99728191304702, -122.1898995151709677);
    private PathMapView mMapView;
    private List<LatLng> clicked;


    List<List<LatLng>> transparentLines;

    private int numPaths;
    private Map<String, Achievement> userAchievements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isOn = true;
                } else {
                    isOn = false;
                    savePath();

                    for (Marker marker : markers) {
                        marker.remove();
                    }
                    mMapView.resetLine();
                    setAchievement();
                }
            }
        });
        //Squar

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        initializeDb();
        initializeCurrentUser(userId);
        reference = mDatabase.getChildReference(userId);
        getUserAchievements();
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
                clicked = new ArrayList<>();
                mDatabase.getChildReference("markers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isEmpty = true;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (ds.getValue(Marker.class) != null) {
                                isEmpty = false;
                            }
                        }
                        if (isEmpty)
                            createMarkers();
//                        if (!snapshot.exists()) {
//                            System.out.println("doing exist");
//                            createMarkers();
//                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                initialMarkersAsy(map);
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        if (!marker.equals(myMaker)) {
                            marker.setIcon(BitMapFromVector(getApplicationContext(), R.drawable.ic_campfire));
                            if (!clicked.contains(marker.getPosition())) {
                                clicked.add(marker.getPosition());
                                saveMarkerState(clicked);
                            }
                            Intent i = new Intent(MapActivity.this, ViewPhotosAtLocationActivity.class);
                            i.putExtra("location", marker.getTitle());
                            startActivity(i);
                        }
                        return false;
                    }
                });
                //initial
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(V_ICE_SCREAMS, 15f));
                initTransparentLine();
            }
        });
        getLocationUpdate();
        readChanges();
        updateToken();
    }

    public void showProfile(View view) {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }

    private void updateToken() {
        // Generate and updates the token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(MapActivity.this, "Something is wrong!", Toast.LENGTH_SHORT).show();
                } else {

                    userToken = task.getResult();

                    Log.e("CLIENT_REGISTRATION_TOKEN", userToken);
                    Toast.makeText(MapActivity.this, "CLIENT_REGISTRATION_TOKEN Existed", Toast.LENGTH_SHORT).show();
                    mDatabase.onUpdateToken(userId, userToken);
                }
            }
        });
    }


    private void sendNotification(String achievement) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String receiverToken = user.getToken();
                SendNotification notification = new SendNotification();
                notification.sendMessageToDevice(receiverToken, achievement, MapActivity.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private void createMarkers() {
        System.out.println("create markers");
        this.markers = new ArrayList<>();
        LatLng spaceNeedle = new LatLng(47.620506, -122.349277);
        LatLng amazonSpheres = new LatLng(47.615556, -122.339444);

        Marker mark1 = map.addMarker(new MarkerOptions().position(spaceNeedle).title("Space Needles")
                .icon(BitMapFromVector(getApplicationContext(), R.drawable.ic_wood_logs)));
        mark1.setVisible(false);
        markers.add(mark1);

        Marker mark2 = map.addMarker(new MarkerOptions().position(amazonSpheres).title("Amazon Spheres")
                .icon(BitMapFromVector(getApplicationContext(), R.drawable.ic_wood_logs)));
        mark2.setVisible(false);
        markers.add(mark2);

        saveMarkers(markers);
    }

    private void initializeDb() {
        mDatabase = new RealtimeDatabase(MapActivity.this);
    }

    private void initializeCurrentUser(String userId) {

        mDatabase.getChildReference(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void initTransparentLine() {
        transparentLines = new ArrayList<>();
        mDatabase.showTransparentLine(userId, transparentLines, new TransparentLineCallBack() {
            @Override
            public void onCallBack(List<List<LatLng>> paths) {
                mMapView.setPathPoints(paths);
            }
        });
    }

    private void initialMarkersAsy(GoogleMap map) {
        markerOptions = new ArrayList<>();
        mDatabase.showMarkersAsy(userId, map);
        System.out.println("initial markers Asy" + markers);
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
                        }
                    } catch (Exception e) {
                        Toast.makeText(MapActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserAchievements() {
        reference.child("achievedMap").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                    userAchievements = new HashMap<>();
                else
                    userAchievements = snapshot.getValue(Map.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null) {
            saveLocation(location);
            if (isOn) {
                updateTrack(location);
                checkMarkersDistance(50);
                System.out.println("location change");

            }
            map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        } else {
            Toast.makeText(this, "No location", Toast.LENGTH_SHORT).show();
        }

    }

    private void savePath() {
        reference.child("paths").setValue(mMapView.getmPathPoints());
        reference.child("numOfPath").setValue(numPaths);
    }

    private void saveMarkers(List<Marker> markers) {
        reference.child("markers").setValue(markers);
    }

    private void saveLocation(Location location) {
        reference.child("location").setValue(location);
    }

    private void saveMarkerState(List<LatLng> clicked) {
        reference.child("clicked").setValue(clicked);
    }

    private void updateTrack(Location location) {
        mMapView.addPoint(new LatLng(location.getLatitude(), location.getLongitude()));
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

    }

    private BitmapDescriptor BitMapFromVector(Context applicationContext, int ic_torch) {
        Drawable vectorDrawable = ContextCompat.getDrawable(applicationContext, ic_torch);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void checkMarkersDistance(double radius) {
        for (Marker marker : this.markers) {
            LatLng markerLatLng = marker.getPosition();
            float result[] = new float[1];
            Location.distanceBetween(myMaker.getPosition().latitude, myMaker.getPosition().longitude, markerLatLng.latitude, markerLatLng.longitude, result);
            if (result[0] < radius) {
                marker.setVisible(true);
                saveMarkers(markers);
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    //    private void displayPastMarker(List<Marker> markers) {
//        for (Marker marker : markers) {
//            if (marker.id
//        }
//    }


//    public void openPostPhotoActivity(View view) {
//        startActivity(new Intent(MapActivity.this, PostPhotoActivity.class));
//    }
//
//    public void openViewPhotoActivity(View view) {
//        startActivity(new Intent(MapActivity.this, ViewPhotosAtLocationActivity.class));
//    }

    private void setAchievement() {
        AchievementMap achievementMap = new AchievementMap();
        if (user.getPaths().size() == 1) {
            userAchievements.put("First Path", new Achievement("First Path", "First time start tracking path."));
            reference.child("achievedMap").setValue(userAchievements);
            if (achievementMap.isAchievementValid("First Path", user.getAchievedMap())) {
                sendNotification("First Path");
            }
        }
        if (user.getTotalLength() >= 10.0) {
            userAchievements.put("10 Feet", new Achievement("10 Feet", "Tracked 10-foot long path."));
            reference.child("achievedMap").setValue(userAchievements);
            if (achievementMap.isAchievementValid("10 Feet", user.getAchievedMap())) {
                sendNotification("10 Feet");
            }
        }
        if (user.getLogCount() == 1) {
            userAchievements.put("First Log", new Achievement("First Log", "Reached the first wood log."));
            reference.child("achievedMap").setValue(userAchievements);
            if (achievementMap.isAchievementValid("First Log", user.getAchievedMap())) {
                sendNotification("First Log");
            }
        }
    }

}