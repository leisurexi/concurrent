package com.leisurexi.concurrent.tool.customizetool;

import com.leisurexi.concurrent.util.SleepUtils;

/**
 * @author: leisurexi
 * @date: 2020-02-19 19:49
 * @description: SleepyBoundedBuffer尝试通过put和take方法来实现一种简单的“轮询与休眠”重试机制，
 * 从而使调用者无须在每次调用时都实现重试逻辑。如果缓存为空，那么take将休眠并直到另一个线程在缓存中放
 * 入一些数据；如果缓存是满的，那么put将休眠并直到另一个线程从缓存中移除一些数据，以便有空间容纳新的数据。
 * @since JDK 1.8
 */
public class SleepyBoundedBuffer<V> extends BaseBoundedBuffer<V> {

    protected SleepyBoundedBuffer(int capacity) {
        super(capacity);
    }

    public void put(V v) throws InterruptedException {
        while (true) {
            synchronized (this) {
                if (!isFull()) {
                    doPut(v);
                    return;
                }
            }
            Thread.sleep(1000);
        }
    }

    public V take() throws InterruptedException {
        while (true) {
            synchronized (this) {
                if (!isEmpty()) {
                    return doTake();
                }
            }
            Thread.sleep(1000);
        }
    }

}
