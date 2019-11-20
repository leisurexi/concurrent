package com.leisurexi.concurrent.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author: leisurexi
 * @date: 2019-11-20 8:54 下午
 * @description:
 * @since JDK 1.8
 */
@Slf4j
public class CustomerThreadPoolTest {

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue queue = new ArrayBlockingQueue(4);
        CustomThreadPool threadPool = new CustomThreadPool(3, 5, 1, TimeUnit.SECONDS, queue, () -> {
            log.info("任务执行完毕");
        });
        for (int i = 0; i < 10; i++) {
            threadPool.execute(new Worker(i));
        }
        log.info("=======休眠前线程池活跃线程数={}======", threadPool.getWorkerCount());

        TimeUnit.SECONDS.sleep(5);
        log.info("=======休眠后线程池活跃线程数={}======", threadPool.getWorkerCount());

        for (int i = 0; i < 3; i++) {
            threadPool.execute(new Worker(i + 100));
        }

        threadPool.shutDown();
        //pool.shutDownNow();
        //pool.execute(new Worker(100));
        log.info("++++++++++++++");
        threadPool.mainNotify();
    }

    private static class Worker implements Runnable {

        private int state;

        public Worker(int state) {
            this.state = state;
        }

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(1);
                log.info("state={}", state);
            } catch (InterruptedException e) {

            }
        }
    }

}
