package com.example.torchbearer.achievement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.torchbearer.R;
import com.example.torchbearer.achievement.achievable.AchievableFragment;
import com.example.torchbearer.achievement.achieved.AchievedFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AchievementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        BottomNavigationView bottomNavigationView = findViewById(R.id.achievement_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected = null;
                switch(item.getItemId()) {
                    case R.id.achieved:
                        selected = new AchievedFragment();
                        break;
                    case R.id.achievable:
                        selected = new AchievableFragment();
                        break;
                }
                if (selected != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.achievement_fragment_container, selected).commit();
                }
                return true;
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.achievement_fragment_container, new AchievedFragment()).commit();
    }
}