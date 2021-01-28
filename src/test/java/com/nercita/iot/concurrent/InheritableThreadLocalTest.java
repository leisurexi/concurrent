package com.nercita.iot.concurrent;

/**
 * @author: leisurexi
 * @date: 2020-09-02 4:58 下午
 */
public class InheritableThreadLocalTest {

    private static final ThreadLocal<String> threadLocal = new InheritableThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {
        threadLocal.set("main");
        printThreadLocalValue();

        Thread a = new Thread(() -> {
            printThreadLocalValue();
            threadLocal.set("a");
            printThreadLocalValue();
        });
        a.start();
        a.join();

        printThreadLocalValue();
    }

    private static void printThreadLocalValue() {
        System.out.println(Thread.currentThread().getName() + ": " + threadLocal.get());
    }

}
