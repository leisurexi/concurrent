package com.nercita.iot.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author: leisurexi
 * @date: 2020-02-16 22:43
 * @description:
 * @since JDK 1.8
 */
@Slf4j
public class BoundBufferTest extends JSR166TestCase {

    private static final long LOCKUP_DETECT_TIMEOUT = 1000;
    private static final int CAPACITY = 10000;
    private static final int THRESHOLD = 10000;


    /** ===================================串行测试=================================== */

    /**
     * 新创建的容器应该是空的，而不是满的
     */
    @Test
    public void testIsEmptyWhenConstructed() {
        BoundedBuffer<Integer> boundedBuffer = new BoundedBuffer<>(10);
        assertTrue(boundedBuffer.isEmpty());
        assertTrue(boundedBuffer.isFull());
    }

    /**
     * 将N个元素插入到容量为N的容器中（这个过程应该成功，并且不会阻塞），然后测试容器是否已经填满
     *
     * @throws InterruptedException
     */
    @Test
    public void testIfFullAfterPuts() throws InterruptedException {
        BoundedBuffer<Integer> boundedBuffer = new BoundedBuffer<>(10);
        for (int i = 0; i < 10; i++) {
            boundedBuffer.put(i);
        }
        assertTrue(boundedBuffer.isFull());
        assertTrue(boundedBuffer.isEmpty());
    }

    /**
     * ===================================阻塞测试===================================
     */
    /**
     * 创建一个 “获取线程”，该线程会尝试从空容器中获取一个元素。如果take方法成功，那么表示测试失败。
     * 执行测试的线程启动 “获取线程”，等待一段时间，然后中断该线程。如果该线程正确地在take方法中阻塞，
     * 那么将抛出InterruptedException，而捕获这个异常的catch块将把这个异常视为测试成功，并让线程退出。
     * 然后，主线程会尝试与 “获取线程” 合并，通过调用Thread.isAlive来验证join方法是否成功返回，如果
     * “获取线程” 可以响应中断，那么join能很快地完成。
     */
    @Test
    public void testTakeBlockWhenEmpty() {
        final BoundedBuffer<Integer> boundedBuffer = new BoundedBuffer<>(10);
        Thread taker = new Thread(() -> {
            try {
                Integer unused = boundedBuffer.take();
                fail(); //如果执行到这里，那么表示出现了一个错误
            } catch (InterruptedException success) {
                log.info(Thread.currentThread().getName() + "收到中断信号");
            }
        }, "获取线程");
        try {
            taker.start();
            Thread.sleep(LOCKUP_DETECT_TIMEOUT);
            taker.interrupt();
            taker.join(LOCKUP_DETECT_TIMEOUT);
            assertFalse(taker.isAlive());
        } catch (Exception unexpected) {
            fail();
        }
    }

    /**
     * ===================================生产者-消费者测试===================================
     */
    /**
     * 启动了N个生产者线程来生成元素并把它们插入到队列，同时还启动了N个消费者线程从队列中取出元素。
     * 当元素进出队列时，每个线程都会更新这些元素计算得到的校验和，每个线程都拥有一个校验和，并在
     * 测试结束后将它们合并起来，从而在测试容器时就不会引入过多的同步或竞争。
     * 根据系统平台的不同，创建线程与启动线程等操作可能需要较大开销。如果线程的执行时间很短，并且
     * 在循环中启动了大量的这种线程，那么最坏的情况就是，这些线程将会串行执行而不是并发执行。即使
     * 在一些不太糟糕的情况下，第一个线程仍然比其他线程具有 “领先优势”。因此这可能无法获得预想中
     * 的交替执行：第一个线程先运行一段时间，然后前两个线程会并发地运行一段时间，只有到了最后，所
     * 有线程才会一起并发执行。（在线程结束运行时存在同样的问题：第一个运行的线程将提前完成。）
     * 在初始化 CyclicBarrier 是将计数值指定为工作者线程的数量再加1，并在运行开始和结束时，使
     * 工作者线程和测试线程都在这个栅栏处等待。这能确保所有线程在开始执行任何工作之前，都首先执行
     * 到同一个位置。PutTakeTest 使用这项技术来协调工作者线程的启动和停止，从而产生更多的并发
     * 交替操作。我们仍然无法确保调度器不会采用串行方式来执行每个线程，但只要这些线程的执行时间足
     * 够长，就能降低调度机制对结果的不利影响。
     * PutTakeTest 使用了一个确定性的结束条件，从而在判断测试何时完成时不需要在线程之间执行额外
     * 的协调。test 方法将启动相同数量的生产者和消费者线程，他们将分别插入和取出相同数量的元素，
     * 因此添加与删除的总数相同。
     */
    @Test
    public void test() {
        new PutTakeTest(10, 10, 100000).test();
    }

    /**
     * 由于并发代码中的大多数错误都是一些低概率事件，所以更多的交替操作才能更容易发现问题，
     * 通过在两个操作之间加入 Thread.yield() 来让线程进行更多的切换。(这项技术与具体平台
     * 有关，因为JVM可以将 Thread.yield 作为一个空操作。如果使用一个睡眠较短的 sleep，
     * 那么虽然更慢些，但却更可靠)
     */
    @Test
    public void testAlternately() {
        //操作1
//        operation1();
        Thread.yield();
        //操作2
//        operation2();
    }

}
