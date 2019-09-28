package com.leisurexi.concurrent.util;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * Description: CyclicBarrier实战 银行流水处理服务类
 * 业务场景: 用一个Excel保存了用户所有银行流水，每个sheet保存一个账户近一年的每笔银行流水，都执行完之后，
 * 得到每个sheet的日均银行流水，最后，再用barrierAction使用这些线程的计算结果，计算出整个Excel的日均银行流水
 * User: leisurexi
 * Date: 2019-09-27
 * Time: 21:45
 */
public class BankWaterService implements Runnable {

    //创建4个屏障，处理完之后执行当前类的run方法
    private CyclicBarrier cyclicBarrier = new CyclicBarrier(4, this);

    //假设只有4个sheet，所有启动4个线程
    private Executor executor = Executors.newFixedThreadPool(4);

    //保存每个sheet计算出的银流结果
    private ConcurrentHashMap<String, Integer> sheetBankWaterCount = new ConcurrentHashMap<>();

    public void count() {
        for (int i = 0; i < 4; i++) {
             executor.execute(() -> {
                 //计算当前sheet的银流数据
                 sheetBankWaterCount.put(Thread.currentThread().getName(), 1);
                 System.out.println(1);
                 //银流计算完成，插入一个屏障
                 try {
                     cyclicBarrier.await();
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 } catch (BrokenBarrierException e) {
                     e.printStackTrace();
                 }
             });
        }
    }

    @Override
    public void run() {
        int result = 0;
        //汇总每个sheet计算出的结果
        for (Map.Entry<String, Integer> sheet : sheetBankWaterCount.entrySet()) {
            result += sheet.getValue();
        }
        //将结果输出
        sheetBankWaterCount.put("result", result);
        System.out.println(result);
    }

    public static void main(String[] args) {
        BankWaterService bankWaterService = new BankWaterService();
        bankWaterService.count();
    }

}
