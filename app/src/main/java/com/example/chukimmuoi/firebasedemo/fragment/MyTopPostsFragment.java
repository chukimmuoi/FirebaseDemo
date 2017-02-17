package com.example.chukimmuoi.firebasedemo.fragment;

import com.example.chukimmuoi.firebasedemo.constants.FirebaseConstants;
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

public class MyTopPostsFragment extends PostListFragment{

    private static final String TAG = MyTopPostsFragment.class.getSimpleName();

    public MyTopPostsFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child(FirebaseConstants.RELATIONSHIP_USER_POST).
                child(getUid()).
                orderByChild("starCount");
    }
}
