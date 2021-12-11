package com.example.torchbearer.profile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.torchbearer.R;
import com.example.torchbearer.RuntimeDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {
    private ImageView profileImage;
    private ImageView editProfileImage;
    private Uri imageUri;
    private Button editProfileButton;
    private Button logoutButton;
    private String userID;
    private RuntimeDatabase mDatabase;
    private StorageReference storageReference;
    private AlertDialog dialog;
    private StorageReference imageFileReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mDatabase = new RuntimeDatabase(this);
        storageReference = FirebaseStorage.getInstance().getReference(userID);
        imageFileReference = storageReference.child("ProfileImages").child(userID);
        profileImage = findViewById(R.id.imageview_profile);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        editProfileImage = findViewById(R.id.edit_profile_image);
        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        editProfileButton = findViewById(R.id.button_edit_profile);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditProfileActivity();
            }
        });
        logoutButton = findViewById(R.id.button_profile_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
//        builder.setCancelable(false);
        builder.setView(R.layout.progress);
        dialog = builder.create();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        //startActivity(new Intent(this, LoginActivity.class));
    }

    private void startEditProfileActivity() {
        startActivity(new Intent(this, EditProfileActivity.class));
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts
                .StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    uploadImage();
                    profileImage.setImageURI(imageUri);
                }
            }
        });
    }



    private void uploadImage() {
        if (imageUri != null) {

            imageFileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                            String imageUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                            mDatabase.getChildReference(userID).child("ProfileImageUrl").setValue(imageUrl);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            dialog.show();
                        }
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}