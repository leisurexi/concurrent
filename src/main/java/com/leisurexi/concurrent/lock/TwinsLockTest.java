package com.leisurexi.concurrent.lock;

import com.leisurexi.concurrent.util.SleepUtils;

import java.util.concurrent.locks.Lock;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: leisurexi
 * Date: 2019-09-23
 * Time: 21:23
 */
public class TwinsLockTest {

    static final Lock lock = new TwinsLock();

    public static void main(String[] args) {
        //启动10个线程
        for (int i = 0; i < 10; i++) {
            Worker worker = new Worker();
            worker.setDaemon(true);
            worker.start();
        }
        //每隔1秒换行
        for (int i = 0; i < 10; i++) {
            SleepUtils.second(1);
            System.out.println();
        }
    }

    static class Worker extends Thread {
        @Override
        public void run() {
            for (; ; ) {
                lock.lock();
                try {
                    SleepUtils.second(1);
                    System.out.println(Thread.currentThread().getName());
                    SleepUtils.second(1);
                } finally {
                    lock.unlock();
                }
            }
        }
    }

}
