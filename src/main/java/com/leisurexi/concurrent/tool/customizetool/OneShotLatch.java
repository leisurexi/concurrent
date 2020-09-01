package com.leisurexi.concurrent.tool.customizetool;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author: leisurexi
 * @date: 2020-02-19 21:39
 * @description:
 * @since JDK 1.8
 */
public class OneShotLatch {

    private final Sync sync = new Sync();

    public void signal() {
        sync.releaseShared(0);
    }

    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(0);
    }


    private class Sync extends AbstractQueuedSynchronizer {

        @Override
        protected int tryAcquireShared(int arg) {
            //如果闭锁是开的(state == 1)，那么操作将成功，否则失败
            return (getState() == 1) ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            //打开闭锁
            setState(1);
            return true;
        }
    }

}
