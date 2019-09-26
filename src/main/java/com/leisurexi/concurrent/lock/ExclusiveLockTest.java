package com.leisurexi.concurrent.lock;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: leisurexi
 * Date: 2019-09-23
 * Time: 20:11
 */
public class ExclusiveLockTest {

    static int k = 0;

    public static void main(String[] args) {
        ExclusiveLock lock = new ExclusiveLock();

        for (int i = 0; i < 2; i++) {

            new Thread(() -> {
                lock.lock();
                for (int j = 0; j < 100; j++) {
                    k++;
                }
                lock.unlock();
            }).start();

        }

    }

}
