package com.example.torchbearer;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

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

}
