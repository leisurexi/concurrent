package com.leisurexi.concurrent.jmm;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: leisurexi
 * @date: 2019-11-21 9:08 下午
 * @description: volatile变量自增运算测试
 * @since JDK 1.8
 */
@Slf4j
public class VolatileTest {

    public static volatile int race = 0;

    public static void increase() {
        race++;
    }

    private static final int THREAD_COUNTS = 20;

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[THREAD_COUNTS];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    increase();
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        log.info(String.valueOf(race));
    }

}
