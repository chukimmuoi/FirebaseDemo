package com.example.chukimmuoi.firebasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.chukimmuoi.firebasedemo.adapter.FragmentAdapter;
import com.google.firebase.auth.FirebaseAuth;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 2/8/17.
 */

@Keep
public class DatabaseActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = DatabaseActivity.class.getSimpleName();

    private TabLayout mTabLayout;

    private ViewPager mViewPager;

    private FragmentPagerAdapter mPagerAdapter;

    private FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        createUI();

    }

    private void createUI() {
        mPagerAdapter = new FragmentAdapter(DatabaseActivity.this, getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.vp_view_pager);
        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tl_tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab_new_post);
        mFloatingActionButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_database, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();

                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_new_post:
                startActivity(new Intent(DatabaseActivity.this, NewPostActivity.class));
                break;
        }
    }
}
