package com.leisurexi.concurrent.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created with IntelliJ IDEA.
 * Description: FutureTask示例，使用FutureTask来提前加载稍后需要的数据
 * User: leisurexi
 * Date: 2019-10-03
 * Time: 22:04
 */
public class Preloader {

    ProductInfo loadProductInfo() {
        //模拟耗时操作
        SleepUtils.second(5);
        return null;
    }

    private final FutureTask<ProductInfo> futureTask = new FutureTask<>(() -> loadProductInfo());

    private final Thread thread = new Thread(futureTask);

    public void start() {
        thread.start();
    }

    public ProductInfo get() throws InterruptedException {
        try {
            return futureTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    interface ProductInfo {

    }

    public static void main(String[] args) throws InterruptedException {
        Preloader preloader = new Preloader();
        preloader.start();
        preloader.get();
    }

}
