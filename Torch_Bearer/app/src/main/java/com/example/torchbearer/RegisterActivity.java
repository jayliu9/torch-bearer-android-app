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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private TextView usernameTxt;
    private TextView emailTxt;
    private TextView phoneTxt;
    private TextView passwordTxt;

    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    private RealtimeDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameTxt = findViewById(R.id.username);
        emailTxt = findViewById(R.id.email);
        phoneTxt = findViewById(R.id.phone);
        passwordTxt = findViewById(R.id.password);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        db = new RealtimeDatabase(RegisterActivity.this);

    }

    public void register(View view) {

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            emailTxt.setText(personEmail);
            usernameTxt.setText(personName);
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

        registerUser(email, password);


    }

    private void registerUser(String email, String password) {
        // Register the user in firebase
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser fuser = firebaseAuth.getCurrentUser();
                    fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(RegisterActivity.this, "Verification Email has been sent.", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                        }
                    });


                    String username = usernameTxt.getText().toString();
                    String phone = phoneTxt.getText().toString();
                    String email = emailTxt.getText().toString().trim();

                    User user = new User(username, email, phone);

                    db.createUser(fuser.getUid(), user);
//                    FirebaseDatabase.getInstance()
//                            .getReference("Users")
//                            .child(fuser.getUid())
//                            .setValue(user)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(RegisterActivity.this, "Successfully added to database.", Toast.LENGTH_LONG).show();
//                                    } else {
//                                        Toast.makeText(RegisterActivity.this, "Failed to be added to database.", Toast.LENGTH_LONG).show();
//                                    }
//                                }
//                            });

                    Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));

                } else {
                    Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void login(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}