package com.example.torchbearer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.torchbearer.Notifications.SendNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


public class AchievementNotification extends AppCompatActivity {

    private static final String SERVER_KEY = "key=AAAAJnvue1A:APA91bEH6vHhgv7BhRwiNThoBlRDyqeHOYyFWbv3Ef8oGYqEFUyAHgd7D2K2KmysT8Vql1kbiJRZiFUAmsMGGuT7p9of7LdmmDRdzS0fJLBFkITD6aVzihSUrCeRfyUlGmiR2elZtoEh";

    private static final String TAG = AchievementNotification.class.getSimpleName();

    private String userToken;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String userId;
    private RealtimeDatabase db;
    private String achievement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achivement_notification);

        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();

        db = new RealtimeDatabase(AchievementNotification.this);


        // Generate and updates the token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(AchievementNotification.this, "Something is wrong!", Toast.LENGTH_SHORT).show();
                } else {

                    userToken = task.getResult();

                    Log.e("CLIENT_REGISTRATION_TOKEN", userToken);
                    Toast.makeText(AchievementNotification.this, "CLIENT_REGISTRATION_TOKEN Existed", Toast.LENGTH_SHORT).show();
                    db.onUpdateToken(userId, userToken);
                }
            }
        });

    }


    private void sendNotification(View view) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String receiverToken = user.getToken();
                SendNotification notification = new SendNotification();
                notification.sendMessageToDevice(view, receiverToken, achievement, AchievementNotification.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
