package com.example.torchbearer.Notifications;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;


public class MyFirebaseInstanceIdService extends FirebaseMessagingService {

    public static final String SHARED_PREF = "ah_firebase";

    private static final String TAG = MyFirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        String refreshedToken = FirebaseMessaging.getInstance().getToken().toString();

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);
    }


    private void sendRegistrationToServer(String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}