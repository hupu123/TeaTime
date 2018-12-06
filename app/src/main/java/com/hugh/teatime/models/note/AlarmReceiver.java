package com.hugh.teatime.models.note;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.hugh.teatime.R;
import com.hugh.teatime.utils.LogUtil;
import com.hugh.teatime.utils.NotificationChannelUtil;
import com.hugh.teatime.utils.StringUtil;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.logHugh("AlarmReceiver onReceive");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        sendSimpleNotification(context, notificationManager);
    }

    /**
     * 发送记事提醒通知
     *
     * @param context 上下文
     * @param nm      通知管理对象
     */
    private void sendSimpleNotification(Context context, NotificationManager nm) {
        // 创建通知ID（使用yyyyMMdd日期作为ID）
        int noticeId = Integer.parseInt(StringUtil.formatTimestamp2(new Date().getTime()));
        // 创建点击通知时发送的广播
        Intent intent = new Intent(context, EventLineActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        // 创建通知
        Notification.Builder nb = new Notification.Builder(context)
                // 设置通知左侧的小图标
                .setSmallIcon(R.mipmap.icon_note)
                // 设置通知标题
                .setContentTitle(context.getResources().getString(R.string.title_note))
                // 设置通知内容
                .setContentText(context.getResources().getString(R.string.content_note))
                // 设置点击通知后自动删除通知
                .setAutoCancel(true)
                // 设置显示通知时间
                .setShowWhen(true)
                // 设置通知右侧的大图标
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo))
                // 设置点击通知时的响应事件
                .setContentIntent(pi);
        // Android 26及以上需要设置频道ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nb.setChannelId(NotificationChannelUtil.CHANNEL_ID_TEATIME_NOTE);
        }
        // 发送通知
        nm.notify(noticeId, nb.build());
    }
}
