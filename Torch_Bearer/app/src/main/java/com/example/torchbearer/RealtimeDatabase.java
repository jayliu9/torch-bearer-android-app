package com.example.torchbearer;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

public class RealtimeDatabase {
    public static final String USERS_TABLE_NAME = "Users";
    private static final String TAG = RealtimeDatabase.class.getSimpleName();

    private DatabaseReference database;
    private Context ctx;

    public RealtimeDatabase(Context context) {
        this.database = FirebaseDatabase.getInstance().getReference();
        this.ctx = context;
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

    public void showMarkers(String userId, List<MarkerOptions> markerOptions, MarkerCallBack myCallBack) {
        getChildReference(userId).child("markers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    MarkerOptions markerOption = ds.getValue(MarkerOptions.class);
                    Double latitude = ds.child("position").child("latitude").getValue(Double.class);
                    Double longitude = ds.child("position").child("longitude").getValue(Double.class);
                    markerOption.position(new LatLng(latitude, longitude));
                    markerOptions.add(markerOption);
                }
                myCallBack.onCallBack(markerOptions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onUpdateClickedState(String userId, List<LatLng> clicked, ClickedStateCallBack myCallBack) {
        getChildReference(userId).child("clicked").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    double latitude = ds.child("latitude").getValue(Double.class);
                    double longitude = ds.child("longitude").getValue(Double.class);
                    clicked.add(new LatLng(latitude, longitude));
                }
                myCallBack.onCallBack(clicked);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
