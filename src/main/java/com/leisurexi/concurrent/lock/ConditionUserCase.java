package com.leisurexi.concurrent.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: leisurexi
 * @date: 2019-11-30 2:14 下午
 * @description: Condition使用方式示例，注意需要在调用方法前获取锁
 * @since JDK 1.8
 */
public class ConditionUserCase {

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void conditionWait() {
        lock.lock();
        try {
            //当调用await()方法后，当前线程会释放锁并在此等待，而其他线程调用signal()方法后，
            //当前线程才从await()方法返回，并且在返回前已经获取了锁
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void conditionSignal() {
        lock.lock();
        try {
            //唤醒一个等待在Condition上的线程，该线程从等待方法返回前必须获得与Condition相关联的锁
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

}
