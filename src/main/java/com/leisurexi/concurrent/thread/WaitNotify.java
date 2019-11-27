package com.leisurexi.concurrent.thread;

import cn.hutool.core.date.DateUtil;
import com.leisurexi.concurrent.util.SleepUtils;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: leisurexi
 * @date: 2019-11-27 9:00 下午
 * @description: 创建了两个线程————WaitThread和NotifyThread，前者检查flag值是否为false，如果符合要求，进行后续操作，
 * 否则在lock上等待，后者在睡眠了一段时间后对lock进行通知。
 * @since JDK 1.8
 */
@Slf4j
public class WaitNotify {

    /**
     * 等待/通知经典范式，该范式分为两部分，分别针对等待方（消费者）和通知方（生产者）。
     * 等待方遵循如下原则。
     * 1.获取对象的锁。
     * 2.如果条件不满足，那么调用对象的wait()方法，被通知后仍要检查条件。
     * 3.条件满足时执行对应的逻辑。
     * 通知方遵循如下原则。
     * 1.获得对象的锁。
     * 2.改变条件。
     * 3.通知所有等待在对象上的线程。
     */

    static boolean flag = true;
    static Object lock = new Object();

    public static void main(String[] args) {
        Thread waitThread = new Thread(new Wait(), "WaitThread");
        waitThread.start();
        SleepUtils.second(1);
        Thread notifyThread = new Thread(new Notify(), "NotifyThread");
        notifyThread.start();
    }

    static class Wait implements Runnable {
        @Override
        public void run() {
            //加锁，拥有lock的Monitor
            synchronized (lock) {
                //当条件不满足时，继续wait，同时释放了lock的锁
                while (flag) {
                    try {
                        log.info(Thread.currentThread() + " flag is true. wait @ " + DateUtil.now());
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //条件满足时，完成工作
                log.info(Thread.currentThread() + " flag is false. running @ " + DateUtil.now());
            }
        }
    }

    static class Notify implements Runnable {
        @Override
        public void run() {
            //加锁，拥有lock的Monitor
            synchronized (lock) {
                //获取lock的锁，然后进行通知，通知时不会释放lock的锁,
                //直到当前线程释放了lock后，WaitThread才能从wait方法中返回
                log.info(Thread.currentThread() + " hold lock. notify @ " + DateUtil.now());
                lock.notifyAll();
                flag = false;
                SleepUtils.second(5);
            }
            //再次加锁
            synchronized (lock) {
                log.info(Thread.currentThread() + " hold lock again. sleep @ " + DateUtil.now());
                SleepUtils.second(5);
            }
        }
    }

}
