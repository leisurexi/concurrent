package com.leisurexi.concurrent.tool;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * @author: leisurexi
 * @date: 2020-02-15 10:48
 * @description: Semaphore示例，将Set变成有界阻塞容器。信号量的计数值会初始化容器的最大值。add操作在向底层容器中添加一个元素
 * 之前，首先要获取一个许可。如果add操作没有添加任何元素，那么会立刻释放许可。同样，remove操作释放一个许可，使更多的元素能够添加到容器中。
 * @since JDK 1.8
 */
public class BoundedHashSet<T> {

    private final Set<T> set;
    private final Semaphore sem;

    public BoundedHashSet(int bound) {
        this.set = Collections.synchronizedSet(new HashSet<>());
        this.sem = new Semaphore(bound);
    }

    public boolean add(T o) throws InterruptedException {
        sem.acquire();
        boolean wasAdded = false;
        try {
            wasAdded = set.add(o);
            return wasAdded;
        } finally {
            if (!wasAdded) {
                sem.release();
            }
        }
    }

    public boolean remove(Object o) {
        boolean wasRemoved = set.remove(o);
        if (wasRemoved) {
            sem.release();
        }
        return wasRemoved;
    }

    public static void main(String[] args) throws InterruptedException {
        BoundedHashSet<Integer> set = new BoundedHashSet<>(2);
        set.add(1);
        set.add(2);
        set.add(3);  //由于容器已满，导致当前线程阻塞
    }

}
