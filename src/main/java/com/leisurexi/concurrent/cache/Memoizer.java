package com.leisurexi.concurrent.cache;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author: leisurexi
 * @date: 2020-02-15 12:00
 * @description: Memoizer3中存在这个重复计算结果的原因是，复核操作 “若没有则添加” 不具备原子性，
 * Memoizer使用了ConcurrentMap中的原子方法putIfAbsent，避免了Memoizer3中的漏洞。
 * @since JDK 1.8
 */
public class Memoizer<A, V> implements Computable<A, V> {

    private final ConcurrentMap<A, Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public Memoizer(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        while (true) {
            Future<V> future = cache.get(arg);
            if (future == null) {
                Callable<V> eval = () -> c.compute(arg);
                FutureTask<V> futureTask = new FutureTask<>(eval);
                future = cache.putIfAbsent(arg, futureTask);
                if (future == null) {
                    future = futureTask;
                    futureTask.run();
                }
            }

            try {
                return future.get();
            } catch (CancellationException e) {
                cache.remove(arg, future);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
