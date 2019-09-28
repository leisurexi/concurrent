package com.leisurexi.concurrent.util;

import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created with IntelliJ IDEA.
 * Description: 同步屏障CyclicBarrier
 * 让一组线程到达一个屏障(也可以叫同步点)时被阻塞，知道最后一个线程到达屏障时，屏障才会开门，
 * 所有被屏障拦截的线程才会继续运行
 * User: leisurexi
 * Date: 2019-09-27
 * Time: 21:31
 */
public class CyclicBarrierTest {

    /**
     * 如果把CyclicBarrier构造函数中的int参数改为3，则主线程和子线程会永远等待，因为没有第三个线程执行await方法，
     * 即没有第三个线程到达屏障，所以之前到达屏障的两个线程不会继续执行
     */
    @Test
    public void test1() {
        //构造函数中的int参数表示屏障拦截的线程数量，每个线程调用await方法告诉CyclicBarrier我已经到达屏障，然后当前线程被阻塞
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
        new Thread(() -> {
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println(1);
        }).start();

        try {
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println(2);
    }

    //
    @Test
    public void test2() {
        //带Runnable参数的构造函数，用于在线程到达屏障时，有限执行barrierAction，方便处理更复杂的业务场景
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2, new A());
        new Thread(() -> {
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println(1);
        }).start();
        try {
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println(2);
    }

    static class A implements Runnable {
        @Override
        public void run() {
            System.out.println(3);
        }
    }

    /**
     * CyclicBarrier和CountDownLatch的区别
     * CountDownLatch的计数器只能使用一次，而CyclicBarrier的计数器可以使用reset()方法重置。所以CyclicBarrier能处理
     * 更复杂的业务场景。
     */
    @Test
    public void test3() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
        Thread thread = new Thread(() -> {
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        thread.interrupt();
        try {
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("InterruptedException: " + cyclicBarrier.isBroken());
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
            //isBroken用来了解阻塞线程释放被中断
            System.out.println("BrokenBarrierException: " + cyclicBarrier.isBroken());
        }
    }

}
