package com.leisurexi.concurrent.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author: leisurexi
 * @date: 2019-11-30 1:42 下午
 * @description: 一个缓存示例，展示读写锁的使用方式。
 * Cache组合一个非线程安全的HashMap作为缓存的实现，同时使用读写锁的读锁和写锁来保证Cache是线程安全的。
 * 在读操作get()方法中，需要获取读锁，这使得并发访问时不会被阻塞。写操作put()方法和clear()方法，在更新
 * HashMap时必须提前获取写锁，当获取写锁后，其他线程对于读锁和写锁的获取均被阻塞，而只有写锁被释放之后，
 * 其他读写操作才能继续。Cache使用读写锁提升读操作的并发性，也保证每次写操作对所有的读写操作的可见性。
 * @since JDK 1.8
 */
public class Cache {

    private static Map<String, Object> map = new HashMap<>();
    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static Lock readLock = readWriteLock.readLock();
    private static Lock writeLock = readWriteLock.writeLock();

    /**
     * 获取一个key对应的value
     *
     * @param key
     * @return
     */
    public static final Object get(String key) {
        readLock.lock();
        try {
            return map.get(key);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 设置key对应的value，并返回旧的value
     *
     * @param key
     * @param value
     * @return
     */
    public static final Object put(String key, Object value) {
        writeLock.lock();
        try {
            return map.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 情况所有内容
     */
    public static final void clear() {
        writeLock.lock();
        try {
            map.clear();
        } finally {
            writeLock.unlock();
        }
    }

}
