package com.leisurexi.concurrent.container;

import com.leisurexi.concurrent.util.SleepUtils;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: leisurexi
 * Date: 2019-10-06
 * Time: 1:08 下午
 */
public class BoundBufferTest {

    final int capacity = 10;

    public static final long SLEEP_TIME = 5;

    /**
     * 这种方式很不优雅。每次出现异常时，睡眠5秒，再重试
     */
    @Test
    public void testGrumpyBoundedBuffer() {
        GrumpyBoundedBuffer<String> buffer = new GrumpyBoundedBuffer<>(capacity);
        while (true) {
            try {
                String take = buffer.take();
                System.out.println(take);
            } catch (Exception e) {
                e.printStackTrace();
                SleepUtils.second(SLEEP_TIME);
            }
        }
    }

    /**
     * 在线程刚刚进入休眠后，条件立即变为真，此时将存在不必要的休眠时间
     */
    @Test
    public void testSleepyBoundedBuffer() {
        SleepyBoundedBuffer<String> buffer = new SleepyBoundedBuffer<>(capacity);
        String take = buffer.take();
        System.out.println(take);
    }

    @Test
    public void testBoundedBuffer() throws InterruptedException {
        BoundedBuffer<String> buffer = new BoundedBuffer<>(capacity);
        String take = buffer.take();
        System.out.println(take);
    }


}
