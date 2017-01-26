package com.example.chukimmuoi.firebasedemo.manager.thread;

import android.os.Process;
import android.util.Log;

import com.example.chukimmuoi.firebasedemo.manager.thread.priority.ThreadPoolExecutorPriority;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author:Hanet Electronics
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: muoick@hanet.com
 * @Website: http://hanet.com/
 * @Project: ThreadExample
 * Created by CHUKIMMUOI on 12/1/2016.
 */
public class ManagerThreadExecutor {

    private String TAG = ManagerThreadExecutor.class.getSimpleName();

    /**
     * Số lõi của chip để quyết định có bao nhiêu thread có thể chạy song song.
     */
    public static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private PriorityThreadFactory backgroundPriorityThreadFactory;

    /**
     * Thread chạy nền thực hiện tác vụ nặng.
     */
    private ThreadPoolExecutor mForBackgroundTasks;

    /**
     * Thread chạy nền thực hiện tác vụ nhẹ.
     */
    private ThreadPoolExecutor mForLightWeightBackgroundTasks;

    /**
     * Thread chạy Main thread UI.
     */
    private MainThreadExecutor mMainThreadExecutor;

    private static ManagerThreadExecutor ourInstance;

    public static ManagerThreadExecutor getInstance() {
        if (ourInstance == null) {
            synchronized (ManagerThreadExecutor.class) {
                ourInstance = new ManagerThreadExecutor();
            }
        }
        return ourInstance;
    }

    /**
     * 3 tasks @mForBackgroundTasks, @mForLightWeightBackgroundTasks, @mMainThreadExecutor
     * là hoàn toàn riêng biệt (có thể chạy song song vì ThreadPoolExecutor khác nhau).
     * ThreadPoolExecutor chỉ có tác dụng trong trường hợp chùng 1 tasks
     * eg: nhử ở đây: MAX THREAD = 3 -> 2 * (NUMBER_OF_CORES * 2) + 1 * (...).
     * */
    public ManagerThreadExecutor() {
        Log.e(TAG, "NUMBER_OF_CORES = " + NUMBER_OF_CORES);

        /**
         * Mức ưu tiên từ 0 -> 19.
         * THREAD_PRIORITY_BACKGROUND = 10 -> Mức trung bình.
         * */
        backgroundPriorityThreadFactory =
                new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND);

        mForBackgroundTasks = new ThreadPoolExecutorPriority(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                backgroundPriorityThreadFactory);

        mForLightWeightBackgroundTasks = new ThreadPoolExecutorPriority(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                backgroundPriorityThreadFactory
        );

        mMainThreadExecutor = new MainThreadExecutor();
    }

    public ThreadPoolExecutor forBackgroundTasks() {
        return mForBackgroundTasks;
    }

    public ThreadPoolExecutor forLightWeightBackgroundTasks() {
        return mForLightWeightBackgroundTasks;
    }

    public Executor forMainThreadTasks() {
        return mMainThreadExecutor;
    }

    public void onDestroy() {
        try {
            if (mMainThreadExecutor != null) {
                mMainThreadExecutor.onDestroy();
                mMainThreadExecutor = null;
            }

            if (mForBackgroundTasks != null) {
                mForBackgroundTasks = null;
            }

            if (mForLightWeightBackgroundTasks != null) {
                mForLightWeightBackgroundTasks = null;
            }

            if (backgroundPriorityThreadFactory != null) {
                backgroundPriorityThreadFactory.onDestroy();
                backgroundPriorityThreadFactory = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ourInstance != null) {
            ourInstance = null;
        }
    }
}
