package com.example.chukimmuoi.firebasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.chukimmuoi.firebasedemo.object.GenreObject;
import com.example.chukimmuoi.firebasedemo.object.SongObject;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createAnalytics();
    }

    private void createAnalytics() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);

        genreUserProperty(new GenreObject("09yu6", "Nhac tre"));

        songAnalytics("a9675h", "Mai Mai Mot Tinh Yeu", "Quang Vinh", "unknown...", true);
    }

    private void songAnalytics(String id, String name, String singer,
                               String author, boolean isActive) {
        Bundle bundle = new Bundle();
        bundle.putString(SongObject.SONG_PARAMS_ID, id);
        bundle.putString(SongObject.SONG_PARAMS_NAME, name);
        bundle.putString(SongObject.SONG_PARAMS_SINGER, singer);
        bundle.putString(SongObject.SONG_PARAMS_AUTHOR, author);
        bundle.putBoolean(SongObject.SONG_PARAMS_ACTIVE, isActive);

        mFirebaseAnalytics.logEvent(SongObject.SONG_EVENT_CHOOSE, bundle);
    }

    private void genreUserProperty(GenreObject genre) {
        mFirebaseAnalytics.setUserProperty(GenreObject.GENRE_FAVORITE, genre.getName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_subscribe:
                FirebaseMessaging.getInstance().subscribeToTopic("news");

                String messageSubscribe = "Message Subscribe";
                Log.d(TAG, messageSubscribe);
                Toast.makeText(MainActivity.this, messageSubscribe, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_token:
                String token = FirebaseInstanceId.getInstance().getToken();

                String messageToken = "Message Token " + token;
                Log.d(TAG, messageToken);
                Toast.makeText(MainActivity.this, messageToken, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_email_password:
                Intent intentEmailPassword = new Intent(MainActivity.this, EmailPasswordActivity.class);
                startActivity(intentEmailPassword);
                return true;
            case R.id.menu_google_sign_in:
                Intent intentGoogleSign = new Intent(MainActivity.this, GoogleSignInActivity.class);
                startActivity(intentGoogleSign);
                return true;
            case R.id.menu_facebook_sign_in:
                Intent intentFacebookSign = new Intent(MainActivity.this, FacebookLoginActivity.class);
                startActivity(intentFacebookSign);
                return true;
            case R.id.menu_anonymous_sign_in:
                Intent intentAnonymousSign = new Intent(MainActivity.this, AnonymousAuthActivity.class);
                startActivity(intentAnonymousSign);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
