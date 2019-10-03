package com.leisurexi.concurrent.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * Created with IntelliJ IDEA.
 * Description: Semaphore示例，将Set变成有界阻塞容器。信号量的技术支持会初始化容器的最大值。add操作在向底层容器中添加一个元素
 * 之前，首先要获取一个许可。如果add操作没有添加任何元素，那么会立刻释放许可。同样，remove操作释放一个许可，使更多的元素能够
 * 添加到容器中。
 * User: leisurexi
 * Date: 2019-10-03
 * Time: 22:32
 */
public class BoundedHashSet<T> {

    private final Set<T> set;
    private final Semaphore sem;

    public BoundedHashSet(int bound) {
        this.set = Collections.synchronizedSet(new HashSet<>());
        sem = new Semaphore(bound);
    }

    public boolean add(T t) throws InterruptedException {
        sem.acquire();
        boolean wasAdded = false;
        try {
            wasAdded = set.add(t);
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
        BoundedHashSet<String> set = new BoundedHashSet<>(2);
        set.add("a");
        set.add("b");
        set.add("c"); //由于容器已满，导致当前线程阻塞
    }

}
