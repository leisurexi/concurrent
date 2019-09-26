package com.leisurexi.concurrent.lock;

/**
 * Created with IntelliJ IDEA.
 * Description: 死锁演示
 * User: leisurexi
 * Date: 2019-09-11
 * Time: 20:45
 */
public class DeadLockDemo {

    private static String A = "A";
    private static String B = "B";

    public static void main(String[] args) {
        new DeadLockDemo().deadLock();
    }

    private void deadLock() {
        Thread t1 = new Thread(() -> {
            synchronized (A) {
                try {
                    Thread.currentThread().sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (B) {
                    System.out.println(1);
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (B) {
                synchronized (A) {
                    System.out.println(2);
                }
            }
        });

        t1.start();
        t2.start();
    }

}
