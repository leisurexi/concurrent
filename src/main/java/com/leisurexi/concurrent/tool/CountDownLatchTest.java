package com.leisurexi.concurrent.tool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * @author: leisurexi
 * @date: 2019-12-01 2:08 下午
 * @description: CountDownLatch允许一个或多个线程等待其他线程完成操作。
 * @since JDK 1.8
 */
@Slf4j
public class CountDownLatchTest {

    /**
     * CountDownLatch的构造函数接收一个int类型的参数作为计数器，如果你想等待N个点完成，这里就传入N。
     * 当我们调用CountDownLatch的countDown方法时，N就会减1，CountDownLatch的await方法会阻塞当前
     * 线程，直到N变成零。由于CountDownLatch方法可以用在任务地方，所以这里说的N个点，可以是N个线程，也
     * 可以使1个线程里的N各执行步骤。用在多线程时，只需要把这个CountDownLatch的引用传递到线程里即可。
     *
     * 注意: 计数器必须大于0，只是等于0的时候，调用await方法时不会阻塞当前线程。CountDownLatch不可能
     * 重新初始化或者修改CountDownLatch对象内部技术器的值。一个线程调用countDown方法happens-before，
     * 另外一个线程调用await方法。
     */
    private static CountDownLatch countDownLatch = new CountDownLatch(2);

    private static int i = 0;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            i += 10;
            countDownLatch.countDown();
        }).start();
        new Thread(() -> {
            i += 20;
            countDownLatch.countDown();
        }).start();
        countDownLatch.await();
        log.info(String.valueOf(i));
    }

}
