package com.example.hooke.photoaddiction.models;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import com.example.hooke.photoaddiction.MainActivity;
import com.example.hooke.photoaddiction.R;



public class AlarmReceiver extends BroadcastReceiver {

    public static final String TICKER = "Time to shoot";
    public static final String CONTENT_TITLE = "PhotoAddiction";
    public static final String CONTENT_TEXT = "It's time to feed me! Let's do a one shoot.";
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        sayNotify(context);
        setAlarm(context);
    }

    public void setAlarm(Context context) {
        int minute=MainActivity.getMinute();
        if (minute>0) {
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + minute*60*1000, alarmIntent);
       }
    }

    private void sayNotify(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Notification builder = new Notification.Builder(context)
                .setTicker(TICKER)
                .setContentTitle(CONTENT_TITLE)
                .setContentText(CONTENT_TEXT)
                .setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingIntent)
                .build();
        builder.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, builder);
    }
}