package com.leisurexi.concurrent.thread.connectionpool;

import java.sql.Connection;
import java.util.LinkedList;

/**
 * @author: leisurexi
 * @date: 2019-11-27 9:55 下午
 * @description:
 * @since JDK 1.8
 */
public class ConnectionPool {

    private LinkedList<Connection> pool = new LinkedList<>();

    public ConnectionPool(int initialSize) {
        if (initialSize < 1) {
            throw new IllegalArgumentException("initialSize must be more than 0");
        }
        for (int i = 0; i < initialSize; i++) {
            pool.addLast(ConnectionDriver.createConnection());
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            synchronized (pool) {
                //连接释放后需要进行通知，这样其他消费者能够感知到连接池中已经归还了一个连接
                pool.addLast(connection);
                pool.notifyAll();
            }
        }
    }

    /**
     * 在mills内无法获取连接，将会返回null
     * @param mills
     * @return
     * @throws InterruptedException
     */
    public Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (pool) {
            //完全超时
            if (mills <= 0) {
                while (pool.isEmpty()) {
                    pool.wait();
                }
                return pool.removeFirst();
            } else {
                long future = System.currentTimeMillis() + mills;
                long remaining = mills;
                while (pool.isEmpty() && remaining > 0) {
                    pool.wait(remaining);
                    remaining = future - System.currentTimeMillis();
                }
                Connection result = null;
                if(!pool.isEmpty()){
                    result = pool.removeFirst();
                }
                return result;
            }
        }
    }


}
