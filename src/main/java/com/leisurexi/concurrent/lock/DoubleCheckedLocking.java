package com.leisurexi.concurrent.lock;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: leisurexi
 * Date: 2019-09-15
 * Time: 10:12
 */
public class DoubleCheckedLocking {

    private volatile static DoubleCheckedLocking instance;

    public static DoubleCheckedLocking getInstance() {
        if (instance == null) {
            synchronized (DoubleCheckedLocking.class) {
                if (instance == null) {
                    instance = new DoubleCheckedLocking();
                }
            }
        }
        return instance;
    }

}
