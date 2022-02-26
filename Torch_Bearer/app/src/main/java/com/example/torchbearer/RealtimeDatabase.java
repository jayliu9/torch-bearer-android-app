package com.example.torchbearer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.torchbearer.achievement.Achievement;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RealtimeDatabase {
    public static final String USERS_TABLE_NAME = "Users";
    private static final String TAG = RealtimeDatabase.class.getSimpleName();

    private DatabaseReference database;
    private Context ctx;
    private List<LatLng> clicked;

    public RealtimeDatabase(Context context) {
        this.database = FirebaseDatabase.getInstance().getReference();
        this.ctx = context;
        this.clicked = new ArrayList<>();
    }

    public void createUser(String userId, User user) {
        getChildReference(userId)
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ctx, "Successfully added to database.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ctx, "Failed to be added to database.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public void onUpdateUsername(String userId, String username) {
        getChildReference(userId)
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        User user = currentData.getValue(User.class);
                        if (user == null) {
                            return Transaction.success(currentData);
                        }
                        user.setUsername(username);
                        currentData.setValue(user);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                        Log.d(TAG, "postTransactionForSender:onComplete:" + error);
                    }
                });
    }

    public void onUpdateUserPhoneNum(String userId, String phoneNum) {
        getChildReference(userId)
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        User user = currentData.getValue(User.class);
                        if (user == null) {
                            return Transaction.success(currentData);
                        }
                        user.setPhoneNum(phoneNum);
                        currentData.setValue(user);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                        Log.d(TAG, "postTransactionForSender:onComplete:" + error);
                    }
                });
    }

    public void onUpdateToken(String userId, String userToken) {
        getChildReference(userId)
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        User user = currentData.getValue(User.class);
                        if (user == null) {
                            return Transaction.success(currentData);
                        }
                        user.setToken(userToken);
                        currentData.setValue(user);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                        Log.d(TAG, "postTransactionForSender:onComplete:" + error);
                    }
                });
    }


    public DatabaseReference getChildReference(String child) {
        return this.database.child(USERS_TABLE_NAME).child(child);
    }

    public void showTransparentLine(String userId, List<List<LatLng>> paths, TransparentLineCallBack myCallBack) {
        getChildReference(userId).child("paths").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                paths.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    List<LatLng> line = new ArrayList<>();
                    for (DataSnapshot dsChild : ds.getChildren()) {
                        double latitude = dsChild.child("latitude").getValue(Double.class);
                        double longitude = dsChild.child("longitude").getValue(Double.class);
                        line.add(new LatLng(latitude, longitude));
                    }
                    paths.add(line);
                }

                myCallBack.onCallBack(paths);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showMarkersAsy(String username, GoogleMap map) {
        onUpdateClickedState(username);
        getChildReference(username).child("markers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    MarkerOptions markerOption = ds.getValue(MarkerOptions.class);
                    Double latitude = ds.child("position").child("latitude").getValue(Double.class);
                    Double longitude = ds.child("position").child("longitude").getValue(Double.class);
                    boolean isVisible = ds.child("visible").getValue(boolean.class);
                    markerOption.position(new LatLng(latitude, longitude));
                    if (clicked.contains(markerOption.getPosition())) {
                        markerOption.icon(BitMapFromVector(ctx.getApplicationContext(), R.drawable.ic_campfire));
                    } else {
                        markerOption.icon(BitMapFromVector(ctx.getApplicationContext(), R.drawable.ic_wood_logs));
                    }
                    map.addMarker(markerOption).setVisible(isVisible);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onUpdateClickedState(String username) {
        getChildReference(username).child("clicked").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    double latitude = ds.child("latitude").getValue(Double.class);
                    double longitude = ds.child("longitude").getValue(Double.class);
                    clicked.add(new LatLng(latitude, longitude));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onUpdateUserAchievement(String userId, Map<String, Achievement> achievement) {
        getChildReference(userId)
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        User user = currentData.getValue(User.class);
                        if (user == null) {
                            return Transaction.success(currentData);
                        }
                        user.setAchievedMap(achievement);
                        currentData.setValue(user);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                        Log.d(TAG, "postTransactionForSender:onComplete:" + error);
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

}
