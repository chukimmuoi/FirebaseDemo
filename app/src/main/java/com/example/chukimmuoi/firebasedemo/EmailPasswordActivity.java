package com.example.chukimmuoi.firebasedemo;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Keep;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 1/28/17.
 */

@Keep
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

    private Button mBtnUpdateProfile;

    private Button mBtnUpdateEmail;

    private Button mBtnSendEmailVerification;

    private Button mBtnUpdatePassword;

    private Button mBtnSendPasswordResetEmail;

    private Button mBtnDeleteUser;

    private Button mBtnReAuthenticate;

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
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mTvDetail = (TextView) findViewById(R.id.tv_detail);
        mEtEmail = (EditText) findViewById(R.id.et_email);
        mEtPassword = (EditText) findViewById(R.id.et_password);

        mBtnSignIn = (Button) findViewById(R.id.btn_email_sign_in);
        mBtnSignOut = (Button) findViewById(R.id.btn_sign_out);
        mBtnCreateAccount = (Button) findViewById(R.id.btn_email_create_account);
        mBtnUpdateProfile = (Button) findViewById(R.id.btn_update_profile);
        mBtnUpdateEmail = (Button) findViewById(R.id.btn_update_email);
        mBtnSendEmailVerification = (Button) findViewById(R.id.btn_send_email_verification);
        mBtnUpdatePassword = (Button) findViewById(R.id.btn_update_password);
        mBtnSendPasswordResetEmail = (Button) findViewById(R.id.btn_send_password_reset_email);
        mBtnDeleteUser = (Button) findViewById(R.id.btn_delete_user);
        mBtnReAuthenticate = (Button) findViewById(R.id.btn_re_authenticate_user);
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
                        Log.e(TAG, "createAccount: signInWithEmailAndPassword: isSuccessful = "
                                + isSuccessful);

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
                        Log.e(TAG, "signIn: signInWithEmailAndPassword: isSuccessful = "
                                + isSuccessful);

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
            mBtnUpdateProfile.setVisibility(View.VISIBLE);
            mBtnUpdateEmail.setVisibility(View.VISIBLE);
            mBtnSendEmailVerification.setVisibility(View.VISIBLE);
            mBtnUpdatePassword.setVisibility(View.VISIBLE);
            mBtnSendPasswordResetEmail.setVisibility(View.VISIBLE);
            mBtnDeleteUser.setVisibility(View.VISIBLE);
            mBtnReAuthenticate.setVisibility(View.VISIBLE);
        } else {
            mTvStatus.setText(getString(R.string.sign_out));
            mTvDetail.setText(null);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            mEtPassword.setVisibility(View.VISIBLE);
            mBtnSignOut.setVisibility(View.GONE);
            mBtnUpdateProfile.setVisibility(View.GONE);
            mBtnUpdateEmail.setVisibility(View.GONE);
            mBtnSendEmailVerification.setVisibility(View.GONE);
            mBtnUpdatePassword.setVisibility(View.GONE);
            mBtnSendPasswordResetEmail.setVisibility(View.GONE);
            mBtnDeleteUser.setVisibility(View.GONE);
            mBtnReAuthenticate.setVisibility(View.GONE);
        }
    }

    private void getCurrentUser() {
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            String name = firebaseUser.getDisplayName();
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

    private void getProviderData() {
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            for (UserInfo info : firebaseUser.getProviderData()) {
                String providerId = info.getProviderId();

                String uid = firebaseUser.getUid();

                String name = firebaseUser.getDisplayName();
                String email = firebaseUser.getEmail();
                Uri photoUrl = firebaseUser.getPhotoUrl();

                Log.e(TAG, "name: " + name
                        + "\nemail: " + email
                        + "\nphotoUrl: " + photoUrl
                        + "\nproviderId: " + providerId
                        + "\nuid: " + uid);
            }
        }
    }

    private void updateProfile(String displayName, String uriPhoto) {
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(Uri.parse(uriPhoto))
                .build();

        if (firebaseUser != null) {
            firebaseUser.updateProfile(profileUpdate).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            boolean isSuccessful = task.isSuccessful();
                            Log.e(TAG, "Update profile: isSuccessful: " + isSuccessful);
                        }
                    });
        }
    }

    private void updateEmail(String email) {
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            boolean isSuccessful = task.isSuccessful();
                            Log.e(TAG, "Update email " + String.valueOf(isSuccessful));
                        }
                    });
        }
    }

    private void updatePassword(String newPassword) {
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            boolean isSuccessful = task.isSuccessful();
                            Log.e(TAG, "Update password " + String.valueOf(isSuccessful));
                        }
                    });
        }
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            boolean isSuccessful = task.isSuccessful();
                            Log.e(TAG, "Send Email Verification " + String.valueOf(isSuccessful));
                        }
                    });
        }
    }

    private void sendPasswordResetEmail(String email) {
        mFirebaseAuth.sendPasswordResetEmail(email).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        boolean isSuccessful = task.isSuccessful();
                        Log.e(TAG, "Send Password Reset Email " + String.valueOf(isSuccessful));
                    }
                });
    }

    private void deleteUser() {
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    boolean isSuccessful = task.isSuccessful();
                    Log.e(TAG, "Delete user " + String.valueOf(isSuccessful));
                }
            });
        }
    }

    private void reAuthenticateUser(String email, String password) {
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);

        if (firebaseUser != null) {
            firebaseUser.reauthenticate(authCredential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            boolean isSuccessful = task.isSuccessful();
                            Log.e(TAG, "Re authenticate user  " + String.valueOf(isSuccessful));
                        }
                    });
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
            case R.id.btn_update_profile:
                updateProfile("Chu Kim Muoi 01010/11", "https://example.com/jane-q-user/profile.jpg");
                break;
            case R.id.btn_update_email:
                updateEmail("muoi01010/11@gmail.com");
                break;
            case R.id.btn_send_email_verification:
                sendEmailVerification();
                break;
            case R.id.btn_update_password:
                updatePassword("123456");
                break;
            case R.id.btn_send_password_reset_email:
                sendPasswordResetEmail("chukimmuoi@gmail.com");
                break;
            case R.id.btn_delete_user:
                deleteUser();
                break;
            case R.id.btn_re_authenticate_user:
                reAuthenticateUser("chukimmuoi@gmail.com", "123456");
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
        switch (item.getItemId()) {
            case R.id.menu_get_current_user:
                getCurrentUser();
                return true;
            case R.id.menu_get_provider_data:
                getProviderData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
