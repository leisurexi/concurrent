package com.leisurexi.concurrent.future;

import com.leisurexi.concurrent.util.SleepUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

/**
 * {@link CompletableFuture} 示例
 *
 * @author: leisurexi
 * @date: 2021-01-28 10:02
 */
@Slf4j
public class CompletableFutureTests {

    @Test
    public void test1() {
        // 任务1：洗水壶 -> 烧开水
        // runAsync() 方法没有返回值
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            log.info("T1:洗水壶...");
            SleepUtils.second(1);
            log.info("T1:烧开水...");
            SleepUtils.second(15);
        });

        // 任务2：洗茶壶 -> 洗茶杯 -> 拿茶叶
        // supplyAsync() 有返回值
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            log.info("T2:洗茶壶...");
            SleepUtils.second(1);
            log.info("T2:洗茶叶...");
            SleepUtils.second(2);
            log.info("T2:拿茶叶...");
            SleepUtils.second(1);
            return "龙井";
        });

        // 任务3：任务1和任务2执行完成后执行：泡茶
        CompletableFuture<String> f3 = f1.thenCombine(f2, (unused, s) -> {
            log.info("T1:拿到茶叶:" + s);
            log.info("T1:泡茶...");
            return "上茶" + s;
        });

        // 等待任务3执行结果
        log.info(f3.join());
    }

    /**
     * 描述串行关系
     */
    @Test
    public void test2() {
        CompletableFuture<String> future = CompletableFuture
                .supplyAsync(() -> "Hello World")
                .thenApply(s -> s + " QQ")
                .thenApply(String::toUpperCase);
        log.info(future.join());
    }

    /**
     * 描述 AND 汇聚关系
     */
    @Test
    public void test3() {
        // 任务1：洗水壶 -> 烧开水
        // runAsync() 方法没有返回值
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            log.info("T1:洗水壶...");
            SleepUtils.second(1);
            log.info("T1:烧开水...");
            SleepUtils.second(15);
        });

        // 任务2：洗茶壶 -> 洗茶杯 -> 拿茶叶
        // supplyAsync() 有返回值
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            log.info("T2:洗茶壶...");
            SleepUtils.second(1);
            log.info("T2:洗茶叶...");
            SleepUtils.second(2);
            log.info("T2:拿茶叶...");
            SleepUtils.second(1);
            return "龙井";
        });

        // 任务3：任务1和任务2执行完成后执行：泡茶
        CompletableFuture<String> f3 = f1.thenCombine(f2, (unused, s) -> {
            log.info("T1:拿到茶叶:" + s);
            log.info("T1:泡茶...");
            return "上茶" + s;
        });

        // 等待任务3执行结果
        log.info(f3.join());
    }

    /**
     * 描述 OR 汇聚关系
     * 只要有一个任务完成即可执行当前任务
     */
    @Test
    public void test4() {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            int t = 5;
            SleepUtils.second(t);
            return String.valueOf(t);
        });

        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            int t = 6;
            SleepUtils.second(t);
            return String.valueOf(t);
        });

        CompletableFuture<String> f3 = f1.applyToEither(f2, s -> s);
        log.info(f3.join());
    }

    /**
     * 异步编程 异常处理
     */
    @Test
    public void test5() {
        CompletableFuture<Integer> future = CompletableFuture
                .supplyAsync(() -> 7 / 0)
                .thenApply(r -> r * 10)
                .exceptionally(e -> 0)
                .whenComplete((value, throwable) -> log.error("异常打印", throwable));
        log.info(String.valueOf(future.join()));
    }

}
