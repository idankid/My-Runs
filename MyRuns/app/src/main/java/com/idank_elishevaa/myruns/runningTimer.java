package com.idank_elishevaa.myruns;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.SystemClock;
import android.widget.Chronometer;

import java.util.Timer;
import java.util.TimerTask;

import androidx.core.app.NotificationCompat;


public class runningTimer {
    private final Chronometer stopwatch;
    private boolean isRunning = false;
    private long timeElapsed;
    private Timer timer;
    private final TimerCallback timerCallback;
    private Context context;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "running_timer_channel";

    public interface TimerCallback {
        void onTimerUpdate(String time);
    }

    public runningTimer(Context context, Chronometer chronometer, TimerCallback callback){
        //getting the stopwatch and starting it
        stopwatch = chronometer;
        timerCallback = callback;
        this.context = context;
    }

    // Starting the stopwatch
    public void start(long start) {
        if (!isRunning) {
            if(start == -1)
                stopwatch.setBase(SystemClock.elapsedRealtime());
            else
                stopwatch.setBase(start);
            stopwatch.start();
            isRunning = true;

            // Start the timer to update stopwatch display
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    timeElapsed = SystemClock.elapsedRealtime() - stopwatch.getBase();
                    String time = makeText();
                    // Show the stopwatch in status bar
                    if (timerCallback != null) {
                        timerCallback.onTimerUpdate(time);
                    }
                }
            }, 0, 100);


        }
    }

    // stopping the stopwatch
    public String stop() {
        if (isRunning) {
            stopwatch.stop();
            isRunning = false;
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            return makeText();
        }

        return "00:00";
    }

    // creates the time text
    private String makeText() {
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

    public long getStartTime(){
        return stopwatch.getBase();
    }

}
