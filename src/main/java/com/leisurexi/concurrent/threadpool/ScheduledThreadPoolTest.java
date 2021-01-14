package com.leisurexi.concurrent.threadpool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 定时及周期性任务线程池
 *
 * @author: leisurexi
 * @date: 2021-01-14 21:18
 */
public class ScheduledThreadPoolTest {

    public static void main(String[] args) {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);

        ScheduledFuture<?> scheduledFuture1 = scheduledThreadPool.schedule(() -> System.out.println("定时任务1"), 5, TimeUnit.SECONDS);

        ScheduledFuture<?> scheduledFuture2 = scheduledThreadPool.schedule(new InterruptibleTask(), 5, TimeUnit.SECONDS);

        // 参数为 true 时代表会中断正在执行的任务，即调用 thread.interrupt() 方法，任务必须捕获该异常并做相应的处理，否则和 false 没什么区别
        scheduledFuture1.cancel(false);

        try {
            Thread.sleep(1000 * 6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        scheduledFuture2.cancel(true);

        scheduledThreadPool.shutdown();

    }

    private static class InterruptibleTask implements Runnable {

        @Override
        public void run() {
            long sum = 0;
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                // 每次检查线程状态，如果被中断了，就不再执行
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("线程被中断");
                    return;
                }
                sum += i;
            }
            System.out.println("最终结果: " + sum);
        }

    }

}
