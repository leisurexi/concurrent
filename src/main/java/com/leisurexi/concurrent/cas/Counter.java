package com.leisurexi.concurrent.cas;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: leisurexi
 * @date: 2019-11-23 12:58 下午
 * @description: 一个基于CAS线程安全的计数器方法safeCount和一个非线程安全的计数器count
 * @since JDK 1.8
 */
@Slf4j
public class Counter {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    private int i = 0;

    public static void main(String[] args) {
        Counter counter = new Counter();
        List<Thread> threads = new ArrayList<>(600);
        long start = System.currentTimeMillis();
        for (int j = 0; j < 100; j++) {
            Thread thread = new Thread(() -> {
                for (int i = 0; i < 10000; i++) {
                    counter.safeCount();
                    counter.count();
                }
            });
            threads.add(thread);
        }
        threads.forEach(thread -> thread.start());
        //等待所有完成
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        log.info("非CAS计数器: {}", counter.i);
        log.info("CAS计数器: {}", counter.atomicInteger.get());
        log.info("所用时间: {}(毫秒)", System.currentTimeMillis() - start);
    }

    /**
     * 使用CAS实现线程安全计数器
     */
    private void safeCount() {
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
