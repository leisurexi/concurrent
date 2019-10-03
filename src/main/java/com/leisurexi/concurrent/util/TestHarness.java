package com.leisurexi.concurrent.util;

import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * Description: CountDownLatch示例，用于统计多个线程的执行时间
 * User: leisurexi
 * Date: 2019-10-03
 * Time: 21:41
 */
public class TestHarness {

    public long timeTasks(int nThreads, final Runnable task) throws InterruptedException {
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(nThreads);
        for (int i = 0; i < nThreads; i++) {
             Thread thread = new Thread(() -> {
                 try {
                     startGate.await();
                     try {
                         task.run();
                     } finally {
                         endGate.countDown();
                     }
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
             });
            thread.start();
        }
        long start = System.currentTimeMillis();
        startGate.countDown();
        endGate.await();
        long end = System.currentTimeMillis();
        return end - start;
    }

    public static void main(String[] args) throws InterruptedException {
        TestHarness harness = new TestHarness();
        long timeTasks = harness.timeTasks(10, () -> {
            SleepUtils.second(1);
        });
        System.out.println(timeTasks);
    }

}
