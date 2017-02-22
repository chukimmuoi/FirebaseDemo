package com.example.chukimmuoi.firebasedemo;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 2/5/17.
 */

@Keep
public class AnonymousAuthActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = AnonymousAuthActivity.class.getSimpleName();

    private TextView mTvStatusId;

    private TextView mTvStatusEmail;

    private Button mBtnSignIn;

    private Button mBtnSignOut;

    private Button mBtnLinkAccount;

    private EditText mEtEmail;

    private EditText mEtPassword;

    private FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_auth);

        createUI();

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser != null) {
                    Log.e(TAG, "onCreate: onAuthStateChanged: Users login: " + firebaseUser.getUid());
                } else {
                    Log.e(TAG, "onCreate: onAuthStateChanged: Users logout: ");

                }

                updateUI(firebaseUser);
            }
        };
    }

    private void createUI() {
        mTvStatusId = (TextView) findViewById(R.id.tv_anonymous_status_id);
        mTvStatusEmail = (TextView) findViewById(R.id.tv_anonymous_status_email);

        mBtnSignIn = (Button) findViewById(R.id.btn_anonymous_sign_in);
        mBtnSignOut = (Button) findViewById(R.id.btn_anonymous_sign_out);
        mBtnLinkAccount = (Button) findViewById(R.id.btn_link_account);

        mEtEmail = (EditText) findViewById(R.id.et_field_email);
        mEtPassword = (EditText) findViewById(R.id.et_field_password);
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

    private void signInAnonymously() {
        showProgressDialog();

        mFirebaseAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                boolean isSuccessful = task.isSuccessful();

                Log.e(TAG, "Sign In Anonymously: onComplete: isSuccessful = "
                        + String.valueOf(isSuccessful));

                if (!isSuccessful) {
                    Log.e(TAG, "Sign In Anonymously: onComplete: error", task.getException());
                    Toast.makeText(AnonymousAuthActivity.this, "Sign in fail.", Toast.LENGTH_LONG).show();
                }

                hideProgressDialog();
            }
        });
    }

    private void signOut() {
        mFirebaseAuth.signOut();

        updateUI(null);
    }

    private void linkAccount() {
        if (!validateLinkForm()) {
            return;
        }

        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        showProgressDialog();

        mFirebaseAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        boolean isSuccessful = task.isSuccessful();

                        Log.e(TAG, "Link Account: onComplete: isSuccessful = "
                                + String.valueOf(isSuccessful));

                        if (!isSuccessful) {
                            Toast.makeText(AnonymousAuthActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }

                        hideProgressDialog();
                    }
                });
    }

    private boolean validateLinkForm() {
        boolean valid = true;

        String email = mEtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEtEmail.setError("Required");
            valid = false;
        } else {
            mEtEmail.setError(null);
        }

        String password = mEtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mEtPassword.setError("Required");
            valid = false;
        } else {
            mEtPassword.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser firebaseUser) {
        hideProgressDialog();

        if (firebaseUser != null) {
            mTvStatusId.setText(getString(R.string.id_fmt, firebaseUser.getUid()));
            mTvStatusEmail.setText(getString(R.string.email_fmt, firebaseUser.getEmail()));
        } else {
            mTvStatusId.setText(R.string.sign_out);
            mTvStatusEmail.setText(null);
        }

        mBtnSignIn.setEnabled(firebaseUser == null);
        mBtnSignOut.setEnabled(firebaseUser != null);
        mBtnLinkAccount.setEnabled(firebaseUser != null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_anonymous_sign_in:
                signInAnonymously();
                break;
            case R.id.btn_anonymous_sign_out:
                signOut();
                break;
            case R.id.btn_link_account:
                linkAccount();
                break;
        }
    }
}
