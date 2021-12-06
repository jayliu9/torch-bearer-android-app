package com.example.torchbearer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openPostPhotoActivity(View view) {
        startActivity(new Intent(MainActivity.this, PostPhotoActivity.class));
    }

    public void openViewPhotoActivity(View view) {
        startActivity(new Intent(MainActivity.this, ViewPhotosAtLocationActivity.class));
    }
}