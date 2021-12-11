package com.example.torchbearer.achievement;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.torchbearer.MapActivity;
import com.example.torchbearer.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AchievementMap {

    private Map<String, Achievement> map;
    private String userToken;

    public AchievementMap () {
        createAchievementMap();
    }

    public AchievementMap(Map<String, Achievement> achievedMap) {
        createAchievementMap();
        for (String key : achievedMap.keySet()) {
            this.map.remove(key);
        }
    }

    private void createAchievementMap() {
        this.map = new HashMap<>();
        map.put("First Path", new Achievement("First Path", "First time start tracking path."));
        map.put("10 Feet", new Achievement("10 Feet", "Tracked 10-foot long path."));
        map.put("First Log", new Achievement("First Log", "Reached the first wood log."));
    }

    public void validateAchievements(Map<String, Achievement> achievedMap, User user) {
        for (String key : achievedMap.keySet()) {
            this.map.remove(key);
        }
        if (user.getNumOfPath() == 1) {
            isAchievementValid("First Path", achievedMap);
        }
        if (user.getTotalLength() >= 10.0) {
            isAchievementValid("10 Feet", achievedMap);
        }
        if (user.getLogCount() == 1) {
            isAchievementValid("First Log", achievedMap);
        }
    }

    public void isAchievementValid(String title, Map<String, Achievement> achievedMap) {
        if (this.map.containsKey(title)) {
            Achievement achievement = this.map.get("10 Feet");
            achievement.setDate(Calendar.getInstance().getTime().toString());
            this.map.remove(achievement.getTitle());
            achievedMap.put(achievement.getTitle(), achievement);
        }
    }


    public Map<String, Achievement> getMap() {
        return map;
    }

    public void setMap(Map<String, Achievement> map) {
        this.map = map;
    }
}
