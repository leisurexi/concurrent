package com.leisurexi.concurrent.container;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Description: 多线程情况下HashMap死循环示例
 * User: leisurexi
 * Date: 2019-09-25
 * Time: 20:34
 */
public class HashMapTest {

    /**
     * 因为用的是jdk1.8当链表节点大于8时转为红黑树解决了链表形成环形数据结构产生的死循环问题。
     * 一旦链表形成环形数据结构，Entry的next节点永远不为空，就会产生死循环获取Entry。
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        final Map<String, String> map = new HashMap<>(2);
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                new Thread(() -> {
                    map.put(UUID.randomUUID().toString(), "");
                }, "ftf" + i).start();
            }
        }, "ftf");
        thread.start();
        thread.join();
        System.out.println(map.size());
    }

}
