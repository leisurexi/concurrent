package com.leisurexi.concurrent.tool.customizetool;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: leisurexi
 * @date: 2020-02-19 21:13
 * @description: 使用两个Condition，分别为notFull和notEmpty，用于表示“非满”和“非空”
 * 两个条件谓词。当缓存为空时，take将阻塞并等待notEmpty，此时put向notEmpty发送信号，可以
 * 解除任何在take中阻塞的线程。
 * @since JDK 1.8
 */
public class ConditionBoundedBuffer<T> {

    protected final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    private final T[] items;
    private int tail, head, count;

    public ConditionBoundedBuffer(int capacity) {
        this.items = (T[]) new Object[capacity];
    }

    /**
     * 阻塞并直到 notFull
     */
    public void put(T x) throws InterruptedException {
        lock.lock();
        try {
            while (count == items.length) {
                notFull.await();
            }
            items[tail] = x;
            if (++tail == items.length) {
                tail = 0;
            }
            ++count;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 阻塞并直到 notEmpty
     */
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            T x = items[head];
            items[head] = null;
            if (++head == items.length) {
                head = 0;
            }
            --count;
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }

}
