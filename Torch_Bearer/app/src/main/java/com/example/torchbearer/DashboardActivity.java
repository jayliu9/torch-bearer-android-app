package com.example.torchbearer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private TextView username;
    private TextView email;
    private TextView phoneNum;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;

    private String userId;

    private Button verifyBtn;
    private TextView verifyMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        username = findViewById(R.id.usernameTxt);
        email = findViewById(R.id.emailTxt);
        phoneNum = findViewById(R.id.phoneTxt);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        verifyBtn = findViewById(R.id.verifyBtn);
        verifyMsg = findViewById(R.id.verifyMsg);

        userId = firebaseAuth.getCurrentUser().getUid();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user.isEmailVerified()) {
            verifyBtn.setVisibility(View.VISIBLE);
            verifyMsg.setVisibility(View.VISIBLE);

            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(view.getContext(), "Verification Email has been sent.", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                        }
                    });
                }
            });
        }

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, (documentSnapshot, e) -> {
            username.setText(documentSnapshot.getString("username"));
            email.setText(documentSnapshot.getString("email"));
            phoneNum.setText(documentSnapshot.getString("phone"));
        });
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}