package com.leisurexi.concurrent.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * Description: 自定义线程池
 * User: leisurexi
 * Date: 2019-11-19
 * Time: 10:22 下午
 */
@Slf4j
public class CustomThreadPool {

    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 最小线程数，也叫核心线程数
     */
    private volatile int minSize;

    /**
     * 最大线程数
     */
    private volatile int maxSize;

    /**
     * 线程需要被回收的时间
     */
    private long keepAliveTime;

    private TimeUnit timeUnit;

    /**
     * 存放线程的阻塞队列
     */
    private BlockingQueue<Runnable> workQueue;

    /**
     * 存放线程池
     */
    private volatile Set<Worker> workers;

    /**
     * 是否关闭线程池标志
     */
    private AtomicBoolean isShutDown = new AtomicBoolean(false);

    /**
     * 提交到线程池中的任务总数
     */
    private AtomicInteger totalTask = new AtomicInteger();

    /**
     * 线程池任务全部执行完毕后的通知组件
     */
    private Object shutDownNotify = new Object();

    private Notify notify;

    /**
     * @param miniSize      最小线程数
     * @param maxSize       最大线程数
     * @param keepAliveTime 线程保活时间
     * @param unit
     * @param workQueue     阻塞队列
     * @param notify        通知接口
     */
    public CustomThreadPool(int miniSize, int maxSize, long keepAliveTime,
                            TimeUnit unit, BlockingQueue<Runnable> workQueue, Notify notify) {
        this.minSize = miniSize;
        this.maxSize = maxSize;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = unit;
        this.workQueue = workQueue;
        this.notify = notify;

        workers = new ConcurrentHashSet<>();
    }

    /**
     * 有返回值
     *
     * @param callable
     * @param <T>
     * @return
     */
    public <T> Future<T> submit(Callable<T> callable) {
        FutureTask<T> futureTask = new FutureTask<>(callable);
        execute(futureTask);
        return futureTask;
    }

    /**
     * 执行任务
     *
     * @param runnable 需要执行的任务
     */
    public void execute(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("runnable must not be empty");
        }
        if (isShutDown.get()) {
            log.info("线程池已经关闭，不能再提交任务!");
            return;
        }
        //提交的线程 计数
        totalTask.incrementAndGet();
        //小于最小线程数时新建线程
        if (workers.size() < minSize) {
            addWorker(runnable);
            return;
        }

        boolean offer = workQueue.offer(runnable);
        //写入队列失败
        if (!offer) {
            //创建新的线程执行
            if (workers.size() < maxSize) {
                addWorker(runnable);
                return;
            } else {
                log.error("超过最大线程数");
                try {
                    //会阻塞
                    workQueue.put(runnable);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 添加任务，需要加锁
     *
     * @param runnable
     */
    private void addWorker(Runnable runnable) {
        Worker worker = new Worker(runnable, true);
        worker.startTask();
        workers.add(worker);
    }

    /**
     * 从队列中获取任务
     */
    private Runnable getTask() {
        //关闭标识及任务是否全部完成
        if (isShutDown.get() && totalTask.get() == 0) {
            return null;
        }
        lock.lock();

        try {
            Runnable task = null;
            if (workers.size() > minSize) {
                //大于核心线程数时需要用保活时间获取任务
                task = workQueue.poll(keepAliveTime, timeUnit);
            } else {
                task = workQueue.take();
            }
            if (task != null) {
                return task;
            }
        } catch (InterruptedException e) {
            return null;
        } finally {
            lock.unlock();
        }
        return null;
    }

    /**
     * 任务执行完毕后关闭线程池
     */
    public void shutDown() {
        isShutDown.set(true);
        tryClose(true);
    }

    /**
     * 立即关闭线程池，会造成任务丢失
     */
    public void shutDownNow() {
        isShutDown.set(true);
        tryClose(false);
    }

    /**
     * 阻塞等待队列执行完毕
     */
    public void mainNotify() {
        synchronized (shutDownNotify) {
            while (totalTask.get() > 0) {
                try {
                    shutDownNotify.wait();
                    if (notify != null) {
                        notify.notifyListen();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    /**
     * 关闭线程池
     *
     * @param isTry true  尝试关闭      --> 会等待所有任务执行完毕
     *              false 立即关闭线程池 --> 任务有丢失的可能
     */
    private void tryClose(boolean isTry) {
        if (!isTry) {
            closeAllTask();
        } else {
            if (isShutDown.get() && totalTask.get() == 0) {
                closeAllTask();
            }
        }
    }

    /**
     * 关闭所有任务
     */
    private void closeAllTask() {
        for (Worker worker : workers) {
            worker.close();
        }
    }

    /**
     * 获取工作线程数量
     */
    public int getWorkerCount() {
        return workers.size();
    }

    /**
     * 工作线程
     */
    private final class Worker extends Thread {

        private Runnable task;

        private Thread thread;

        /**
         * true  -->  创建新的线程执行
         * false -->  从队列里获取线程执行
         */
        private boolean isNewTask;

        public Worker(Runnable task, boolean isNewTask) {
            this.task = task;
            this.isNewTask = isNewTask;
            thread = this;
        }

        public void startTask() {
            thread.start();
        }

        public void close() {
            thread.interrupt();
        }

        @Override
        public void run() {
            Runnable task = null;
            if (isNewTask) {
                task = this.task;
            }
            boolean compile = true;
            try {
                while ((task != null || (task = getTask()) != null)) {
                    try {
                        //执行任务
                        task.run();
                    } catch (Exception e) {
                        compile = false;
                        throw e;
                    } finally {
                        //任务执行完毕
                        task = null;
                        int number = totalTask.decrementAndGet();
                        if (number == 0) {
                            synchronized (shutDownNotify) {
                                shutDownNotify.notify();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //释放线程
                workers.remove(this);
                if (!compile) {
                    addWorker(null);
                }
                tryClose(true);
            }
        }
    }

    /**
     * 内部存放工作线程容器，并发安全
     *
     * @param <T>
     */
    private final class ConcurrentHashSet<T> extends AbstractSet<T> {

        private ConcurrentHashMap<T, Object> map = new ConcurrentHashMap<>();
        private final Object PRESENT = new Object();

        private AtomicInteger count = new AtomicInteger();

        @Override
        public boolean add(T t) {
            count.incrementAndGet();
            return map.put(t, PRESENT) == null;
        }

        @Override
        public boolean remove(Object o) {
            count.decrementAndGet();
            return map.remove(o) == PRESENT;
        }

        @Override
        public Iterator<T> iterator() {
            return map.keySet().iterator();
        }

        @Override
        public int size() {
            return count.get();
        }
    }

}
