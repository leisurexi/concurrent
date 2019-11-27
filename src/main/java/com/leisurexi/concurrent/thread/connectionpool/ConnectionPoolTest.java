package com.leisurexi.concurrent.thread.connectionpool;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: leisurexi
 * @date: 2019-11-27 10:14 下午
 * @description:
 * @since JDK 1.8
 */
@Slf4j
public class ConnectionPoolTest {

    private static ConnectionPool pool = new ConnectionPool(10);

    /**
     * 保证所有ConnectionRunner能够同时开始
     */
    private static CountDownLatch start = new CountDownLatch(1);

    /**
     * main线程将会等待所有ConnectionRunner结束后才能继续执行
     */
    private static CountDownLatch end;

    /**
     * 在资源一定的情况下（连接池中的10个连接），随着客户端线程的逐步增加，客户端出现超时无法获取连接的比率不断升高。
     * 虽然客户端线程在这种超时获取的模式下会出现连接无法获取的情况，但是它能够保证客户端线程不会一直挂在连接获取的
     * 操作上，而是"按时"返回，并告知客户端连接获取出现问题，是系统的一种自我保护机制。数据库连接池的设计也可以复用
     * 到其他的资源获取场景，针对昂贵资源（比如数据库连接）的获取都应该加以超时限制。
     */
    public static void main(String[] args) throws InterruptedException {
        //线程数量，可以修改线程数量进行观察
        int threadCount = 30;
        end = new CountDownLatch(threadCount);
        int count = 20;
        AtomicInteger got = new AtomicInteger();
        AtomicInteger notGot = new AtomicInteger();
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(new ConnectionRunner(count, got, notGot), "ConnectionRunnerThread");
            thread.start();
        }
        start.countDown();
        end.await();
        log.info("total invoke: {}", threadCount * count);
        log.info("got connection: {}", got.get());
        log.info("notGot connection: {}", notGot.get());
    }

    static class ConnectionRunner implements Runnable {

        int count;
        AtomicInteger got;
        AtomicInteger notGot;

        public ConnectionRunner(int count, AtomicInteger got, AtomicInteger notGot) {
            this.count = count;
            this.got = got;
            this.notGot = notGot;
        }

        @Override
        public void run() {
            try {
                start.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (count > 0) {
                try {
                    //从线程池中获取连接，如果1000ms内无法获取到，将会返回null
                    //分别统计连接获取的数量got和未获取到的数量notGot
                    Connection connection = pool.fetchConnection(1000);
                    if (connection != null) {
                        try {
                            connection.createStatement();
                            connection.commit();
                        } finally {
                            pool.releaseConnection(connection);
                            got.incrementAndGet();
                        }
                    } else {
                        notGot.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    count--;
                }
            }
            end.countDown();
        }
    }

}
