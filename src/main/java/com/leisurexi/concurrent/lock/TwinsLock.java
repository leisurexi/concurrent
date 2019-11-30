package com.leisurexi.concurrent.lock;

import com.leisurexi.concurrent.util.SleepUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedLongSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author: leisurexi
 * @date: 2019-11-30 12:07 下午
 * @description: 共享锁示例。该工具在同一时刻，只允许最多两个线程同时访问，超过两个线程的访问将被阻塞。
 * @since JDK 1.8
 */
@Slf4j
public class TwinsLock implements Lock {

    private final Sync sync = new Sync(2);

    private static final class Sync extends AbstractQueuedLongSynchronizer {

        public Sync(int count) {
            if (count <= 0) {
                throw new IllegalArgumentException("count must large than zero");
            }
            setState(count);
        }

        @Override
        protected long tryAcquireShared(long reduceCount) {
            for (; ; ) {
                long current = getState();
                long newCount = current - reduceCount;
                if (newCount < 0 || compareAndSetState(current, newCount)) {
                    return newCount;
                }
            }
        }

        @Override
        protected boolean tryReleaseShared(long reduceCount) {
            for (; ; ) {
                long current = getState();
                long newCount = current + reduceCount;
                if (compareAndSetState(current, newCount)) {
                    return true;
                }
            }
        }

        /**
         * 返回一个Condition，每个Condition都包含了一个Condition队列
         *
         * @return
         */
        Condition newCondition() {
            return new ConditionObject();
        }

    }


    @Override
    public void lock() {
        sync.acquireShared(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquireShared(1) > 0;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.releaseShared(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

    public static void main(String[] args) {
        Lock lock = new TwinsLock();

        class Worker extends Thread {
            @Override
            public void run() {
                while (true) {
                    lock.lock();
                    try {
                        SleepUtils.second(1);
                        log.info(Thread.currentThread().getName());
                        SleepUtils.second(1);
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }

        int threadCount = 10;
        //启动10个线程
        for (int i = 0; i < threadCount; i++) {
            Worker worker = new Worker();
            worker.setDaemon(true);
            worker.start();
        }

        //每个1秒换行
        for (int i = 0; i < threadCount; i++) {
            SleepUtils.second(1);
            log.info("");
        }

    }

}
