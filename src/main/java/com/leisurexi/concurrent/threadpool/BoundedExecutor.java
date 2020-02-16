package com.leisurexi.concurrent.threadpool;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * @author: leisurexi
 * @date: 2020-02-16 10:56
 * @description: 使用Semaphore来控制任务的提交速率
 * @since JDK 1.8
 */
public class BoundedExecutor {

    private final Executor exec;
    private Semaphore semaphore;

    public BoundedExecutor(Executor exec, int bound) {
        this.exec = exec;
        this.semaphore = new Semaphore(bound);
    }

    public void submitTask(final Runnable command) throws InterruptedException {
        semaphore.acquire();
        try {
            exec.execute(() -> {
                try {
                    command.run();
                } finally {
                    semaphore.release();
                }
            });
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
            semaphore.release();
        }
    }

}
