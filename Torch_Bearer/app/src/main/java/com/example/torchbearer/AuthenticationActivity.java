package com.example.torchbearer;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class AuthenticationActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anthentication);
        googleBtnUi();
//        facebookSignIn();

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Log.d(TAG, "onSuccess");

                            Intent data = result.getData();
                            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);

                            try {
                                GoogleSignInAccount signInAcc = signInTask.getResult(ApiException.class);
                                AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAcc.getIdToken(), null);

                                firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Toast.makeText(getApplicationContext(), "Your Google Account is Connected to Our Application.", Toast.LENGTH_SHORT).show();

                                        User user = new User(signInAcc.getDisplayName(), signInAcc.getEmail());

                                        FirebaseDatabase.getInstance()
                                                .getReference("Users")
                                                .child(firebaseAuth.getCurrentUser().getUid())
                                                .setValue(user)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getApplicationContext(), "Successfully added to database.", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "Failed to be added to database.", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });

                                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Your Google Account Failed to Connect Our Application.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (ApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        SignInButton google = findViewById(R.id.googleBtn);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("165287983952-gd03g801lttd7vanju271bl2habn7mf0.apps.googleusercontent.com")
                        .requestEmail()
                        .build();

                GoogleSignInClient signInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
                GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

                if (signInAccount != null || firebaseAuth.getCurrentUser() != null) {
                    Toast.makeText(getApplicationContext(), "User is Logged in Already.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    finish();
                }

                Intent sign = signInClient.getSignInIntent();
                someActivityResultLauncher.launch(sign);
            }
        });
    }

    private void googleBtnUi() {

        SignInButton googleButton = findViewById(R.id.googleBtn);

        for (int i = 0; i < googleButton.getChildCount(); i++) {
            View v = googleButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
//                tv.setTextSize(14);
//                tv.setTypeface(null, Typeface.NORMAL);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                tv.setText("Continue with Google");
//                tv.setTextColor(Color.parseColor("#"));
//                tv.setBackgroundColor();
//                tv.setSingleLine(true);
//                tv.setPadding(15, 15, 15, 15);

                return;
            }
            if (v instanceof ImageView) {
                ImageView iv = (ImageView) v;
                iv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }


    public void facebookSignIn() {

        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(getApplication());

        LoginButton facebookBtn = findViewById(R.id.facebookBtn);

        callbackManager = CallbackManager.Factory.create();

        facebookBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d(TAG, "onSuccess" + loginResult);
                AuthCredential authCredential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());

                firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getApplicationContext(), "Your Facebook Account is Connected to Our Application.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Your Facebook Account Failed to Connect Our Application.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Log.d(TAG, "onError");
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//        super.onActivityResult(requestCode, resultCode, data);
//    }


    public void emailSignIn(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}