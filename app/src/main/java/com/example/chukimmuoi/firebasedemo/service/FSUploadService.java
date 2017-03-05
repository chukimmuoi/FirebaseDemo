package com.example.chukimmuoi.firebasedemo.service;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 2/28/17.
 */

public class FSUploadService extends FSBaseTaskService {

    private static final String TAG = FSUploadService.class.getSimpleName();

    //Action
    public static final String ACTION_UPLOAD = "action_upload";
    public static final String ACTION_UPLOAD_COMPLETED = "action_upload_completed";
    public static final String ACTION_UPLOAD_ERROR = "action_upload_error";

    //Extras
    public static final String EXTRA_FILE_URI = "extra_file_uri";
    public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";

    //Constants
    public static final String KEY_PHOTOS = "photos";

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
        if (ACTION_UPLOAD.equals(intent.getAction())) {
            Uri fileUri = intent.getParcelableExtra(EXTRA_FILE_URI);
            uploadFromUri(fileUri);
        }

        return START_REDELIVER_INTENT;
    }

    private void uploadFromUri(final Uri fileUri) {
        Log.e(TAG, "uploadFromUri: src = " + fileUri.toString());

        taskStarted();
        showProgressNotification(getString(R.string.progress_uploading), 0, 0);

        final StorageReference photoRef = mStorageReference.child(KEY_PHOTOS)
                .child(fileUri.getLastPathSegment());
        Log.e(TAG, "uploadFromUri: dst = " + photoRef.getPath());

        photoRef.putFile(fileUri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.e(TAG, "uploadFromUri: onProgress");
                        showProgressNotification(getString(R.string.progress_uploading),
                                taskSnapshot.getBytesTransferred(),
                                taskSnapshot.getTotalByteCount());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.e(TAG, "uploadFromUri: onSuccess");

                        Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                        broadcastUploadFinished(downloadUri, fileUri);
                        showUploadFinishedNotification(downloadUri, fileUri);

                        taskCompleted();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "uploadFromUri: onFailure");

                        broadcastUploadFinished(null, fileUri);
                        showUploadFinishedNotification(null, fileUri);

                        taskCompleted();
                    }
                });
    }

    private boolean broadcastUploadFinished(Uri downloadUrl, Uri fileUri) {
        boolean success = downloadUrl != null;

        String action = success ? ACTION_UPLOAD_COMPLETED : ACTION_UPLOAD_ERROR;

        Intent broadcast = new Intent(action)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_FILE_URI, fileUri);

        return LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcast);
    }

    private void showUploadFinishedNotification(Uri downloadUrl, Uri fileUri) {
        dismissProgressNotification();

        Intent intent = new Intent(this, FirebaseStorageActivity.class)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_FILE_URI, fileUri)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean success = downloadUrl != null;

        String caption = success ?
                getString(R.string.upload_success) :
                getString(R.string.upload_failure);

        showFinishedNotification(caption, intent, success);
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPLOAD_COMPLETED);
        intentFilter.addAction(ACTION_UPLOAD_ERROR);

        return intentFilter;
    }
}
