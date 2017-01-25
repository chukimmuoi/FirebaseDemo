package com.example.chukimmuoi.firebasedemo.object;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 1/25/17.
 */

public class GenreObject {

    private static final String TAG = GenreObject.class.getSimpleName();

    public static final String GENRE_FAVORITE = "genre_favorite";

    public static final String GENRE_PARAMS_ID   = "genre_id";
    public static final String GENRE_PARAMS_NAME = "genre_name";

    private String id;

    private String name;

    public GenreObject(String id) {
    }

    public GenreObject(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
