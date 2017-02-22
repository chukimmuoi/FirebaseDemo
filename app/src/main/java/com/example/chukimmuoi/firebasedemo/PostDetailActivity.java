package com.example.chukimmuoi.firebasedemo;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chukimmuoi.firebasedemo.adapter.CommentAdapter;
import com.example.chukimmuoi.firebasedemo.constants.FirebaseConstants;
import com.example.chukimmuoi.firebasedemo.object.Comment;
import com.example.chukimmuoi.firebasedemo.object.Posts;
import com.example.chukimmuoi.firebasedemo.object.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 2/10/17.
 */

@Keep
public class PostDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = PostDetailActivity.class.getSimpleName();

    public static final String EXTRA_POST_KEY = "post_key";

    private TextView mTvAuthor;

    private TextView mTvTitle;

    private TextView mTvBody;

    private EditText mEtCommentField;

    private Button mBtnComment;

    private RecyclerView mRvComment;

    private String mPostKey;

    private DatabaseReference mPostReference;

    private DatabaseReference mCommentReference;

    private ValueEventListener mPostListener;

    private CommentAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        createUI();

        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mPostKey == null) throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");

        mPostReference = FirebaseDatabase.getInstance()
                .getReference()
                .child(Posts.TAG)
                .child(mPostKey);
        mCommentReference = FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseConstants.RELATIONSHIP_POST_COMMENT)
                .child(mPostKey);
    }

    private void createUI() {
        mTvAuthor = (TextView) findViewById(R.id.post_author);

        mTvTitle = (TextView) findViewById(R.id.post_title);

        mTvBody = (TextView) findViewById(R.id.post_body);

        mEtCommentField = (EditText) findViewById(R.id.field_comment_text);

        mBtnComment = (Button) findViewById(R.id.button_post_comment);
        mBtnComment.setOnClickListener(this);

        mRvComment = (RecyclerView) findViewById(R.id.recycler_comments);
        mRvComment.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Posts posts = dataSnapshot.getValue(Posts.class);
                mTvAuthor.setText(posts.author);
                mTvTitle.setText(posts.title);
                mTvBody.setText(posts.body);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Value Event Listener: onCancelled", databaseError.toException());

                Toast.makeText(PostDetailActivity.this, "Fail to load post", Toast.LENGTH_LONG)
                        .show();
            }
        };

        mPostListener = postListener;
        mPostReference.addValueEventListener(mPostListener);

        mAdapter = new CommentAdapter(this, mCommentReference);
        mRvComment.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener);
        }

        if (mAdapter != null) {
            mAdapter.cleanupListener();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_post_comment:
                postComment();
                break;
        }
    }

    private void postComment() {
        final String uid = getUid();
        //TODO: Read data once, not change.
        FirebaseDatabase.getInstance().getReference().child(Users.TAG).child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e(TAG, "Posts comment: onDataChange: " + dataSnapshot.getKey());

                        Users users = dataSnapshot.getValue(Users.class);
                        String authorName = users.username;

                        String commentText = mEtCommentField.getText().toString();
                        Comment comment = new Comment(uid, authorName, commentText);

                        mCommentReference.push().setValue(comment);

                        mEtCommentField.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Posts comment: onCancelled", databaseError.toException());

                        Toast.makeText(PostDetailActivity.this, "Fail to listener", Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }
}
