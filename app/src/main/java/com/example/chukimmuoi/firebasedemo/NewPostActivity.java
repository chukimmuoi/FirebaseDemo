package com.example.chukimmuoi.firebasedemo;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chukimmuoi.firebasedemo.constants.FirebaseConstants;
import com.example.chukimmuoi.firebasedemo.object.Posts;
import com.example.chukimmuoi.firebasedemo.object.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
public class NewPostActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = NewPostActivity.class.getSimpleName();

    private DatabaseReference mDatabaseReference;

    private EditText mEtTitleField;

    private EditText mEtBodyField;

    private FloatingActionButton mBtnSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        createUI();
    }

    private void createUI() {
        mEtTitleField = (EditText) findViewById(R.id.field_title);

        mEtBodyField = (EditText) findViewById(R.id.field_body);

        mBtnSubmit = (FloatingActionButton) findViewById(R.id.fab_submit_post);
        mBtnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_submit_post:
                submitPost();
                break;
        }
    }

    private void submitPost() {
        final String title = mEtTitleField.getText().toString();
        final String body = mEtBodyField.getText().toString();

        if (TextUtils.isEmpty(title)) {
            mEtTitleField.setError("Required");
            return;
        }

        if (TextUtils.isEmpty(body)) {
            mEtBodyField.setError("Required");
            return;
        }

        setEditingEnabled(false);
        Toast.makeText(this, "Posting ...", Toast.LENGTH_LONG).show();

        final String userId = getUid();
        mDatabaseReference.child(Users.TAG).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e(TAG, "Get users: onDataChange: key = " + dataSnapshot.getKey());

                        Users users = dataSnapshot.getValue(Users.class);

                        if (users == null) {
                            Log.e(TAG, "Users " + userId + " is unexpectedly null.");
                            Toast.makeText(NewPostActivity.this, "Error: could not fetch users.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            writeNewPost(userId, users.username, title, body);
                        }

                        setEditingEnabled(true);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Get user: on cancelled.", databaseError.toException());

                        setEditingEnabled(true);
                    }
                });
    }

    private void setEditingEnabled(boolean enabled) {
        mEtTitleField.setEnabled(enabled);
        mEtBodyField.setEnabled(enabled);
        if (enabled) {
            mBtnSubmit.setVisibility(View.VISIBLE);
        } else {
            mBtnSubmit.setVisibility(View.GONE);
        }
    }

    private void writeNewPost(String userId, String userName, String title, String body) {
        String key = mDatabaseReference.child(Posts.TAG).push().getKey();
        Posts posts = new Posts(userId, userName, title, body);
        Map<String, Object> postValues = posts.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(File.separator + Posts.TAG +
                        File.separator + key,
                postValues);
        childUpdates.put(File.separator + FirebaseConstants.RELATIONSHIP_USER_POST +
                        File.separator + userId +
                        File.separator + key,
                postValues);

        mDatabaseReference.updateChildren(childUpdates);
    }
}
