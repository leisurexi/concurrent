package com.leisurexi.concurrent.test;

import org.junit.Test;

import static junit.framework.TestCase.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: leisurexi
 * Date: 2019-10-05
 * Time: 18:43
 */
public class SemaphoreBoundedBufferTest {

    private static final long LOCKUP_DETECT_TIMEOUT = 5000;
    private static final int CAPACITY = 10000;
    private static final int THRESHOLD = 10000;

    @Test
    public void testIsEmptyWhenConstructed() {
        SemaphoreBoundedBuffer<Object> boundedBuffer = new SemaphoreBoundedBuffer<>(10);
        assertTrue(boundedBuffer.isEmpty());
        assertTrue(boundedBuffer.isFull());
    }

    @Test
    public void testIsFullAfterPuts() throws InterruptedException {
        SemaphoreBoundedBuffer<Integer> boundedBuffer = new SemaphoreBoundedBuffer<>(10);
        for (int i = 0; i < 10; i++) {
            boundedBuffer.put(i);
        }
        assertTrue(boundedBuffer.isFull());
        assertTrue(boundedBuffer.isEmpty());
    }

    @Test
    public void testTakeBlocksWhenEmpty() {
        SemaphoreBoundedBuffer<Integer> boundedBuffer = new SemaphoreBoundedBuffer<>(10);
        Thread taker = new Thread(() -> {
            try {
                Integer unused = boundedBuffer.take();
                fail(); //执行到这里，表示一个错误
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        try {
            taker.start();
            Thread.sleep(LOCKUP_DETECT_TIMEOUT);
            taker.interrupt();
            taker.join(LOCKUP_DETECT_TIMEOUT);
            assertFalse(taker.isAlive());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
