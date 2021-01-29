package com.nercita.iot.concurrent;

import com.leisurexi.concurrent.util.SleepUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * {@link CompletionService} 代码示例
 *
 * @author: leisurexi
 * @date: 2021-01-29 14:54
 */
@Slf4j
public class CompletionServiceTests {

    @Test
    public void test1() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);

        // 异步向电商1询价
        completionService.submit(() -> getPriceByS1());
        // 异步向电商2询价
        completionService.submit(() -> getPriceByS2());
        // 异步向电商3询价
        completionService.submit(() -> getPriceByS3());

        for (int i = 0; i < 3; i++) {
            Integer price = completionService.take().get();
            log.info(String.valueOf(price));
        }

        executor.shutdown();
    }

    /**
     * 模仿 Dubbo 中的 Forking 集群模式，并行的调用多个查询服务，只要有一个成功返回结果，整个服务就可以返回了。
     */
    @Test
    public void test2() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);
        List<Future<Integer>> futures = new ArrayList<>();

        futures.add(completionService.submit(() -> getPriceByS1()));
        futures.add(completionService.submit(() -> getPriceByS2()));
        futures.add(completionService.submit(() -> getPriceByS3()));

        // 获取最快返回的任务执行结果
        Integer r = 0;
        try {
            // 只要有一个成功，则 break
            for (int i = 0; i < 3; i++) {
                r = completionService.take().get();
                // 简单地通过判空来检查是否成功返回
                if (r != null) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            // 取消所有任务
            for (Future<Integer> future : futures) {
                future.cancel(true);
            }
        }
        // 返回结果
        log.info("结果：[{}]", r);
    }

    private Integer getPriceByS1() {
        log.info("询价电商1");
        SleepUtils.second(1);
        return 10;
    }

    private Integer getPriceByS2() {
        log.info("询价电商2");
        SleepUtils.second(2);
        return 20;
    }

    private Integer getPriceByS3() {
        log.info("询价电商3");
        SleepUtils.second(3);
        return 30;
    }

}
