package com.example.torchbearer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostPhotoActivity extends AppCompatActivity {

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 321;
    public static final int CAMERA_REQUEST_CODE = 456;

    public static final String TAG = "POST_PHOTO";
    public static final int GALLERY_REQUEST_CODE = 789;

    ImageView imageSelected;
    Button cameraButton, albumButton, postButton;

    String currentPhotoPath;
    String currentPhotoFileName;
    Uri currentPhotoURI;
    String currentLocation;

    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_photo);

        imageSelected = findViewById(R.id.imageSelected);
        cameraButton = findViewById(R.id.camera);
        albumButton = findViewById(R.id.album);
        postButton = findViewById(R.id.post);

        postButton.setEnabled(false);

        // TODO: replace with real location
        currentLocation = "seattle";

        storageReference = FirebaseStorage.getInstance().getReference();

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCamearaPermission();
                dispatchTakePictureIntent();
            }
        });

        albumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);

            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadToFirebase(currentPhotoFileName, currentPhotoURI);

            }
        });
    }

    private void getCamearaPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File storageDir = Environment.getExternalStorageDirectory();
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File storageDir = Environment.getExternalStorageDirectory();
//        storageDir.mkdirs();

//        File storageDir = Environment.getExternalStoragePublicDirectory("Test");

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoFileName = imageFileName + ".jpg";

//        File image = new File(storageDir, imageFileName + ".jpg");
        Log.i(TAG, image.getAbsolutePath());

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                Log.i(TAG, "Creating impage");

                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(TAG, "Error: " + ex);

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.torchbearer.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
//        } else {
//            Log.d(LOG_TAG, "Error: " + "sth wrong");
//
//        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            //&& data.getExtras() != null

            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                currentPhotoURI = Uri.fromFile(f);
                imageSelected.setImageURI(currentPhotoURI);

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                File f = new File(currentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                postButton.setEnabled(true);
            }
//            Bitmap image = (Bitmap) data.getExtras().get("data");
//            imageSelected.setImageBitmap(image);
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            //&& data.getExtras() != null

            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                currentPhotoURI = contentUri;

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);

                currentPhotoFileName = imageFileName;

                Log.i(TAG, "Chossing " + imageFileName);

//                File f = new File(currentPhotoPath);
                imageSelected.setImageURI(contentUri);

//                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
////                File f = new File(currentPhotoPath);
//                Uri contentUri = Uri.fromFile(f);
//                mediaScanIntent.setData(contentUri);
//                this.sendBroadcast(mediaScanIntent);
                postButton.setEnabled(true);

            }
//            Bitmap image = (Bitmap) data.getExtras().get("data");
//            imageSelected.setImageBitmap(image);
        }
    }

    private String getFileExt(Uri uri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(uri));
    }

    private void uploadToFirebase(String fileName, Uri uri) {
        StorageReference image = storageReference.child(currentLocation + "/" + fileName);

//// Create a reference to 'images/mountains.jpg'
//        StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");
//
//// While the file names are the same, the references point to different files
//        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        image.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(PostPhotoActivity.this, "Photo Posted Successfully!", Toast.LENGTH_SHORT).show();

                postButton.setEnabled(false);
                imageSelected.setImageResource(android.R.color.transparent);


//                imageSelected.cl
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i(TAG, "Success " + uri.toString());
                    }
                });
            }
        });
    }
}