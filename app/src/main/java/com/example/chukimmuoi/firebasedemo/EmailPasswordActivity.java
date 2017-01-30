package com.example.chukimmuoi.firebasedemo;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 1/28/17.
 */

public class EmailPasswordActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = EmailPasswordActivity.class.getSimpleName();

    private FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private TextView mTvStatus;

    private TextView mTvDetail;

    private EditText mEtEmail;

    private EditText mEtPassword;

    private Button mBtnSignIn;

    private Button mBtnSignOut;

    private Button mBtnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);

        createUI();

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.e(TAG, "onAuthStateChanged: user login = " + user.getUid());
                } else {
                    Log.e(TAG, "onAuthStateChanged: user logout");
                }

                updateUI(user);
            }
        };
    }

    private void createUI() {
        mTvStatus   = (TextView) findViewById(R.id.tv_status);
        mTvDetail   = (TextView) findViewById(R.id.tv_detail);
        mEtEmail    = (EditText) findViewById(R.id.et_email);
        mEtPassword = (EditText) findViewById(R.id.et_password);

        mBtnSignIn        = (Button) findViewById(R.id.btn_email_sign_in);
        mBtnSignOut       = (Button) findViewById(R.id.btn_sign_out);
        mBtnCreateAccount = (Button) findViewById(R.id.btn_email_create_account);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mFirebaseAuth != null) {
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
    protected void onDestroy() {
        super.onDestroy();
    }

    private void createAccount(String email, String password) {

        Log.e(TAG, "email: " + email + ", password: " + password);

        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        boolean isSuccessful = task.isSuccessful();
                        Log.e(TAG, "createAccount: signInWithEmailAndPassword: isSuccessful = " + isSuccessful);

                        if (!isSuccessful) {
                            Toast.makeText(EmailPasswordActivity.this,
                                    "createAccount: signInWithEmailAndPassword: Auth fail",
                                    Toast.LENGTH_LONG).show();
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void signIn(String email, String password) {

        Log.e(TAG, "email: " + email + ", password: " + password);

        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        boolean isSuccessful = task.isSuccessful();
                        Log.e(TAG, "signIn: signInWithEmailAndPassword: isSuccessful = " + isSuccessful);

                        if (!isSuccessful) {
                            Log.e(TAG, "signIn: failed" + task.getException());
                            Toast.makeText(EmailPasswordActivity.this,
                                    "signIn: signInWithEmailAndPassword: Auth fail",
                                    Toast.LENGTH_LONG).show();
                        }

                        if (!isSuccessful) {
                            mTvStatus.setText(getString(R.string.auth_failed));
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void signOut() {
        mFirebaseAuth.signOut();
        updateUI(null);
    }

    private boolean validateForm() {
        boolean output = true;

        String email = mEtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEtEmail.setError("Required");
            output = false;
        } else {
            mEtEmail.setError(null);
        }

        String password = mEtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mEtPassword.setError("Required");
            output = false;
        } else {
            mEtPassword.setError(null);
        }

        return output;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            mTvStatus.setText(getString(R.string.emailpassword_status_fmt, user.getEmail()));
            mTvDetail.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            mEtPassword.setVisibility(View.GONE);
            mBtnSignOut.setVisibility(View.VISIBLE);
        } else {
            mTvStatus.setText(getString(R.string.sign_out));
            mTvDetail.setText(null);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            mEtPassword.setVisibility(View.VISIBLE);
            mBtnSignOut.setVisibility(View.GONE);
        }
    }

    private void getCurrentUser(){
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        if(firebaseUser != null) {
            String name  = firebaseUser.getDisplayName();
            String email = firebaseUser.getEmail();
            Uri photoUrl = firebaseUser.getPhotoUrl();
            Log.e(TAG, "name: " + name + "\nemail: " + email + "\nphotoUrl: " + photoUrl);

            boolean emailVerified = firebaseUser.isEmailVerified();
            Log.e(TAG, "emailVerified: " + emailVerified);

            //Id - duy nhat.
            String uid = firebaseUser.getUid();
            Log.e(TAG, "uid: " + uid);

            Toast.makeText(EmailPasswordActivity.this, "name: " + name
                    + "\nemail: " + email
                    + "\nphotoUrl: " + photoUrl
                    + "\nemailVerified: " + emailVerified
                    + "\nuid: " + uid, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_email_sign_in:
                signIn(mEtEmail.getText().toString(), mEtPassword.getText().toString());
                break;
            case R.id.btn_email_create_account:
                createAccount(mEtEmail.getText().toString(), mEtPassword.getText().toString());
                break;
            case R.id.btn_sign_out:
                signOut();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_email_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_get_current_user:
                getCurrentUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
