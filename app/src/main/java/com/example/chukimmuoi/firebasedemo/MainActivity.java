package com.example.chukimmuoi.firebasedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.chukimmuoi.firebasedemo.object.GenreObject;
import com.example.chukimmuoi.firebasedemo.object.SongObject;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);

        genreUserProperty(new GenreObject("09yu6", "Nhac tre"));

        songAnalytics("a9675h", "Mai Mai Mot Tinh Yeu", "Quang Vinh", "unknown...", true);
    }

    private void songAnalytics(String id, String name, String singer, String author, boolean isActive) {
        Bundle bundle = new Bundle();
        bundle.putString(SongObject.SONG_PARAMS_ID, id);
        bundle.putString(SongObject.SONG_PARAMS_NAME, name);
        bundle.putString(SongObject.SONG_PARAMS_SINGER, singer);
        bundle.putString(SongObject.SONG_PARAMS_AUTHOR, author);
        bundle.putBoolean(SongObject.SONG_PARAMS_ACTIVE, isActive);

        mFirebaseAnalytics.logEvent(SongObject.SONG_EVENT_CHOOSE, bundle);
    }

    private void genreUserProperty(GenreObject genre){
        mFirebaseAnalytics.setUserProperty(GenreObject.GENRE_FAVORITE, genre.getName());
    }
}
