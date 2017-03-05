package com.example.chukimmuoi.firebasedemo;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chukimmuoi.firebasedemo.service.FSDownloadService;
import com.example.chukimmuoi.firebasedemo.service.FSUploadService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.Locale;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 2/28/17.
 */

public class FirebaseStorageActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = FirebaseStorageActivity.class.getSimpleName();

    private static final int RC_TAKE_PICTURE = 101;

    private static final String KEY_FILE_URI = "key_file_uri";

    private static final String KEY_DOWNLOAD_URL = "key_download_url";

    private Button mBtnCamera;

    private Button mBtnSignIn;

    private Button mBtnDownload;

    private TextView mTvPictureDownloadUri;

    private LinearLayout mLayoutSignIn;

    private LinearLayout mLayoutStorage;

    private LinearLayout mLayoutDownload;

    private BroadcastReceiver mBroadcastReceiver;

    private ProgressDialog mProgressDialog;

    private FirebaseAuth mFirebaseAuth;

    private Uri mFileUri = null;

    private Uri mDownloadUrl = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        createUI();

        mFirebaseAuth = FirebaseAuth.getInstance();

        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(KEY_FILE_URI);
            mDownloadUrl = savedInstanceState.getParcelable(KEY_DOWNLOAD_URL);
        }
        onNewIntent(getIntent());

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "onReceive: " + intent);
                hideProgressDialog();

                switch (intent.getAction()) {
                    case FSDownloadService.ACTION_DOWNLOAD_COMPLETED:
                        long numBytes = intent.getLongExtra(FSDownloadService.EXTRA_BYTES_DOWNLOADED, 0);

                        showMessageDialog(getString(R.string.success), String.format(Locale.getDefault(),
                                "%d bytes downloaded from %s",
                                numBytes,
                                intent.getStringExtra(FSDownloadService.EXTRA_DOWNLOAD_PATH)));
                        break;
                    case FSDownloadService.ACTION_DOWNLOAD_ERROR:
                        showMessageDialog("Error", String.format(Locale.getDefault(),
                                "Failed to download from %s",
                                intent.getStringExtra(FSDownloadService.EXTRA_DOWNLOAD_PATH)));
                        break;
                    case FSUploadService.ACTION_UPLOAD_COMPLETED:
                    case FSUploadService.ACTION_UPLOAD_ERROR:
                        onUploadResultIntent(intent);
                        break;
                }
            }
        };

    }

    private void createUI() {
        mBtnCamera = (Button) findViewById(R.id.btn_camera);
        mBtnCamera.setOnClickListener(this);

        mBtnSignIn = (Button) findViewById(R.id.btn_sign_in);
        mBtnSignIn.setOnClickListener(this);

        mBtnDownload = (Button) findViewById(R.id.btn_download);
        mBtnDownload.setOnClickListener(this);

        mTvPictureDownloadUri = (TextView) findViewById(R.id.picture_download_uri);

        mLayoutSignIn = (LinearLayout) findViewById(R.id.layout_signin);

        mLayoutStorage = (LinearLayout) findViewById(R.id.layout_storage);

        mLayoutDownload = (LinearLayout) findViewById(R.id.layout_download);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(FSUploadService.EXTRA_DOWNLOAD_URL)) {
            onUploadResultIntent(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI(mFirebaseAuth.getCurrentUser());

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mBroadcastReceiver, FSDownloadService.getIntentFilter());
        manager.registerReceiver(mBroadcastReceiver, FSUploadService.getIntentFilter());
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_FILE_URI, mFileUri);
        outState.putParcelable(KEY_DOWNLOAD_URL, mDownloadUrl);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult: " + requestCode + ":" + resultCode + ":" + data);
        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                mFileUri = data.getData();

                if (mFileUri != null) {
                    uploadFromUri(mFileUri);
                } else {
                    Log.e(TAG, "File URI is null");
                }
            } else {
                Toast.makeText(this, "Taking picture failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFromUri(Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        // Save the File URI
        mFileUri = fileUri;

        // Clear the last download, if any
        updateUI(mFirebaseAuth.getCurrentUser());
        mDownloadUrl = null;

        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        startService(new Intent(this, FSUploadService.class)
                .putExtra(FSUploadService.EXTRA_FILE_URI, fileUri)
                .setAction(FSUploadService.ACTION_UPLOAD));

        // Show loading spinner
        showProgressDialog(getString(R.string.progress_uploading));
    }

    private void beginDownload() {
        // Get path
        String path = FSUploadService.KEY_PHOTOS + File.separator + mFileUri.getLastPathSegment();

        // Kick off MyDownloadService to download the file
        Intent intent = new Intent(this, FSDownloadService.class)
                .putExtra(FSDownloadService.EXTRA_DOWNLOAD_PATH, path)
                .setAction(FSDownloadService.ACTION_DOWNLOAD);
        startService(intent);

        // Show loading spinner
        showProgressDialog(getString(R.string.progress_downloading));
    }

    private void launchCamera() {
        Log.d(TAG, "launchCamera");

        // Pick an image from storage
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, RC_TAKE_PICTURE);
    }

    private void signInAnonymously() {
        // Sign in anonymously. Authentication is required to read or write from Firebase Storage.
        showProgressDialog(getString(R.string.progress_auth));

        mFirebaseAuth.signInAnonymously()
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "signInAnonymously:SUCCESS");
                        hideProgressDialog();

                        updateUI(authResult.getUser());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "signInAnonymously:FAILURE", exception);
                        hideProgressDialog();

                        updateUI(null);
                    }
                });
    }

    private void onUploadResultIntent(Intent intent) {
        // Got a new intent from MyUploadService with a success or failure
        mDownloadUrl = intent.getParcelableExtra(FSUploadService.EXTRA_DOWNLOAD_URL);
        mFileUri = intent.getParcelableExtra(FSUploadService.EXTRA_FILE_URI);

        updateUI(mFirebaseAuth.getCurrentUser());
    }

    private void updateUI(FirebaseUser user) {
        // Signed in or Signed out
        if (user != null) {
            mLayoutSignIn.setVisibility(View.GONE);
            mLayoutStorage.setVisibility(View.VISIBLE);
        } else {
            mLayoutSignIn.setVisibility(View.VISIBLE);
            mLayoutStorage.setVisibility(View.GONE);
        }

        // Download URL and Download button
        if (mDownloadUrl != null) {
            mTvPictureDownloadUri.setText(mDownloadUrl.toString());
            mLayoutDownload.setVisibility(View.VISIBLE);
        } else {
            mTvPictureDownloadUri.setText(null);
            mLayoutDownload.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_storage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                updateUI(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera:
                launchCamera();
                break;
            case R.id.btn_sign_in:
                signInAnonymously();
                break;
            case R.id.btn_download:
                beginDownload();
                break;
        }
    }

    private void showMessageDialog(String title, String message) {
        AlertDialog ad = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .create();
        ad.show();
    }

    private void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
