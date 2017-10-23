/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.compartilhevida.compartilhevida.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import br.com.compartilhevida.compartilhevida.NotificacaoActivity;
import br.com.compartilhevida.compartilhevida.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getBoolean("notifications_new_message", true)){
            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());
                notification(remoteMessage.getData().get("mensagem"), remoteMessage.getData().get("titulo"), remoteMessage.getData().get("uid"));

            }
        }

    }


    public void notification(String messageBody, String titulo, String uid) {
        // Set Notification Title
        String strtitle = titulo;
        // Set Notification Text
        String strtext = messageBody;
        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(this, NotificacaoActivity.class);
        // Send data to NotificationView Class
        intent.putExtra("title", strtitle);
        intent.putExtra("text", strtext);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(this, uniqueInt, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String strRingtonePreference = preference.getString("notifications_new_message_ringtone", "DEFAULT_SOUND");

        boolean vibrate = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getBoolean("notifications_new_message_vibrate", true);
        //Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // Set Icon
                .setSmallIcon(R.mipmap.ic_launcher)
                // Set Ticker Message
                .setTicker(getString(R.string.notificationticker))
                // Set Title
                .setContentTitle(titulo)
                .setSound(Uri.parse(strRingtonePreference))
                // Set Text
                .setContentText(messageBody)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Dismiss Notification
                .setAutoCancel(true);

        if (vibrate){
            builder.setVibrate(new long[] { 1000, 1000,});
        }
        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(uniqueInt, builder.build());

    }
}
