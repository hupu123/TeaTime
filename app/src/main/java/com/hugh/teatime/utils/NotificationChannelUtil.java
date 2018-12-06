package com.hugh.teatime.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationChannelUtil {

    public static final String CHANNEL_ID_TEATIME_NOTE = "teatime_note";

    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "TeaTime-记事本消息";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_TEATIME_NOTE, channelName, NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
