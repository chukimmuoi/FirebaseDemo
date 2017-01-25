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

public class SongObject {

    private static final String TAG = SongObject.class.getSimpleName();

    public static final String SONG_EVENT_CHOOSE = "choose_song";

    public static final String SONG_PARAMS_ID     = "song_id";
    public static final String SONG_PARAMS_NAME   = "song_name";
    public static final String SONG_PARAMS_SINGER = "song_singer";
    public static final String SONG_PARAMS_AUTHOR = "song_author";
    public static final String SONG_PARAMS_ACTIVE = "song_active";

    private String id;

    private String name;

    private String singer;

    private String author;

    private boolean isActive;

    public SongObject() {

    }

    public SongObject(String id, String name, String singer, String author) {
        this.id = id;
        this.name = name;
        this.singer = singer;
        this.author = author;
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

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
