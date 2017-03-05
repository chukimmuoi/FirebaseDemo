package com.example.chukimmuoi.firebasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chukimmuoi.firebasedemo.object.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 2/11/17.
 */

@Keep
public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = SignInActivity.class.getSimpleName();

    private DatabaseReference mDatabaseReference;

    private FirebaseAuth mFirebaseAuth;

    private EditText mEtEmailField;

    private EditText mEtPasswordField;

    private Button mBtnSignIn;

    private Button mBtnSignUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseAuth = FirebaseAuth.getInstance();

        createUI();
    }

    private void createUI() {
        mEtEmailField = (EditText) findViewById(R.id.field_email);

        mEtPasswordField = (EditText) findViewById(R.id.field_password);

        mBtnSignIn = (Button) findViewById(R.id.btn_sign_in);
        mBtnSignIn.setOnClickListener(this);

        mBtnSignUp = (Button) findViewById(R.id.button_sign_up);
        mBtnSignUp.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mFirebaseAuth.getCurrentUser() != null) {
            onAuthSuccess(mFirebaseAuth.getCurrentUser());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                signIn();
                break;
            case R.id.button_sign_up:
                signUp();
                break;
        }
    }

    private void onAuthSuccess(FirebaseUser firebaseUser) {
        String username = usernameFromEmail(firebaseUser.getEmail());

        writeNewUser(firebaseUser.getUid(), username, firebaseUser.getEmail());

        startActivity(new Intent(SignInActivity.this, DatabaseActivity.class));
        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private void writeNewUser(String userId, String name, String email) {
        Users users = new Users(name, email);

        mDatabaseReference.child(Users.TAG).child(userId).setValue(users);
    }

    private void signIn() {
        Log.e(TAG, "Sing In");

        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        String email = mEtEmailField.getText().toString();
        String password = mEtPasswordField.getText().toString();

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        boolean isSuccessful = task.isSuccessful();

                        Log.e(TAG, "Sign in: onComplete: isSuccessful = "
                                + String.valueOf(isSuccessful));

                        if (isSuccessful) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign in fail.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void signUp() {
        Log.d(TAG, "Sign Up");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEtEmailField.getText().toString();
        String password = mEtPasswordField.getText().toString();

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        boolean isSuccessful = task.isSuccessful();

                        Log.d(TAG, "Sign up: onComplete: isSuccessful = "
                                + String.valueOf(isSuccessful));

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign up failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }
                });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEtEmailField.getText().toString())) {
            mEtEmailField.setError("Required");
            result = false;
        } else {
            mEtEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mEtPasswordField.getText().toString())) {
            mEtPasswordField.setError("Required");
            result = false;
        } else {
            mEtPasswordField.setError(null);
        }

        return result;
    }
}
