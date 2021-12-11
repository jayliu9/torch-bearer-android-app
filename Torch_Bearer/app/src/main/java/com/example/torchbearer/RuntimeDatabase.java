package com.example.torchbearer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RuntimeDatabase {
    public static final String USERS_TABLE_NAME = "Users";
    public static final String TOKENS_TABLE = "Tokens";
    private static final String TAG = RuntimeDatabase.class.getSimpleName();

    private DatabaseReference mDatabase;
    private Context ctx;

    public RuntimeDatabase(Context context) {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.ctx = context;
    }

    public void createUser(String username) {
        getChildReference(username).setValue(new User(username));
    }

    public void createUserToken(String username, String token) {
        mDatabase.child(TOKENS_TABLE).child(username).setValue(token);
    }

//    public void createUserIfNotExist(String username) {
//        getChildReference(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e(TAG, "Error getting data", task.getException());
//                } else {
//                    User user = task.getResult().getValue(User.class);
//
//                    if (null == user) {
//                        Log.i(TAG, "User '" + username + "' doesn't exist. Creating one...");
//                        createUser(username);
//                    } else {
//                        Log.i(TAG, "User '" + username + "' already exists. Skipped creation");
//                    }
//                }
//            }
//        });
//    }

    public DatabaseReference getChildReference(String child) {
        return this.mDatabase.child(USERS_TABLE_NAME).child(child);
    }


    public void showTransparentLine(String username, List<List<LatLng>> paths, TransparentLineCallBack myCallBack) {
        getChildReference(username).child("paths").addValueEventListener(new ValueEventListener() {
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

    private void updatePath(String username, DataSnapshot dataSnapshot, List<Polyline> paths) {
        paths = dataSnapshot.getValue(List.class);
    }
}
