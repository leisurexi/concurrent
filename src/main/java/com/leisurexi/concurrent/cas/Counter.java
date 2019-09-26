package com.leisurexi.concurrent.cas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * Description: 一个基于CAS线程安全的计数器方法safeCount和一个非线程安全的计数器count
 * User: leisurexi
 * Date: 2019-09-11
 * Time: 21:46
 */
public class Counter {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    private int i = 0;

    private static CountDownLatch countDownLatch = new CountDownLatch(100);

    public static void main(String[] args) throws InterruptedException {
        final Counter counter = new Counter();
        List<Thread> threads = new ArrayList<>(600);
        long start = System.currentTimeMillis();
        for (int j = 0; j < 100; j++) {
            Thread thread = new Thread(() -> {
                for (int i = 0; i < 10000; i++) {
                    counter.safeCount();
                    counter.count();
                }
                countDownLatch.countDown();
            });
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
        }

        countDownLatch.await();
        System.out.println(counter.i);
        System.out.println(counter.atomicInteger.get());
        System.out.println(System.currentTimeMillis() - start);
    }

    /**
     * 使用CAS实现线程安全计数器
     */
    private void safeCount() {
//        atomicInteger.incrementAndGet();
        for (; ; ) {
            int i = atomicInteger.get();
            boolean result = atomicInteger.compareAndSet(i, ++i);
            if (result) {
                break;
            }
        }
    }

    /**
     * 非线程安全计数器
     */
    private void count() {
        i++;
    }

}
