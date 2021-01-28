package com.nercita.iot.concurrent;

import java.util.concurrent.Semaphore;

/**
 * @author: leisurexi
 * @date: 2020-02-16 22:31
 * @description:
 * @since JDK 1.8
 */
public class BoundedBuffer<E> {

    /** 可以从容器中删除的元素个数 */
    private final Semaphore availableItems;
    /** 可以插入到容器中的元素个数 */
    private final Semaphore availableSpaces;
    private final E[] items;
    private int putPosition = 0, takePosition = 0;

    public BoundedBuffer(int capacity) {
        this.availableItems = new Semaphore(0);
        this.availableSpaces = new Semaphore(capacity);
        items = (E[]) new Object[capacity];
    }

    public boolean isEmpty() {
        return availableItems.availablePermits() == 0;
    }

    public boolean isFull() {
        return availableSpaces.availablePermits() == 0;
    }

    /**
     * 基于 Semaphore 实现当 items 满时阻塞线程
     * @param x
     * @throws InterruptedException
     */
    public void put(E x) throws InterruptedException {
        availableSpaces.acquire();
        doInsert(x);
        availableItems.release();
    }

    /**
     * 基于 Semaphore 实现当 items 为空时阻塞线程
     * @throws InterruptedException
     */
    public E take() throws InterruptedException {
        availableItems.acquire();
        E item = doExtract();
        availableSpaces.release();
        return item;
    }

    private synchronized void doInsert(E x) {
        int i = putPosition;
        items[i] = x;
        putPosition = (++i == items.length) ? 0 : i;
    }

    private synchronized E doExtract() {
        int i = takePosition;
        E x = items[i];
        items[i] = null;
        takePosition = (++i == items.length) ? 0 : i;
        return x;
    }

}
