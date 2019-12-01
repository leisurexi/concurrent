package com.leisurexi.concurrent.tool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author: leisurexi
 * @date: 2019-12-01 2:17 下午
 * @description: CyclicBarrier的字面意思是可循环使用(Cyclic)的屏障(Barrier)。它要做的事情是，让一组线程
 * 到达一个屏障(也可以叫同步点)时被阻塞，直到最后一个线程到达屏障时，屏障才会开门，所有被屏障拦截的线程才会继续运行。
 * @since JDK 1.8
 */
@Slf4j
public class CyclicBarrierTest {

    private static CyclicBarrier barrier = new CyclicBarrier(2);

    private static int i = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            i += 10;
        });
        thread1.start();
        Thread thread2 = new Thread(() -> {
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            i += 20;
        });
        thread2.start();
        thread1.join();
        thread2.join();
        log.info(String.valueOf(i));
    }

}
