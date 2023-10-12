package com.example.studenttracker;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class AlarmIntentService extends IntentService {

    private int NOTIFICATION_ID;

    public AlarmIntentService() {
        super("AlarmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        createDateNotificationChannel();
        TrackerDatabase trackerDatabase = TrackerDatabase.getInstance(getApplicationContext());
        List<Alarm> courseAlarms = trackerDatabase.getAllCourseAlarms();
        List<Alarm> assessmentAlarms = trackerDatabase.getAllAssessmentAlarms();
        long todayMillis = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        for (Alarm alarm: courseAlarms) {
            if (alarm.getTime() == todayMillis) {
                String text;
                if (alarm.getType().equals("start")) {
                    text = "You have " + alarm.getDays() + " days before the start of a course. Course title: " + alarm.getTitle();
                }
                else{
                    text = "You have " + alarm.getDays() + " days before the end of a course. Course title: " + alarm.getTitle();
                }
                createDateNotification(text);
                trackerDatabase.deleteCourseAlarm(alarm.getId());
            }
            if(alarm.getTime() < todayMillis) {
                trackerDatabase.deleteCourseAlarm(alarm.getId());
            }
        }
        for (Alarm alarm: assessmentAlarms) {
            if (alarm.getTime() == todayMillis) {
                String text;
                if (alarm.getType().equals("start")) {
                    text = "You have " + alarm.getDays() + " days before the start of an assessment. Assessment title: " + alarm.getTitle();
                }
                else{
                    text = "You have " + alarm.getDays() + " days before the end of an assessment. Assessment title: " + alarm.getTitle();
                }
                NOTIFICATION_ID = (int)System.currentTimeMillis();
                createDateNotification(text);
                trackerDatabase.deleteAssessmentAlarm(alarm.getId());
            }
            if(alarm.getTime() < todayMillis) {
                trackerDatabase.deleteCourseAlarm(alarm.getId());
            }
        }
    }

    private final String CHANNEL_ID_ALERT = "channel_alert";

    private void createDateNotificationChannel() {
        if (Build.VERSION.SDK_INT < 26) return;
        CharSequence name = getResources().getString(R.string.channel_name);
        String description = getResources().getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID_ALERT, name, importance);
        channel.setDescription(description);

        // Register channel with system
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        notificationManager.createNotificationChannel(channel);
    }

    private void createDateNotification(String text) {
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID_ALERT)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("AlarmIntentService", "createDateNotification(String text) - post_notifications permission NOT granteed");
        }
        else {
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }
}