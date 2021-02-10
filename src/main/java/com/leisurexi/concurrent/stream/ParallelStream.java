package com.leisurexi.concurrent.stream;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Java8 并行流示例
 *
 * @author: leisurexi
 * @date: 2021-02-10 10:43
 */
@Slf4j
public class ParallelStream {

    @Test
    public void test1() {
        IntStream list = IntStream.range(0, 10);
        Set<Thread> threadSet = new CopyOnWriteArraySet<>();
        // 开始并行执行
        list.parallel().forEach(value -> {
            log.info("integer: [{}]", value);
            threadSet.add(Thread.currentThread());
        });
        log.info("all threads: [{}]", threadSet.stream().map(Thread::getName).collect(Collectors.joining(":")));
    }

    /**
     * 使用并行流会出现线程安全问题
     */
    @Test
    public void test2() {
        List<Integer> values = new ArrayList<>();
        // 会出现并发问题
//        IntStream.range(1, 10000).parallel().forEach(values::add);
        IntStream.range(1, 10000).forEach(values::add);
        ArrayList<Object> result = IntStream.range(1, 10000).parallel().collect(ArrayList::new, (objects, value) -> objects.add(value),
                (objects, objects2) -> objects.addAll(objects2));
        log.info(String.valueOf(values.size()));
        log.info(String.valueOf(result.size()));
    }

    @Test
    public void test3() {
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        List<Integer> list3 = new ArrayList<>();

        ReentrantLock lock = new ReentrantLock();

        long startTime = System.currentTimeMillis();
        IntStream.range(0, 10000).forEach(list1::add);
        log.info("串行执行耗费时间: [{}]ms, 集合长度: [{}]", (System.currentTimeMillis() - startTime), list1.size());

        startTime = System.currentTimeMillis();
        IntStream.range(0, 10000).parallel().forEach(list2::add);
        log.info("并行执行耗费时间: [{}]ms, 集合长度: [{}]", (System.currentTimeMillis() - startTime), list2.size());

        startTime = System.currentTimeMillis();
        IntStream.range(0, 10000).parallel().forEach(value -> {
            lock.lock();
            try {
                list3.add(value);
            } finally {
                lock.unlock();
            }
        });
        log.info("并行枷锁执行耗费时间: [{}]ms, 集合长度: [{}]", (System.currentTimeMillis() - startTime), list3.size());
    }

}
