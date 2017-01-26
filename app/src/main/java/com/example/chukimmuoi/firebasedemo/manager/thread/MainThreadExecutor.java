package com.example.chukimmuoi.firebasedemo.manager.thread;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * @author:Hanet Electronics
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: muoick@hanet.com
 * @Website: http://hanet.com/
 * @Project: ThreadExample
 * Created by CHUKIMMUOI on 12/1/2016.
 */
//TODO: Cho phép CHẠY NGẦM đồng thời UPDATE, CAN THIỆP lên giao diện(UI).
public class MainThreadExecutor implements Executor {

    private String TAG = MainThreadExecutor.class.getSimpleName();

    /**
     * Set Main Looper của Main Thread UI cho Handler
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(Runnable command) {
        mHandler.post(command);
    }

    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
