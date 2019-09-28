package com.leisurexi.concurrent.util;

import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * Description: Exchanger(交换者)是一个用于线程间协作的工具类。Exchanger用于进行线程间的数据交换。它提供一个同步点，
 * 在这个同步点，两个线程可以交换彼此的数据。这两个线程通过exchange()方法交换数据，如果第一个线程先执行exchange()方法，
 * 它会一直等待第二个线程也执行exchange()方法，当两个线程都到达同步点时，这两个线程就可以交换数据，将本线程生产出来的数据
 * 传递给对方。
 *
 * 应用场景:
 * Exchanger可以用于遗传算法，遗传算法里需要选出两个人作为交配对象，这时候会交换两人的数据，并使用交叉规则得出2个
 * 交配结果。
 * Exchanger也可以用于校队工作，比如我们需要将纸质银行流水通过人工方式录入成电子银行流水，为了避免错误，采用AB岗两人
 * 进行录入，录入到Excel之后，系统需要加载这两个Excel，并对两个Excel数据进行校队，看看是否录入一致。
 *
 * User: leisurexi
 * Date: 2019-09-28
 * Time: 11:27
 */
public class ExchangerTest {

    private static final Exchanger<String> EXCHANGER = new Exchanger<>();

    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);

    /**
     * 如果线程有一个没有执行exchange()方法，则会一致等待。
     */
    public static void main(String[] args) {
        threadPool.execute(() -> {
            String a = "银行流水A"; //A员工录入银行流水数据
            try {
                EXCHANGER.exchange(a);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        threadPool.execute(() -> {
            String b = "银行流水B"; //B员工录入银行流水数据
            try {
                String a = EXCHANGER.exchange(b);
                System.out.println("A和B录入数据是否一致: " + a.equals(b) + ", " +
                        "A录入的数据是: " + a + ", B录入的数据是: " + b);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        threadPool.shutdown();
    }

}
