package com.example.chukimmuoi.firebasedemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.chukimmuoi.firebasedemo.BaseActivity;
import com.example.chukimmuoi.firebasedemo.PostDetailActivity;
import com.example.chukimmuoi.firebasedemo.R;
import com.example.chukimmuoi.firebasedemo.constants.FirebaseConstants;
import com.example.chukimmuoi.firebasedemo.object.Posts;
import com.example.chukimmuoi.firebasedemo.viewholder.PostViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 2/9/17.
 */

public class PostAdapter extends FirebaseRecyclerAdapter<Posts, PostViewHolder> {

    private static final String TAG = PostAdapter.class.getSimpleName();

    private BaseActivity mContext;

    private DatabaseReference mDatabaseReference;

    public PostAdapter(Class<Posts> modelClass, int modelLayout,
                       Class<PostViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    public PostAdapter(Class<Posts> modelClass, int modelLayout,
                       Class<PostViewHolder> viewHolderClass, Query ref,
                       Context context, DatabaseReference databaseReference) {
        super(modelClass, modelLayout, viewHolderClass, ref);

        mContext = (BaseActivity) context;

        mDatabaseReference = databaseReference;
    }


    @Override
    protected void populateViewHolder(PostViewHolder viewHolder, final Posts model, final int position) {
        final DatabaseReference postRef = getRef(position);

        final String postKey = postRef.getKey();

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                mContext.startActivity(intent);
            }
        });

        if (model.stars.containsKey(mContext.getUid())) {
            viewHolder.getStarView().setImageResource(R.drawable.ic_toggle_star_24);
        } else {
            viewHolder.getStarView().setImageResource(R.drawable.ic_toggle_star_outline_24);

        }

        viewHolder.bindToPost(model, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference globalPostRef
                        = mDatabaseReference.child(Posts.TAG).child(postRef.getKey());
                DatabaseReference userPostRef
                        = mDatabaseReference.child(FirebaseConstants.RELATIONSHIP_USER_POST)
                        .child(model.uid)
                        .child(postRef.getKey());

                onStarClicked(globalPostRef);
                onStarClicked(userPostRef);
            }
        });

    }

    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Posts posts = mutableData.getValue(Posts.class);
                if (posts == null) {
                    return Transaction.success(mutableData);
                }
                if (posts.stars.containsKey(mContext.getUid())) {
                    posts.starCount = posts.starCount - 1;
                    posts.stars.remove(mContext.getUid());
                } else {
                    posts.starCount = posts.starCount + 1;
                    posts.stars.put(mContext.getUid(), true);
                }

                mutableData.setValue(posts);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.e(TAG, "onStarClicked: onComplete: " + databaseError);
            }
        });
    }
}
