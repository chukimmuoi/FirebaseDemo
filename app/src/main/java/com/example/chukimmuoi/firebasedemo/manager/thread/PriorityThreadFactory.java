package com.example.chukimmuoi.firebasedemo.manager.thread;


import android.os.Process;

import java.util.concurrent.ThreadFactory;

/**
 * @author:Hanet Electronics
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: muoick@hanet.com
 * @Website: http://hanet.com/
 * @Project: ThreadExample
 * Created by CHUKIMMUOI on 11/30/2016.
 */
//TODO: Thiết lập độ ƯU TIÊN.
public class PriorityThreadFactory implements ThreadFactory {

    private static final String TAG = PriorityThreadFactory.class.getSimpleName();

    private final int mThreadPriority;

    private Thread mNewThread;

    public PriorityThreadFactory(int mThreadPriority) {
        this.mThreadPriority = mThreadPriority;
    }

    @Override
    public Thread newThread(final Runnable r) {
        Runnable wrapperRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Process.setThreadPriority(mThreadPriority);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                r.run();
            }
        };

        mNewThread = new Thread(wrapperRunnable);

        return mNewThread;
    }

    public void onDestroy() {
        try {
            if (mNewThread != null) {
                mNewThread.interrupt();
                mNewThread = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
