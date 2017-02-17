package com.example.chukimmuoi.firebasedemo.object;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 2/8/17.
 */

@IgnoreExtraProperties
public class Comment {

    public static String TAG = Comment.class.getSimpleName();

    public String uid;

    public String author;

    public String text;

    //Default constructor required for calls to DataSnapshot.getValue(Comment.class).
    public Comment() {

    }

    public Comment(String uid, String author, String text) {
        this.uid = uid;
        this.author = author;
        this.text = text;
    }
}
