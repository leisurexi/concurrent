package com.leisurexi.concurrent.threadpool;

import com.leisurexi.concurrent.util.SleepUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * Description: 扩展ThreadPoolExecutor，统计每个任务的平均处理时间
 * 无论任务是从run中正常返回，还是抛出一个异常而返回，afterExecute都会被调用。(如果任务完成后带有一个Error，那么就不会调用afterExecute)
 * 如果beforeExecute抛出一个RuntimeException，那么任务将不会被执行，并且afterExecute也不会被调用。
 * 在线程池完成关闭操作时调用terminated，也就是在所有任务都已经完成并且所有工作者线程也已经关闭后。terminated可以用来释放
 * Executor在其生命周期里分配的各种资源，此外还可以执行发送通知、记录日志或者手机finalize统计信息等操作。
 * User: leisurexi
 * Date: 2019-10-04
 * Time: 19:59
 */
public class TimingThreadPool extends ThreadPoolExecutor {

    public TimingThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    //每个任务在哪个工作线程中执行不确定，所以把开始时间保存在ThreadLocal
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();
    private final AtomicLong numTasks = new AtomicLong();
    private final AtomicLong totalTime = new AtomicLong();

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        System.out.println(String.format("Thread %s: start %s", t, r));
        startTime.set(System.nanoTime());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        try {
            long endTime = System.nanoTime();
            long taskTime = endTime - startTime.get();
            numTasks.incrementAndGet();
            totalTime.addAndGet(taskTime);
            System.out.println(String.format("Thread %s: end %s, time=%dns", t, r, taskTime));
        } finally {
            super.afterExecute(r, t);
        }
    }

    @Override
    protected void terminated() {
        try {
            System.out.println(String.format("Terminated: avg time=%dns", totalTime.get() / numTasks.get()));
        } finally {
            super.terminated();
        }
    }

    public static void main(String[] args) {
        int nThreads = Runtime.getRuntime().availableProcessors();
        TimingThreadPool threadPool = new TimingThreadPool(nThreads, nThreads, 0, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<>(100));
        threadPool.execute(() -> {
            throw new RuntimeException();
        });
        threadPool.execute(() -> SleepUtils.second(10));
        threadPool.shutdown();
    }

}
