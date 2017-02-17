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
public class Users {

    public static final String TAG = Users.class.getSimpleName();

    public String username;

    public String email;

    //Default constructor required for calls to DataSnapshot.getValue(Users.class).
    public Users() {

    }

    public Users(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
