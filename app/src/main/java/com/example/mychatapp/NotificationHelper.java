package com.example.mychatapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    public static void displayNotification(Context context,String title,String body)
    {
        Intent intent=new Intent(context,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(
                context,100,intent,PendingIntent.FLAG_CANCEL_CURRENT
        );
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(context);
        managerCompat.notify(1,builder.build());
    }
}
