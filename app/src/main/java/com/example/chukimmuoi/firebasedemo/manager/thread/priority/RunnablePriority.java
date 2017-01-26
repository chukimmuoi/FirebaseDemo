package com.example.chukimmuoi.firebasedemo.manager.thread.priority;

/**
 * @author:Hanet Electronics
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: muoick@hanet.com
 * @Website: http://hanet.com/
 * @Project: ThreadExample
 * Created by CHUKIMMUOI on 12/4/2016.
 */
public abstract class RunnablePriority implements Runnable {

    private static final String TAG = RunnablePriority.class.getSimpleName();

    private final ThreadPriority mThreadPriority;

    public RunnablePriority() {
        this.mThreadPriority = ThreadPriority.MEDIUM;
    }

    public RunnablePriority(ThreadPriority mThreadPriority) {
        this.mThreadPriority = mThreadPriority;
    }

    public ThreadPriority getPriorityThread() {
        return mThreadPriority;
    }
}
