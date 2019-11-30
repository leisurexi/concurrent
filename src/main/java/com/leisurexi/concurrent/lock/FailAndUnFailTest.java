package com.leisurexi.concurrent.lock;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: leisurexi
 * @date: 2019-11-30 12:35 下午
 * @description:
 * @since JDK 1.8
 */
@Slf4j
public class FailAndUnFailTest {

    private static MyReentrantLock fairLock = new MyReentrantLock(true);
    private static MyReentrantLock unFairLock = new MyReentrantLock(false);

    @Test
    public void fair() {
        testLock(fairLock);
    }

    @Test
    public void unFair() {
        testLock(unFairLock);
    }

    private void testLock(MyReentrantLock lock) {
        int threadCount = 5;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Job(lock, countDownLatch);
            threads[i].start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class Job extends Thread {
        private MyReentrantLock lock;
        private CountDownLatch countDownLatch;
        public Job(MyReentrantLock lock, CountDownLatch countDownLatch) {
            this.lock = lock;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            lock.lock();
            try {
                log.info(Thread.currentThread().getName());
                log.info(Arrays.toString(lock.getQueuedThreads().toArray()));
            } finally {
                lock.unlock();
            }
            lock.lock();
            try {
                log.info(Thread.currentThread().getName());
                log.info(Arrays.toString(lock.getQueuedThreads().toArray()));
            } finally {
                lock.unlock();
                countDownLatch.countDown();
            }
        }
    }

    private static class MyReentrantLock extends ReentrantLock {
        public MyReentrantLock(boolean fair) {
            super(fair);
        }

        @Override
        protected Collection<Thread> getQueuedThreads() {
            //由于列表是逆序输出的，为了方便观察结果，将其进行反转
            List<Thread> threadList = new ArrayList<>(super.getQueuedThreads());
            Collections.reverse(threadList);
            return threadList;
        }
    }

}
