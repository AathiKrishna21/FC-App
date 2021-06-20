package com.joker.fcapp1.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.joker.fcapp1.R;

public class NotificationHelper extends ContextWrapper {

    private static final String FCAPP_CHANNEL_ID="com.joker.fcapp1.tce";
    private static final String FCAPP_CHANNEL_NAME="FC App";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel fcappchannel = new NotificationChannel(FCAPP_CHANNEL_ID,
                FCAPP_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);
        fcappchannel.enableLights(true);
        fcappchannel.enableVibration(true);
        fcappchannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(fcappchannel);
    }

    public NotificationManager getManager() {
        if(manager == null)
                manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }
    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getFCAppChannelNotification(String title, String body, PendingIntent contentIntent,
                                                                     Uri soundUri){
        return new Notification.Builder(getApplicationContext(),FCAPP_CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_default_notification)
                .setColor(getColor(R.color.orange))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.fc_logo))
                .setSound(soundUri)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setAutoCancel(true);
    }
}
