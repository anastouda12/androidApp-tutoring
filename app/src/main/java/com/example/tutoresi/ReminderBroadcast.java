package com.example.tutoresi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Broadcast receiver responsible for running the code to display the notification
 */
public class ReminderBroadcast extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationCompat.Builder builder =  new NotificationCompat.Builder(context,context.getString(R.string.channelID))
                .setSmallIcon(R.drawable.ic_action_reminder)
                .setContentTitle(context.getString(R.string.notifTitle))
                .setContentText(context.getString(R.string.notifText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(getUniqueID(),builder.build());
    }

    /**
     * It gets current system time. Then I'm reading only last 4 digits from it.
     * I'm using it to create unique id every time notification is displayed.
     * So the probability of getting same or reset of notification id will be avoided.
     */
    private int getUniqueID(){
        long time = new Date().getTime();
        String tmpStr = String.valueOf(time);
        String last4Str = tmpStr.substring(tmpStr.length() - 5);
        return Integer.valueOf(last4Str);
    }
}
