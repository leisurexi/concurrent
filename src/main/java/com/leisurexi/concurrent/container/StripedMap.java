package com.leisurexi.concurrent.container;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author: leisurexi
 * @date: 2020-02-16 15:57
 * @description: 模仿JDK1.8以前的ConcurrentHashMap的分段锁技术
 * @since JDK 1.8
 */
@Slf4j
public class StripedMap<K, V> {

    /**
     * 同步策略：buckets[n]由locks[n % N_LOCKS]来保护
     */
    private static final int N_LOCKS = 16;
    private final Node[] buckets;
    private final Object[] locks;

    public StripedMap(int numBuckets) {
        this.buckets = new Node[numBuckets];
        this.locks = new Object[N_LOCKS];
        for (int i = 0; i < N_LOCKS; i++) {
            locks[i] = new Object();
        }
    }

    private final int hash(Object key) {
        return Math.abs(key.hashCode() % buckets.length);
    }

    public V get(Object key) {
        Node<K, V> e;
        return (e = getNode(key)) == null ? null : e.value;
    }

    private Node<K, V> getNode(Object key) {
        int hash = hash(key);
        synchronized (locks[hash % N_LOCKS]) {
            for (Node<K, V> m = buckets[hash]; m != null; m = m.next) {
                if (m.key.equals(key)) {
                    return m;
                }
            }
        }
        return null;
    }

    public void put(K key, V value) {
        int hash = hash(key);
        synchronized (locks[hash % N_LOCKS]) {
            Node<K, V> node = getNode(key);
            if (node != null) {
                node.value = value;
            } else {
                Node<K, V> newNode = new Node<>(hash, key, value, null);
                if (buckets[hash] == null) {
                    buckets[hash] = newNode;
                } else {
                    node = buckets[hash];

                    //头插式
//                    buckets[hash] = newNode;
//                    newNode.next = node;

                    //尾插式
                    while (node.next != null) {
                        node = node.next;
                    }
                    node.next = newNode;
                }
            }
        }
        log.info(Arrays.toString(buckets));
    }

    public void clear() {
        for (int i = 0; i < buckets.length; i++) {
            synchronized (locks[i % N_LOCKS]) {
                buckets[i] = null;
            }
        }
    }

    @ToString
    private static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }
    }

    public static void main(String[] args) {
        StripedMap<Integer, Integer> map = new StripedMap<>(2);
        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 3);
    }

}
