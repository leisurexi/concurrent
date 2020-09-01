package com.leisurexi.concurrent.cas;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: leisurexi
 * @date: 2020-02-20 21:05
 * @description: 非阻塞的栈。栈是由Node元素构成的一个链表，其中栈顶作为根节点，
 * 并且在元素中都包含了一个值以及指向下一个元素的链接。push方法创建一个新的节点，
 * 该节点的next域指向当前的栈顶，然后使用CAS把这个新节点放入栈顶。如果在开始插入
 * 节点时，位于栈顶的节点没有变化，那么CAS就会成功，如果栈顶节点发生了变化(例如
 * 其他线程在本地线程开始之前插入或移除了元素)，那么CAS将会失败，而push方法会根
 * 据栈的当前状态来更新节点，并且再次尝试。无论哪种情况，在CAS执行完成后，栈仍会
 * 处于一致的状态。
 * @since JDK 1.8
 */
public class ConcurrentStack<E> {

    AtomicReference<Node<E>> top = new AtomicReference<>();

    public void push(E item) {
        Node<E> newHead = new Node<>(item);
        Node<E> oldHead;
        do {
            oldHead = top.get();
            newHead.next = oldHead;
        } while (!top.compareAndSet(oldHead, newHead));
    }

    public E pop() {
        Node<E> oldHead;
        Node<E> newHead;
        do {
            oldHead = top.get();
            if (oldHead == null) {
                return null;
            }
            newHead = oldHead.next;
        } while (!top.compareAndSet(oldHead, newHead));
        return oldHead.item;
    }

    private static class Node<E> {
        public final E item;
        public Node<E> next;

        public Node(E item) {
            this.item = item;
        }
    }

}
