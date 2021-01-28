package com.nercita.iot.concurrent;

import com.leisurexi.concurrent.util.SleepUtils;

/**
 * @author: leisurexi
 * @date: 2020-09-02 9:16 上午
 */
public class ThreadLocalTest {

    private static final ThreadLocal<String> threadLocal = ThreadLocal.withInitial(() -> "");

    public static void main(String[] args) throws InterruptedException {
        // 线程a向 ThreadLocal 设置值，并获取
        Thread a = new Thread(() -> {
            threadLocal.set("A");
            printThreadLocalValue();
        });

        // 线程b等待线程a执行完，再获取 ThreadLocal 中的值
        Thread b = new Thread(() -> {
            // 线程睡眠5秒，等待a线程执行完
            SleepUtils.second(5);
            printThreadLocalValue();
            threadLocal.set("B");
            printThreadLocalValue();
        });
        a.start();
        a.join();
        b.start();
        b.join();

        // 主线程等待线程a和线程b执行完，获取 ThreadLocal 中的值
        printThreadLocalValue();
        threadLocal.set("main");
        printThreadLocalValue();
    }

    private static void printThreadLocalValue() {
        System.out.println(Thread.currentThread().getName() + ": " + threadLocal.get());
    }

}
