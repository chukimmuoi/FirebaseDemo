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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 2/4/17.
 */

@Keep
public class FacebookLoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = FacebookLoginActivity.class.getSimpleName();

    private TextView mTvStatus;

    private TextView mTvDetail;

    private LoginButton mBtnFacebookLogin;

    private Button mBtnFacebookSignOut;

    private FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_facebook);

        createUI();

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

        mCallbackManager = CallbackManager.Factory.create();
        mBtnFacebookLogin.setReadPermissions("email", "public_profile");
        mBtnFacebookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "Facebook: onSuccess: loginResult = " + loginResult);

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "Facebook: onCancel");

                updateUI(null);
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Facebook: onError", error);

                updateUI(null);
            }
        });
    }

    private void createUI() {
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mTvDetail = (TextView) findViewById(R.id.tv_detail);

        mBtnFacebookLogin = (LoginButton) findViewById(R.id.btn_facebook_login);
        mBtnFacebookSignOut = (Button) findViewById(R.id.btn_facebook_sign_out);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.e(TAG, "Handle Facebook Access Token: token = " + token);

        showProgressDialog();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                boolean isSuccessful = task.isSuccessful();

                Log.e(TAG, "Handle Facebook Access Token: Sign In With Credential: isSuccessful = "
                        + String.valueOf(isSuccessful));

                if (!isSuccessful) {
                    Log.e(TAG, "Handle Facebook Access Token: Sign In With Credential: error",
                            task.getException());
                    Toast.makeText(FacebookLoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_LONG).show();
                }

                hideProgressDialog();
            }
        });

    }

    private void signOut() {
        mFirebaseAuth.signOut();
        LoginManager.getInstance().logOut();

        updateUI(null);
    }

    private void updateUI(FirebaseUser firebaseUser) {
        hideProgressDialog();
        if (firebaseUser != null) {
            mTvStatus.setText(getString(R.string.facebook_status_fmt, firebaseUser.getDisplayName()));
            mTvDetail.setText(getString(R.string.firebase_status_fmt, firebaseUser.getUid()));

            mBtnFacebookLogin.setVisibility(View.GONE);
            mBtnFacebookSignOut.setVisibility(View.VISIBLE);
        } else {
            mTvStatus.setText(R.string.sign_out);
            mTvDetail.setText(null);

            mBtnFacebookLogin.setVisibility(View.VISIBLE);
            mBtnFacebookSignOut.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_facebook_login:
                break;
            case R.id.btn_facebook_sign_out:
                signOut();
                break;
        }
    }
}
