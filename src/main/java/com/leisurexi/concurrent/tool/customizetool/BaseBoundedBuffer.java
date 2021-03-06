package com.leisurexi.concurrent.tool.customizetool;

/**
 * @author: leisurexi
 * @date: 2020-02-19 19:26
 * @description: 有界缓存实现的基类，在这个类中实现了一个基于数组的循环缓存，其中各个缓存状态变量(buf、head、tail和count)
 * 均由缓存的内置锁来保护。它还提供了同步的doPut和doTake方法，并在子类中通过这些方法来实现put和take操作，底层的状态将对
 * 子类隐藏。
 * @since JDK 1.8
 */
public abstract class BaseBoundedBuffer<V> {

    private final V[] buf;
    private int tail;
    private int head;
    private int count;

    protected BaseBoundedBuffer(int capacity) {
        this.buf = (V[]) new Object[capacity];
    }

    protected synchronized final void doPut(V v) {
        buf[tail] = v;
        if (++tail == buf.length) {
            tail = 0;
        }
        ++count;
    }

    protected synchronized final V doTake() {
        V v = buf[head];
        buf[head] = null;
        if (++head == buf.length) {
            head = 0;
        }
        --count;
        return v;
    }

    public synchronized final boolean isFull() {
        return count == buf.length;
    }

    public synchronized final boolean isEmpty() {
        return count == 0;
    }

}
