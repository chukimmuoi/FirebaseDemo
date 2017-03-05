package com.example.chukimmuoi.firebasedemo.service;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.chukimmuoi.firebasedemo.FirebaseStorageActivity;
import com.example.chukimmuoi.firebasedemo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 2/28/17.
 */

public class FSDownloadService extends FSBaseTaskService {

    private static final String TAG = FSDownloadService.class.getSimpleName();

    //Action
    public static final String ACTION_DOWNLOAD = "action_download";
    public static final String ACTION_DOWNLOAD_COMPLETED = "action_download_completed";
    public static final String ACTION_DOWNLOAD_ERROR = "action_download_error";

    //Extras
    public static final String EXTRA_DOWNLOAD_PATH = "extra_download_path";
    public static final String EXTRA_BYTES_DOWNLOADED = "extra_bytes_downloaded";

    private static final int MEGABYTE = 1024;

    private StorageReference mStorageReference;

    @Override
    public void onCreate() {
        super.onCreate();

        mStorageReference = FirebaseStorage.getInstance().getReference();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: " + intent + ":" + String.valueOf(startId));
        if (ACTION_DOWNLOAD.equals(intent.getAction())) {
            String downloadPath = intent.getStringExtra(EXTRA_DOWNLOAD_PATH);
            downloadFromPath(downloadPath);
        }

        return START_REDELIVER_INTENT;
    }

    private void downloadFromPath(final String downloadPath) {
        Log.e(TAG, "downloadFromPath: " + downloadPath);

        taskStarted();

        showProgressNotification(getString(R.string.progress_downloading), 0, 0);

        mStorageReference.child(downloadPath).getStream(new StreamDownloadTask.StreamProcessor() {
            @Override
            public void doInBackground(StreamDownloadTask.TaskSnapshot taskSnapshot,
                                       InputStream inputStream) throws IOException {
                Log.e(TAG, "Download: doInBackground");

                long totalBytes = taskSnapshot.getTotalByteCount();
                long bytesDownloaded = 0;

                byte[] buffer = new byte[MEGABYTE];
                int size;

                while ((size = inputStream.read(buffer)) != -1) {
                    bytesDownloaded += size;
                    showProgressNotification(getString(R.string.progress_downloading),
                            bytesDownloaded, totalBytes);
                }

                inputStream.close();
            }
        }).addOnSuccessListener(new OnSuccessListener<StreamDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(StreamDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e(TAG, "Download: Success");

                broadcastDownloadFinished(downloadPath, taskSnapshot.getTotalByteCount());
                showDownloadFinishedNotification(downloadPath,
                        (int) taskSnapshot.getTotalByteCount());

                taskCompleted();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Download: Failure");

                broadcastDownloadFinished(downloadPath, -1);
                showDownloadFinishedNotification(downloadPath, -1);

                taskCompleted();
            }
        });
    }

    private boolean broadcastDownloadFinished(String downloadPath, long bytesDownloaded) {
        boolean success = bytesDownloaded != -1;
        String action = success ? ACTION_DOWNLOAD_COMPLETED : ACTION_DOWNLOAD_ERROR;

        Intent broadcast = new Intent(action)
                .putExtra(EXTRA_DOWNLOAD_PATH, downloadPath)
                .putExtra(EXTRA_BYTES_DOWNLOADED, bytesDownloaded);

        return LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcast);
    }

    private void showDownloadFinishedNotification(String downloadPath, int bytesDownloaded) {
        dismissProgressNotification();

        Intent intent = new Intent(this, FirebaseStorageActivity.class)
                .putExtra(EXTRA_DOWNLOAD_PATH, downloadPath)
                .putExtra(EXTRA_BYTES_DOWNLOADED, bytesDownloaded)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean success = bytesDownloaded != -1;
        String caption = success ?
                getString(R.string.download_success) :
                getString(R.string.download_failure);

        showFinishedNotification(caption, intent, true);
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DOWNLOAD_COMPLETED);
        filter.addAction(ACTION_DOWNLOAD_ERROR);

        return filter;
    }
}
