package com.example.chukimmuoi.firebasedemo.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.chukimmuoi.firebasedemo.BaseActivity;
import com.example.chukimmuoi.firebasedemo.R;
import com.example.chukimmuoi.firebasedemo.fragment.MyPostsFragment;
import com.example.chukimmuoi.firebasedemo.fragment.MyTopPostsFragment;
import com.example.chukimmuoi.firebasedemo.fragment.RecentPostsFragment;

/**
 * @author : Hanet Electronics
 * @Skype : chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FirebaseDemo
 * Created by chukimmuoi on 2/11/17.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    private static final String TAG = FragmentAdapter.class.getSimpleName();

    private BaseActivity mContext;

    private final Fragment[] mFragments = new Fragment[]{
            new RecentPostsFragment(),
            new MyPostsFragment(),
            new MyTopPostsFragment()
    };

    private String[] mFragmentNames;

    public FragmentAdapter(Context context, FragmentManager fm) {
        super(fm);

        mContext = (BaseActivity) context;

        mFragmentNames = new String[]{
                mContext.getString(R.string.heading_recent),
                mContext.getString(R.string.heading_my_posts),
                mContext.getString(R.string.heading_my_top_posts)
        };
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentNames[position];
    }
}
