package com.example.torchbearer.Notifications;

import android.app.Activity;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;


public class SendNotification {


    private static final String SERVER_KEY = "key=AAAAm8aojVk:APA91bHprXNlrQ3ezv1nO21yuoHUiDsN7Rdi_hXaI0JJJw7JA5RENAsljULWePCJAxbEoPt1VoqUAeappkQmaeJJ7pTYWm7FEbObulvoxAc1pl_Niy8wlvSCSRZ58GM0FMm-tv6BvLhv";


    public void sendMessageToDevice(View view, String targetToken, String achievement, Activity activity) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessageToDevice(targetToken, achievement, activity);
            }
        }).start();
    }


    private void sendMessageToDevice(String targetToken, String achievement, Activity activity) {

        // Prepare data
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "New Achievement!");
            jNotification.put("body", "Congratulations！You have achieved a new achievement! " + achievement);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");

            jdata.put("title", "data title from 'SEND STICKER BUTTON'");
            jdata.put("content", "data content from 'SEND STICKER BUTTON'");

            /***
             * The Notification object is now populated.
             * Next, build the Payload that we send to the server.
             */

            // If sending to a single client
            jPayload.put("to", targetToken); // CLIENT_REGISTRATION_TOKEN);

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        final String resp = NotificationUtils.fcmHttpConnection(SERVER_KEY, jPayload);
        NotificationUtils.postToastMessage("Status from Server: " + resp, activity.getApplicationContext());
    }
}