package com.leisurexi.concurrent.thread.threadpool;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: leisurexi
 * Date: 2019-09-21
 * Time: 22:27
 */
public class ThreadPoolTest {

    public static void main(String[] args) throws InterruptedException {
        DefaultThreadPool<Runnable> threadPool = new DefaultThreadPool<>();
        for (int i = 0; i < 100; i++) {
            threadPool.execute(() -> System.out.println("leisurexi"));
        }
    }

}
