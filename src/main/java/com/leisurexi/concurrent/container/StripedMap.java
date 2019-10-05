package com.leisurexi.concurrent.container;


/**
 * Created with IntelliJ IDEA.
 * Description: 模仿JDK1.8以前的ConcurrentHashMap的分段锁技术
 * User: leisurexi
 * Date: 2019-10-05
 * Time: 16:53
 */
public class StripedMap {

    //同步策略: buckets[n]由locks[n % N_LOCKS]来保护
    private static final int N_LOCKS = 16;
    private final Node[] buckets;
    private final Object[] locks;

    private static class Node {
        Node next;
        Object key;
        Object value;
    }

    public StripedMap(int numBuckets) {
        this.buckets = new Node[numBuckets];
        this.locks = new Object[N_LOCKS];
        for (int i = 0; i < N_LOCKS; i++) {
            this.locks[i] = new Object();
        }
    }

    private final int hash(Object key) {
        return Math.abs(key.hashCode() % buckets.length);
    }

    public Object get(Object key) {
        int hash = hash(key);
        synchronized (locks[hash % N_LOCKS]) {
            for (Node m = buckets[hash]; m != null; m = m.next) {
                if (m.key.equals(key)) {
                    return m.value;
                }
            }
        }
        return null;
    }

    public void clear() {
        for (int i = 0; i < buckets.length; i++) {
            synchronized (locks[i % N_LOCKS]) {
                buckets[i] = null;
            }
        }
    }

}
