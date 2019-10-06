package com.leisurexi.concurrent.container;

/**
 * Created with IntelliJ IDEA.
 * Description: GrumpyBoundedBuffer是第一个简单的有界缓存实现。put和take方法都进行了同步以确保实现对缓存状态的
 * 独占访问，因为这两个方法在访问缓存时都采用"先检查再运行"的逻辑策略。将前提条件的失败传递给调用者。
 * User: leisurexi
 * Date: 2019-10-06
 * Time: 1:03 下午
 */
public class GrumpyBoundedBuffer<V> extends BaseBoundedBuffer<V> {

    protected GrumpyBoundedBuffer(int capacity) {
        super(capacity);
    }

    public synchronized void put(V v) throws Exception {
        if (isFull()) {
            throw new RuntimeException("队列已满");
        }
        doPut(v);
    }

    public synchronized V take() throws Exception {
        if (isEmpty()) {
            throw new RuntimeException("队列为空");
        }
        return doTake();
    }

}
