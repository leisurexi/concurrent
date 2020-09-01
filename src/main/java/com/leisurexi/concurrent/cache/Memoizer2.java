package com.leisurexi.concurrent.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: leisurexi
 * @date: 2020-02-15 11:24
 * @description: Mempozer2相比Memoizer1用ConcurrentHashMap代替了HashMap有了
 * 更好的并发行为，但它在作为缓存时仍然存在一些不足——如果某个线程启动了一个开销很大的计算，
 * 而其他线程并不知道这个计算正在进行，那么很可能会重复这个计算。
 * @since JDK 1.8
 */
public class Memoizer2<A, V> implements Computable<A, V> {

    private final Map<A, V> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public Memoizer2(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        V result = cache.get(arg);
        if (result == null) {
            result = c.compute(arg);
            cache.put(arg, result);
        }
        return result;
    }


}
