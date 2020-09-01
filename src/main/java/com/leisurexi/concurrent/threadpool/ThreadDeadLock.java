package com.leisurexi.concurrent.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author: leisurexi
 * @date: 2020-02-16 10:16
 * @description: 线程饥饿死锁示例
 * @since JDK 1.8
 */
@Slf4j
public class ThreadDeadLock {

    private static ExecutorService exec = Executors.newSingleThreadExecutor();

    public static class RenderPageTask implements Callable<String> {

        @Override
        public String call() throws Exception {
            Future<String> header, footer;
            header = exec.submit(() -> {
                log.info("加载头部页面");
                return "success";
            });
            footer = exec.submit(() -> {
                log.info("加载尾部页面");
                return "success";
            });
            //将发生死锁——由于任务在等待子任务完成
            return header.get() + footer.get();
        }

    }

    public static void main(String[] args) {
        exec.submit(new RenderPageTask());
    }

}
