package com.leisurexi.concurrent.util;

import java.math.BigInteger;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * Description: 一个缓存计算结果的示例。利用FutureTask防止重复计算，如果有结果可用，那么直接返回结果，否则就一直阻塞，
 * 直到结果计算出来再将其返回。
 *
 * User: leisurexi
 * Date: 2019-10-04
 * Time: 12:08
 */
public class Memoizer<A, V> implements Computable<A, V> {

    private final ConcurrentHashMap<A, Future<V>> cache = new ConcurrentHashMap<>();
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
            } catch (ExecutionException e) {
                cache.remove(arg, future);
                e.printStackTrace();
            }
        }
    }

}

interface Computable<A, V> {
    V compute(A arg) throws InterruptedException;
}

class ExpensiveFunction implements Computable<String, BigInteger> {
    @Override
    public BigInteger compute(String arg) throws InterruptedException {
        SleepUtils.second(5);
        return new BigInteger(arg);
    }
}
