package com.example.chukimmuoi.firebasedemo.manager.thread.priority;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author:Hanet Electronics
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: muoick@hanet.com
 * @Website: http://hanet.com/
 * @Project: ThreadExample
 * Created by CHUKIMMUOI on 12/4/2016.
 */

//TODO: Set độ ưu tiên, thứ tự chạy thread trong hàng đợi Queue.
public class ThreadPoolExecutorPriority extends ThreadPoolExecutor {

    private static final String TAG = ThreadPoolExecutorPriority.class.getSimpleName();

    public ThreadPoolExecutorPriority(int corePoolSize,
                                      int maximumPoolSize,
                                      long keepAliveTime,
                                      TimeUnit unit,
                                      ThreadFactory threadFactory) {

        super(corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                new PriorityBlockingQueue<Runnable>(),
                threadFactory);
    }

    @Override
    public Future<?> submit(Runnable task) {
        PriorityFutureTask futureTask = new PriorityFutureTask((RunnablePriority) task);
        execute(futureTask);
        return futureTask;
    }

    @Override
    public void execute(Runnable command) {
        if (command instanceof PriorityFutureTask) {
            super.execute(command);
        } else {
            PriorityFutureTask futureTask = new PriorityFutureTask((RunnablePriority) command);
            super.execute(futureTask);
        }
    }

    private static final class PriorityFutureTask
            extends FutureTask<RunnablePriority>
            implements Comparable<PriorityFutureTask> {

        private final RunnablePriority mRunnablePriority;

        public PriorityFutureTask(RunnablePriority runnablePriority) {
            super(runnablePriority, null);
            this.mRunnablePriority = runnablePriority;
        }

        @Override
        public int compareTo(PriorityFutureTask o) {
            ThreadPriority p1 = mRunnablePriority.getPriorityThread();
            ThreadPriority p2 = o.mRunnablePriority.getPriorityThread();
            return p2.ordinal() - p1.ordinal();
        }
    }
}
