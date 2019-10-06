package com.leisurexi.concurrent.container;

/**
 * Created with IntelliJ IDEA.
 * Description: BoundedBuffer中使用wait和notifyAll来实现一个有界缓存。这比使用"休眠"的有界缓存更简单，
 * 并且更高效(当缓存状态没有发生变化时，线程醒来的次数将更少)，响应性也更高(当发生特定状态变化时将立即醒来)。
 * 这是一个较大的改进，但要注意: 与使用"休眠"的有界缓存相比，条件队列并没有改变原来的语义。它只是在多个方面进行了优化: CPU效率、
 * 上下文切换开销和响应性等。
 * User: leisurexi
 * Date: 2019-10-06
 * Time: 1:46 下午
 */
public class BoundedBuffer<V> extends BaseBoundedBuffer<V> {

    protected BoundedBuffer(int capacity) {
        super(capacity);
    }

    //阻塞并直到队列不满的情况
    public synchronized void put(V v) throws InterruptedException {
        while (isFull()) {
            wait();
        }
        doPut(v);
        notifyAll();
    }

    //阻塞并直到队列有元素的情况
    public synchronized V take() throws InterruptedException {
        while (isEmpty()) {
            wait();
        }
        V v = doTake();
        notifyAll();
        return v;
    }

}
