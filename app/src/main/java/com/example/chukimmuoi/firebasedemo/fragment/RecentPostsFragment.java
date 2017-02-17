package com.example.chukimmuoi.firebasedemo.fragment;

import com.example.chukimmuoi.firebasedemo.object.Posts;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 2/11/17.
 */

public class RecentPostsFragment extends PostListFragment {

    private static final String TAG = RecentPostsFragment.class.getSimpleName();

    public RecentPostsFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child(Posts.TAG).limitToFirst(100);
    }
}
