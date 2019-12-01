package com.leisurexi.concurrent.lock;

import com.leisurexi.concurrent.util.SleepUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: leisurexi
 * @date: 2019-11-30 2:21 下午
 * @description: 用Condition实现有界队列。有界队列时一种特殊的队列，当队列为空时，队列的获取
 * 操作将会阻塞当前线程，直到队列中有新增元素，当队列已满时，队列的插入操作将会阻塞插入线程，直接队列出现"空位"。
 * @since JDK 1.8
 */
@Slf4j
public class BoundedQueue<T> {

    private Object[] items;
    /**
     * 添加的下标，删除的下标和数组当前数量
     */
    private int addIndex, removeIndex, count;
    private ReentrantLock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();
    private Condition notFull = lock.newCondition();

    public BoundedQueue(int capacity) {
        this.items = new Object[capacity];
    }

    /**
     * 添加一个元素，如果数组满，则添加的线程进入等待状态，直到有"空位"
     *
     * @param t
     * @throws InterruptedException
     */
    public void add(T t) throws InterruptedException {
        lock.lock();
        try {
            while (count == items.length) {
                log.info("容器已满，添加线程:[{}]进入等待状态", Thread.currentThread().getName());
                notFull.await();
            }
            log.info("添加线程:[{}]添加数据", Thread.currentThread().getName());
            items[addIndex] = t;
            if (++addIndex == items.length) {
                addIndex = 0;
            }
            ++count;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 由头部删除一个元素，如果数组为空，则删除线程进入等待状态，直到有新添加元素
     *
     * @return
     * @throws InterruptedException
     */
    public T remove() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                log.info("容器已空，删除线程:[{}]进入等待状态", Thread.currentThread().getName());
                notEmpty.await();
            }
            Object item = items[removeIndex];
            if (++removeIndex == items.length) {
                removeIndex = 0;
            }
            --count;
            notFull.signal();
            return (T) item;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        BoundedQueue<Integer> queue = new BoundedQueue<>(2);
        int threadCount = 10;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            Thread thread = new Thread(() -> {
                try {
                    queue.add(finalI);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
            thread.start();
        }
        Thread thread = new Thread(() -> {
            try {
                for (; ; ) {
                    Integer number = queue.remove();
                    log.info("取出的数据: {}", number);
                    SleepUtils.second(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
