package com.leisurexi.concurrent.tool;

import com.leisurexi.concurrent.util.SleepUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @author: leisurexi
 * @date: 2019-12-01 2:54 下午
 * @description: Semaphore(信号量)是用来控制同时访问特定资源的线程数量，它通过各个线程，以保证合理
 * 的使用公共资源。
 * @since JDK 1.8
 */
@Slf4j
public class SemaphoreTest {

    private static final int THREAD_COUNT = 30;

    private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);

    private static Semaphore semaphore = new Semaphore(10);

    public static void main(String[] args) {
        for (int i = 0; i < THREAD_COUNT; i++) {
            threadPool.execute(() -> {
                try {
                    semaphore.acquire();
                    log.info("save data");
                    SleepUtils.second(1);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        threadPool.shutdown();
    }

}
