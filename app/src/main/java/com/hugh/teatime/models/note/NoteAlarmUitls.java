package com.hugh.teatime.models.note;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.utils.LogUtil;
import com.hugh.teatime.utils.StringUtil;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class NoteAlarmUitls {

    /**
     * 打开定时提醒
     *
     * @param context 上下文
     * @param time    时间
     */
    public static void openAlarmNotice(Context context, String time) {
        if (context == null) {
            return;
        }
        if (StringUtil.isStrNull(time)) {
            time = "00:00";
        }
        context = context.getApplicationContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        String[] times = time.split(":");
        int hour = 0;
        int minute = 0;
        if (times.length == 2) {
            hour = Integer.parseInt(times[0]);
            minute = Integer.parseInt(times[1]);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        final long INTERVAL = 24 * 60 * 60 * 1000;//24小时

        LogUtil.logHugh("openALarmNotice time=" + StringUtil.formatTimestamp1(calendar.getTimeInMillis()));
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, GlobalVar.NOTE_ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, broadcast);

    }

    /**
     * 关闭定时提醒
     *
     * @param context 上下文
     */
    public static void cancelAlarmNotice(Context context) {
        if (context == null) {
            return;
        }
        context = context.getApplicationContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        LogUtil.logHugh("cancelALarmNotice");
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, GlobalVar.NOTE_ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(broadcast);
    }
}
