package com.leisurexi.concurrent.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created with IntelliJ IDEA.
 * Description: Semaphore(信号量)是用来控制同时访问特定资源的线程数量，它通过协调各个线程，保证合理的使用公共资源
 *
 * 可以把Semaphore比作是控制流量的红绿灯，比如XX马路要限制流量，只允许同时有一百辆车在这条路上行驶，其他的都必须在路口等待，
 * 所以前一百辆车会看到绿灯，可以开进这条马路，后面的车会看到红灯，不能驶入XX马路，但是如果前一百辆车有5辆车已经离开了XX马路，
 * 那么后面就允许有5辆车驶入马路，这个例子里说的车就是线程，驶入马路就表示线程在执行，离开马路就表示线程执行完成，看见红灯就
 * 表示被阻塞，不能执行。
 *
 * User: leisurexi
 * Date: 2019-09-28
 * Time: 00:32
 */
public class SemaphoreTest {

    private static final int THREAD_COUNT = 30;

    private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);

    private static Semaphore semaphore = new Semaphore(10);

    /**
     * 在代码中，虽然有30个线程在执行，但是只允许10个并发执行。Semaphore的构造方法int参数接收一个整形的数字，
     * 表示可用的许可证数量。
     * 首先线程使用Semaphore的acquire()方法获取一个许可证，使用完之后用release()方法归还许可证。
     */
    public static void main(String[] args) {
        for (int i = 0; i < THREAD_COUNT; i++) {
            threadPool.execute(() -> {
                try {
                    semaphore.acquire();
                    System.out.println("save data");
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        threadPool.shutdown();
    }

}
