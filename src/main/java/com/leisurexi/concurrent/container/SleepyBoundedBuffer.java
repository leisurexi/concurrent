package com.leisurexi.concurrent.container;

import com.leisurexi.concurrent.util.SleepUtils;

/**
 * Created with IntelliJ IDEA.
 * Description: SleepyBoundedBuffer尝试通过put和take方法来实现一种简单的"轮询与休眠"重试机制，从而使调用者无须在
 * 每次调用时都实现重试逻辑。如果缓存为空，那么take将休眠并直到另一个线程在缓存中放入一些数据；如果缓存是满的，那么put将
 * 休眠并直到另一个线程从缓存中移除一些数据，以便有空间容纳新的数据。这种方法将前提条件的管理操作封装起来，并简化了对缓存的使用。
 * User: leisurexi
 * Date: 2019-10-06
 * Time: 1:18 下午
 */
public class SleepyBoundedBuffer<V> extends BaseBoundedBuffer<V> {

    protected SleepyBoundedBuffer(int capacity) {
        super(capacity);
    }

    public void put(V v) {
        while (true) {
            synchronized (this) {
                if (!isFull()) {
                    doPut(v);
                    return;
                }
            }
            SleepUtils.second(BoundBufferTest.SLEEP_TIME);
        }
    }

    public V take() {
        while (true) {
            synchronized (this) {
                if (!isEmpty()) {
                    return doTake();
                }
            }
            SleepUtils.second(BoundBufferTest.SLEEP_TIME);
        }
    }

}
