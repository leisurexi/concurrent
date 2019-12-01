package com.leisurexi.concurrent.tool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author: leisurexi
 * @date: 2019-12-01 2:26 下午
 * @description:
 * @since JDK 1.8
 */
@Slf4j
public class CyclicBarrierTest2 {

    /**
     * 当线程到达屏障时，优先执行MyThread
     */
    private static CyclicBarrier barrier = new CyclicBarrier(2, new MyThread());

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        new Thread(() -> {
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            log.info(String.valueOf(1));
        }).start();

        barrier.await();
        log.info(String.valueOf(2));
    }

    private static class MyThread implements Runnable {
        @Override
        public void run() {
            log.info(String.valueOf(3));
        }
    }

}
