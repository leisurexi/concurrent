package com.leisurexi.concurrent.tool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: leisurexi
 * @date: 2019-12-01 3:14 下午
 * @description: Exchanger(交换者)是一个用于线程间协作的工具类。Exchanger用于线程间的数据交换。它提供了
 * 一个同步点，在这个同步点，两个线程可以交换彼此的数据。这两个线程通过exchange方法，当两个线程都到达同步点时，
 * 这两个线程就可以交换数据，将本线程生产出来的数据传递给对方。
 * @since JDK 1.8
 */
@Slf4j
public class ExchangerTest {

    /**
     * Exchanger可以用于遗传算法，遗传算法里需要选出两个人作为交配对象，这时候会交换两人的数据，并使用交叉规则
     * 得出2个交配结果。Exchanger也可以用于校队工作，比如我们需要将纸制银行流水通过人工的方式录入成电子银行流水，
     * 为了避免错误，采用AB岗两人进行录入，录入到Excel之后，系统需要加载这两个Excel，并对两个Excel数据进行校队。
     */

    private static final Exchanger<String> EXCHANGER = new Exchanger<>();

    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        threadPool.execute(() -> {
            try {
                String a = "银行流水A";
                EXCHANGER.exchange(a);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        threadPool.execute(() -> {
            try {
                String b = "银行流水B";
                String a = EXCHANGER.exchange(b);
                log.info("A和B数据是否一致: {}，A录入的是: {}, B录入的是: {}", a.equals(b), a, b);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        threadPool.shutdown();
    }

}
