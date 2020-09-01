package com.leisurexi.concurrent.tool.customizetool;

/**
 * @author: leisurexi
 * @date: 2020-02-19 20:24
 * @description: BoundedBuffer使用了wait和notifyAll来实现一个有界缓存。
 * 这比使用“休眠”的有界缓存更简单，并且更高效(当缓存状态没有发生变化时，线程醒来
 * 的次数将更少)，响应性也更高(当发生特定状态变化时将立即醒来)。
 * @since JDK 1.8
 */
public class BoundedBuffer<V> extends BaseBoundedBuffer<V> {

    protected BoundedBuffer(int capacity) {
        super(capacity);
    }

    /**
     * 阻塞并直到 not-full
     */
    public synchronized void put(V v) throws InterruptedException {
        while (isFull()) {
            wait();
        }
        doPut(v);
        notifyAll();
    }

    /**
     * 阻塞并直到 not-empty
     */
    public synchronized V take() throws InterruptedException {
        while (isEmpty()) {
            wait();
        }
        V v = doTake();
        notifyAll();
        return v;
    }

}
