package com.leisurexi.concurrent.test;

import java.util.concurrent.Semaphore;

/**
 * Created with IntelliJ IDEA.
 * Description: 测试有界缓存示例程序，使用Semaphore来实现缓存的有界属性和阻塞行为
 * User: leisurexi
 * Date: 2019-10-05
 * Time: 18:21
 */
public class SemaphoreBoundedBuffer<E> {

    //可以从缓存中删除的元素个数，初始值为0
    private final Semaphore availableItems;
    //可以插入到缓存的元素个数，初始值等于缓存的大小
    private final Semaphore availableSpaces;
    private final E[] items;
    private int putPosition = 0, takePosition = 0;

    public SemaphoreBoundedBuffer(int capacity) {
        if (capacity <= 0)
            throw new IllegalArgumentException();
        this.availableItems = new Semaphore(0);
        this.availableSpaces = new Semaphore(capacity);
        items = (E[]) new Object[capacity];
    }

    public boolean isEmpty() {
        return availableSpaces.availablePermits() == 0;
    }

    public boolean isFull() {
        return availableSpaces.availablePermits() == 0;
    }

    /**
     * 该操作顺序与take方法相反
     * @param x
     * @throws InterruptedException
     */
    public void put(E x) throws InterruptedException {
        availableSpaces.acquire();
        doInsert(x);
        availableItems.release();
    }

    /**
     * take操作首先从availableItems中获得一个许可。如果缓存不为空，那么这个请求会立即成功，否则请求将被阻塞知道缓存不为空。
     * 在获得一个许可后，take方法将删除缓存中的下一个元素，并返回一个许可到availableSpaces。
     * @return
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

