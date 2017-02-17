package com.example.chukimmuoi.firebasedemo.object;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

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
public class Posts {

    public static final String TAG = Posts.class.getSimpleName();

    public String uid;

    public String author;

    public String title;

    public String body;

    public int starCount = 0;

    public Map<String, Boolean> stars = new HashMap<>();

    //Default constructor required for calls to DataSnapshot.getValue(Posts.class).
    public Posts() {

    }

    public Posts(String uid, String author, String title, String body) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("starCount", starCount);
        result.put("stars", stars);

        return result;
    }
}
