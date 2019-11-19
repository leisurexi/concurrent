package com.leisurexi.concurrent.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
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

    //最小线程数，也叫核心线程数
    private volatile int minSize;

    //最大线程数
    private volatile int maxSize;

    //线程需要被回收的时间
    private long keepAliveTime;
    private TimeUnit timeUnit;

    //存放线程的阻塞队列
    private BlockingQueue<Runnable> workQueue;

    //存放线程池
//    private volatile Set<>


    //工作线程
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

        }
    }

    //内部存放工作线程容器，并发安全
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
