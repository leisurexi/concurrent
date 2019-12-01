package com.leisurexi.concurrent.tool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author: leisurexi
 * @date: 2019-12-01 2:47 下午
 * @description:
 * @since JDK 1.8
 */
@Slf4j
public class CyclicBarrierTest3 {

    private static CyclicBarrier barrier = new CyclicBarrier(2);

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        thread.interrupt();

        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
            log.info("阻塞的线程是否被中断: {}", barrier.isBroken());
        }

    }

}
