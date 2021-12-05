package com.example.torchbearer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore;
    private String userId;
//    private TextView userName = findViewById(R.id.username);
//    private TextView email = findViewById(R.id.email);
//    private TextView phoneNum = findViewById(R.id.phone);
//    private TextView password = findViewById(R.id.password);
//    private ProgressBar progressBar = findViewById(R.id.progressBar);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void register(View view) {

        TextView usernameTxt = findViewById(R.id.username);
        TextView emailTxt = findViewById(R.id.email);
        TextView phoneTxt = findViewById(R.id.phone);
        TextView passwordTxt = findViewById(R.id.password);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        fStore = FirebaseFirestore.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            finish();
        }

        String username = usernameTxt.getText().toString();
        String phone = phoneTxt.getText().toString();
        String email = emailTxt.getText().toString().trim();
        String password = passwordTxt.getText().toString().trim();



        if (TextUtils.isEmpty(email)) {
            emailTxt.setError("Email is Required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordTxt.setError("Password is Required");
            return;
        }

        if (password.length() < 6) {
            passwordTxt.setError("Password must be greater than 6 characters");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Register the user in firebase
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser fuser = firebaseAuth.getCurrentUser();
                    fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this, "Verification Email has been sent.", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                        }
                    });


                    Toast.makeText(MainActivity.this, "User Created.", Toast.LENGTH_LONG).show();
                    userId = firebaseAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userId);
                    Map<String, Object> user = new HashMap<>();
                    user.put("username", username);
                    user.put("email", email);
                    user.put("phone", phone);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "onSuccess: User Profile is created for " + userId);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.toString());
                        }
                    });
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void login(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}