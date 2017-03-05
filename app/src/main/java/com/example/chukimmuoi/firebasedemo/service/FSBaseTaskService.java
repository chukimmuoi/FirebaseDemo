package com.example.chukimmuoi.firebasedemo.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.chukimmuoi.firebasedemo.R;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 2/28/17.
 */

public abstract class FSBaseTaskService extends Service {

    private static final String TAG = FSBaseTaskService.class.getSimpleName();

    private static int PROGRESS_NOTIFICATION_ID = 0;

    private static int FINISHED_NOTIFICATION_ID = 1;

    private int mNumTask = 0;

    protected void taskStarted() {
        changeNumberOfTasks(1);
    }

    protected void taskCompleted() {
        changeNumberOfTasks(-1);
    }

    private synchronized void changeNumberOfTasks(int delta) {
        Log.e(TAG, "changeNumberOfTasks: " + String.valueOf(mNumTask) + ":" + String.valueOf(delta));

        mNumTask += delta;

        if (mNumTask <= 0) {
            Log.e(TAG, "STOPPING");

            //Stop service.
            stopSelf();
        }
    }

    protected void showProgressNotification(String caption, long completedUnits, long totalUnits) {
        int percentComplete = 0;
        if (totalUnits > 0) {
            percentComplete = (int) (completedUnits * 100 / totalUnits);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_file_upload_white_24dp)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(caption)
                    .setProgress(100, percentComplete, false)
                    .setOngoing(true)
                    .setAutoCancel(false);

            NotificationManager manager
                    = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            manager.notify(PROGRESS_NOTIFICATION_ID, builder.build());
        }
    }

    protected void showFinishedNotification(String caption, Intent intent, boolean success) {
        PendingIntent pendingIntent
                = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        int icon = success ?
                R.drawable.ic_check_white_24 :
                R.drawable.ic_error_white_24dp;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(caption)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager
                = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(FINISHED_NOTIFICATION_ID, builder.build());
    }

    protected void dismissProgressNotification() {
        NotificationManager manager
                = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.cancel(PROGRESS_NOTIFICATION_ID);
    }
}
