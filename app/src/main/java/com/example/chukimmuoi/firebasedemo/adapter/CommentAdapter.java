package com.example.chukimmuoi.firebasedemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chukimmuoi.firebasedemo.R;
import com.example.chukimmuoi.firebasedemo.object.Comment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 2/10/17.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private static final String TAG = CommentAdapter.class.getSimpleName();

    private Context mContext;

    private DatabaseReference mDatabaseReference;

    private ChildEventListener mChildEventListener;

    private List<String> mCommentIds = new ArrayList<>();

    private List<Comment> mComments = new ArrayList<>();

    public CommentAdapter(Context context, DatabaseReference databaseReference) {
        mContext = context;
        mDatabaseReference = databaseReference;

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "ChildEventListener: onChildAdded: " + dataSnapshot.getKey());

                Comment comment = dataSnapshot.getValue(Comment.class);

                mCommentIds.add(dataSnapshot.getKey());
                mComments.add(comment);
                notifyItemInserted(mComments.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "ChildEventListener: onChildChanged: " + dataSnapshot.getKey());

                Comment newComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                int commentIndex = mCommentIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    mComments.set(commentIndex, newComment);

                    notifyItemChanged(commentIndex);
                } else {
                    Log.e(TAG, "ChildEventListener: onChildChanged: unknown_child: " + commentKey);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e(TAG, "ChildEventListener: onChildRemoved: " + dataSnapshot.getKey());

                String commentKey = dataSnapshot.getKey();

                int commentIndex = mCommentIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    mCommentIds.remove(commentIndex);
                    mComments.remove(commentIndex);

                    notifyItemRemoved(commentIndex);
                } else {
                    Log.e(TAG, "ChildEventListener: onChildRemoved: unknown_child: " + commentKey);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "ChildEventListener: onChildMoved: " + dataSnapshot.getKey());

                Comment movedComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "ChildEventListener: onCancelled", databaseError.toException());

                Toast.makeText(mContext, "Fail to load comment", Toast.LENGTH_LONG).show();
            }
        };

        mChildEventListener = childEventListener;
        mDatabaseReference.addChildEventListener(mChildEventListener);

    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = mComments.get(position);
        holder.tvAuthor.setText(comment.author);
        holder.tvBody.setText(comment.text);
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
        }
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView tvAuthor;

        public TextView tvBody;

        public CommentViewHolder(View itemView) {
            super(itemView);

            tvAuthor = (TextView) itemView.findViewById(R.id.comment_author);

            tvBody = (TextView) itemView.findViewById(R.id.comment_body);
        }
    }
}
