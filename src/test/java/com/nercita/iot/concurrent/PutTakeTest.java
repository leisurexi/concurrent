package com.nercita.iot.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: leisurexi
 * @date: 2020-02-18 19:19
 * @description:
 * @since JDK 1.8
 */
@Slf4j
public class PutTakeTest {

    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private final AtomicInteger putSum = new AtomicInteger(0);
    private final AtomicInteger takeSum = new AtomicInteger(0);
    private final CyclicBarrier barrier;
    private final BarrierTimer timer;
    private final BoundedBuffer<Integer> boundedBuffer;
    private final int nTrials, nPairs;

    public static void main(String[] args) {
        new PutTakeTest(10, 10, 100000).test();
    }

    public PutTakeTest(int capacity, int nPairs, int nTrials) {
        this.boundedBuffer = new BoundedBuffer<>(capacity);
        this.nTrials = nTrials;
        this.nPairs = nPairs;
        this.timer = new BarrierTimer();
        this.barrier = new CyclicBarrier(nPairs * 2 + 1, timer);
    }

    void test() {
        try {
            timer.clear();
            for (int i = 0; i < nPairs; i++) {
                pool.execute(new Producer());
                pool.execute(new Consumer());
            }
            barrier.await(); //等待所有的线程就绪
            barrier.await(); //等待所有的线程执行完成
            long nsPerItem = timer.getTime() / (nPairs * (long) nTrials);
            log.info("through: {}", nsPerItem);
            log.info(String.valueOf(putSum.get()));
            log.info(String.valueOf(takeSum.get()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            pool.shutdown();
        }
    }

    static int xorShift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);
        return y;
    }

    class Producer implements Runnable {
        @Override
        public void run() {
            try {
                int seed = (this.hashCode() ^ (int) System.nanoTime());
                int sum = 0;
                barrier.await();
                for (int i = nTrials; i > 0; --i) {
                    boundedBuffer.put(seed);
                    sum += seed;
                    seed = xorShift(seed);
                }
                putSum.getAndAdd(sum);
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                barrier.await();
                int sum = 0;
                for (int i = nTrials; i > 0; --i) {
                    sum += boundedBuffer.take();
                }
                takeSum.getAndAdd(sum);
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class BarrierTimer implements Runnable {
        private boolean started;
        private long startTime, endTime;

        @Override
        public synchronized void run() {
            long t = System.nanoTime();
            if (!started) {
                started = true;
                startTime = t;
            } else {
                endTime = t;
            }
        }

        public synchronized void clear() {
            started = false;
        }

        public synchronized long getTime() {
            return endTime - startTime;
        }

    }

}
