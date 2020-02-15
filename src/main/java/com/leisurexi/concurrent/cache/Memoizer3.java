package com.leisurexi.concurrent.cache;

import org.junit.internal.builders.NullBuilder;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author: leisurexi
 * @date: 2020-02-15 11:31
 * @description: Memoizer3的实现几乎是完美的，它有很好的并发性，若结果已经计算出来，
 * 那么将立即返回，如果其他线程正在计算结果，那么新到的线程将一直等待这个结果被计算出来。
 * 它只有一个缺陷，即仍然存在两个线程计算出相同的值的漏洞。由于compute方法中的if代码块
 * 仍然是非原子的 “先检查再执行” 操作，因此两个线程仍然有可能在同一个时间内调用compute
 * 来计算相同的值。
 * @since JDK 1.8
 */
public class Memoizer3<A, V> implements Computable<A, V> {

    private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public Memoizer3(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        Future<V> future = cache.get(arg);
        if (future == null) {
            Callable<V> eval = () -> c.compute(arg);
            FutureTask<V> futureTask = new FutureTask<>(eval);
            future = futureTask;
            cache.put(arg, futureTask);
            futureTask.run(); //在这里将调用c.compute
        }
        try {
            return future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

}
