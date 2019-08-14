package com.chandler.red.mystock.imageloader;

/**
 * Created by Alex on 2016/8/22.
 */

import android.os.AsyncTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Alex on 2016/4/19.
 * 用于替换系统自带的AsynTask，使用自己的多线程池，执行一些比较复杂的工作，比如select photos，这里用的是缓存线程池，也可以用和cpu数相等的定长线程池以提高性能
 */
public abstract class AlxMultiTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT + 3;
    private static final int KEEP_ALIVE = 10;
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(1024);
    public static Executor mTHREAD_POOL_EXECUTOR = null;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "AlxMultiTask #" + mCount.getAndIncrement());
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        }
    };
    public void executeDependSDK(Params...params){
        if(mTHREAD_POOL_EXECUTOR==null)initThreadPool();
        super.executeOnExecutor(mTHREAD_POOL_EXECUTOR,params);
    }


    /**
     * 初始化线程池
     */
    public static void initThreadPool(){
        mTHREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
    }

}
