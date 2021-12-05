package com.example.torchbearer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);

        ProgressBar progressBar = findViewById(R.id.progressBar);


        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            finish();
        }

        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        if (TextUtils.isEmpty(emailStr)) {
            email.setError("Email is Required");
            return;
        }

        if (TextUtils.isEmpty(passwordStr)) {
            password.setError("Password is Required");
            return;
        }

        if (passwordStr.length() < 6) {
            password.setError("Password must be greater than 6 characters");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Authenticate the user in firebase
        firebaseAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Logged in successfully.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void register(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void forgotPassword(View view) {
        final EditText resetMail = new EditText(view.getContext());
        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
        passwordResetDialog.setTitle("Reset Password?");
        passwordResetDialog.setMessage("Enter your email to received reset link");
        passwordResetDialog.setView(resetMail);

        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Extract the email and send reset link
                String mail = resetMail.getText().toString();
                firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(LoginActivity.this, "Reset link sent to your email.", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error! Reset link is not sent. " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Close the dialog
            }
        });

        passwordResetDialog.create().show();

    }


}