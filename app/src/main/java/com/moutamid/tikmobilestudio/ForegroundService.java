package com.moutamid.tikmobilestudio;

import static android.content.Intent.getIntent;
import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class ForegroundService extends Service {
    private static final String LOG_TAG = "ForegroundService";
    PassData data;
    private int flag;
    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // create the custom or default notification
        // based on the android version
        MainActivity m = new MainActivity();
       // PassData data = m.getData();
        //Activity a = m.getAct();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(2, new Notification());

        // create an instance of Window class
        // and display the content on screen

        Window window=new Window(this);
        window.open();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
        /*if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            // your start service code
        }
        else if (intent.getAction().equals( Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            //your end servce code
            stopForeground(true);
            stopSelfResult(startId);
        }
        return START_STICKY;*/
    }

    // for android version >=O we need to create
    // custom notification stating
    // foreground service is running
    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_MIN);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Service running")
                .setContentText("Displaying over other apps")

                // this is important, otherwise the notification will show the way
                // you want i.e. it will show some default notification
                .setSmallIcon(R.drawable.ic_launcher_foreground)

                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        //stopService(new Intent(getApplicationContext(), ForegroundService.class));
    }
}
