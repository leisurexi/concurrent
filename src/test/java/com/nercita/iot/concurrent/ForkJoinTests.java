package com.nercita.iot.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 单机版 MapReduce {@link ForkJoinPool} 示例
 *
 * @author: leisurexi
 * @date: 2021-01-29 15:57
 */
@Slf4j
public class ForkJoinTests {

    @Test
    public void test1() {
        String[] fc = {"hello world", "hello me", "hello fork", "hello join", "fork join in world"};
        // 创建 ForkJoin 线程池
        ForkJoinPool fjp = new ForkJoinPool(3);
        // 创建任务
        MR mr = new MR(fc, 0, fc.length);
        // 启动任务
        Map<String, Long> result = fjp.invoke(mr);
        // 输出结果
        result.forEach((k, v) -> log.info(k + ":" + v));
    }

    static class MR extends RecursiveTask<Map<String, Long>> {

        private String[] fc;
        private int start, end;

        public MR(String[] fc, int start, int end) {
            this.fc = fc;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Map<String, Long> compute() {
            if (end - start == 1) {
                return calc(fc[start]);
            } else {
                int mid = (start + end) / 2;
                MR mr1 = new MR(fc, start, mid);
                mr1.fork();
                MR mr2 = new MR(fc, mid, end);
                // 计算子任务，并返回合并结果
                return merge(mr2.compute(), mr1.join());
            }
        }

        /**
         * 合并结果
         */
        private Map<String, Long> merge(Map<String, Long> r1, Map<String, Long> r2) {
            Map<String, Long> result = new HashMap<>();
            result.putAll(r1);
            // 合并结果
            r2.forEach((k, v) -> {
                Long c = result.get(k);
                if (c != null) {
                    result.put(k, c + v);
                } else {
                    result.put(k, v);
                }
            });
            return result;
        }

        /**
         * 统计单词数量
         */
        private Map<String, Long> calc(String line) {
            Map<String, Long> result = new HashMap<>();
            // 分割单词
            String[] words = line.split("\\s+");
            // 统计单词数量
            for (String word : words) {
                Long count = result.get(word);
                result.put(word, count == null ? 1 : count + 1);
            }
            return result;
        }

    }

}
