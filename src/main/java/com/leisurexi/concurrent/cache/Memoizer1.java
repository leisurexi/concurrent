package com.leisurexi.concurrent.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: leisurexi
 * @date: 2020-02-15 11:09
 * @description: 使用HashMap来保存之前计算的结果。compute方法将首先检查需要的结果是否已经在缓存中，
 * 如果存在则返回之前计算的值。否则，将把计算结果缓存在HashMap中，然后再返回。
 * HashMap不是线程安全的，因此要确保两个线程不会同时访问HashMap，Memoizer1采用了一种保守的方法，即
 * 对整个compute方法进行同步。这种方法能确保线程安全性，但会带来一个明显的可伸缩性问题：每次只有一个线程
 * 能够执行compute。如果另一个线程正在计算结果，那么其他调用compute的线程可能被阻塞很长时间。如果有多个
 * 线程在排队等待还未计算出的结果，那么compute方法的计算时间可能比没有 “缓存” 操作的计算时间更长。
 * @since JDK 1.8
 */
public class Memoizer1<A, V> implements Computable<A, V> {

    private final Map<A, V> cache = new HashMap<>();
    private final Computable<A, V> c;

    public Memoizer1(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public synchronized V compute(A arg) throws InterruptedException {
        V result = cache.get(arg);
        if (result == null) {
            result = c.compute(arg);
            cache.put(arg, result);
        }
        return result;
    }

}
