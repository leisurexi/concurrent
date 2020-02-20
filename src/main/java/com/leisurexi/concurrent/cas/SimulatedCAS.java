package com.leisurexi.concurrent.cas;

/**
 * @author: leisurexi
 * @date: 2020-02-20 19:36
 * @description: CAS包含了3个操作数——需要读写的内存位置V、进行比较的值A和拟写入的新值B。
 * 当且仅V的值等于A时，CAS才会通过原子方式用新值B来更新V的值，否则不会执行任何操作。无论
 * 位置V的值是否等于A，都将返回V原有的值。(这种变化形式被称为比较并设置，无论操作是否成功都
 * 会返回。) CAS的含义是：我认为V的值应该是A，如果是，那么将V的值更新为B，否则不修改并告诉
 * V的值实际为多少。CAS是一项乐观的技术，它希望能成功地执行更新操作，并且如果有另一个线程在
 * 最近一次检查后更新了该变量，那么CAS能检测到这个错误。
 * @since JDK 1.8
 */
public class SimulatedCAS {

    /** 模拟CAS操作 */

    private int value;

    public synchronized int get() {
        return value;
    }

    public synchronized int compareAndSwap(int expectedValue, int newValue) {
        int oldValue = value;
        if (oldValue == expectedValue) {
            value = newValue;
        }
        return oldValue;
    }

    public synchronized boolean compareAndSet(int expectedValue, int newValue) {
        return (expectedValue == compareAndSwap(expectedValue, newValue));
    }

}
