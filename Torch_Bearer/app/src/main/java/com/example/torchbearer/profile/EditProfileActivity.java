package com.example.torchbearer.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.torchbearer.R;
import com.example.torchbearer.RealtimeDatabase;

import com.google.firebase.auth.FirebaseAuth;


public class EditProfileActivity extends AppCompatActivity {


    private RealtimeDatabase db;

    private FirebaseAuth firebaseAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        db = new RealtimeDatabase(this);

    }


    public void updateProfile(View view) {

        TextView username = findViewById(R.id.editText_profile_username);
        TextView phone = findViewById(R.id.editText_profile_phone);

        String usernameStr = username.getText().toString();
        String phoneNumStr = phone.getText().toString();

        db.onUpdateUsername(userId, usernameStr);
        db.onUpdateUserPhoneNum(userId, phoneNumStr);
    }
}