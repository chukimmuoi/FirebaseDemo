package com.example.chukimmuoi.firebasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 1/31/17.
 */

@Keep
public class GoogleSignInActivity extends BaseActivity
        implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GoogleSignInActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 9001;

    private TextView mTvStatus;

    private TextView mTvDetail;

    private SignInButton mBtnSignIn;

    private Button mBtnSignOut;

    private Button mBtnDisconnect;

    private FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);

        createUI();

        GoogleSignInOptions googleSignInOptions
                = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser != null) {
                    Log.e(TAG, "On Auth State Changed: user login: " + firebaseUser.getUid());
                } else {
                    Log.e(TAG, "On Auth State Changed: user logout");
                }

                updateUI(firebaseUser);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mFirebaseAuth != null) {
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mFirebaseAuth != null) {
            if (mAuthStateListener != null) {
                mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
            }
        }
    }

    private void createUI() {
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mTvDetail = (TextView) findViewById(R.id.tv_detail);

        mBtnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        mBtnSignOut = (Button) findViewById(R.id.btn_sign_out);
        mBtnDisconnect = (Button) findViewById(R.id.btn_disconnect);

        mBtnSignIn.setOnClickListener(this);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mFirebaseAuth.signOut();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Log.e(TAG, "Sign In: Status: " + status);

                updateUI(null);
            }
        });
    }

    private void revokeAccess() {
        mFirebaseAuth.signOut();

        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Log.e(TAG, "Revoke Access: Status: " + status);

                updateUI(null);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.e(TAG, "Firebase Auth With Google: account: " + account.getId());

        showProgressDialog();

        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        boolean isSuccessful = task.isSuccessful();

                        Log.e(TAG, "Firebase Auth With Google: Sign In With Credential "
                                + String.valueOf(isSuccessful));

                        if (!isSuccessful) {
                            Log.e(TAG, "Firebase Auth With Google: Sign In With Credential",
                                    task.getException());
                            Toast.makeText(GoogleSignInActivity.this, "Authentication failed",
                                    Toast.LENGTH_LONG).show();
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void updateUI(FirebaseUser firebaseUser) {
        hideProgressDialog();

        if (firebaseUser != null) {
            mTvStatus.setText(getString(R.string.google_status_fmt, firebaseUser.getEmail()));
            mTvDetail.setText(getString(R.string.firebase_status_fmt, firebaseUser.getUid()));

            mBtnSignIn.setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);

        } else {
            mTvStatus.setText(getString(R.string.sign_out));
            mTvDetail.setText(null);

            mBtnSignIn.setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "on Connection Failed: " + connectionResult);

        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                signIn();
                break;
            case R.id.btn_sign_out:
                signOut();
                break;
            case R.id.btn_disconnect:
                revokeAccess();
                break;
        }
    }
}
