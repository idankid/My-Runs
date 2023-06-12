package com.idank_elishevaa.myruns;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class runningTimerService extends Service {
    private static final String CHANNEL_ID = "stopwatch_channel";
    private static final int NOTIFICATION_ID = 1;

    private Handler handler;
    private long startTime;
    private boolean isRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    public long getStartTime(){
        return startTime;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("start_time")) {
            startTime = intent.getLongExtra("start_time", 0L);
        }

        String time = makeText(SystemClock.elapsedRealtime() - startTime);
        startForeground(NOTIFICATION_ID, showNotification(time));

        startTimer();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
        hideNotification();
    }

    private void startTimer() {
        if (!isRunning) {
            handler.postDelayed(timerRunnable, 0);
            isRunning = true;
        }
    }

    private void stopTimer() {
        if (isRunning) {
            handler.removeCallbacks(timerRunnable);
            isRunning = false;
        }
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedTime = SystemClock.elapsedRealtime() - startTime;
            String time = makeText(elapsedTime);
            updateNotification(time);
            handler.postDelayed(this, 1000);
        }
    };

    // creates the time text
    private String makeText(long timeElapsed) {
        long seconds = timeElapsed / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds %= 60;
        minutes %= 60;

        // building the string
        String time;
        if(hours == 0)
            time = String.format("%02d:%02d", minutes, seconds);
        else
            time = String.format("%02d:%02d:%02d",hours, minutes, seconds);

        return time;
    }

    private Notification showNotification(String time) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Creating a notification channel
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Running Timer", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);


        // Building the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("Running Timer")
                .setContentText(time)
                .setOngoing(true);

        return builder.build();

    }

    private void hideNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void updateNotification(String content) {
        Notification notification = showNotification(content);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

}
