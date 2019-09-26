package com.leisurexi.concurrent.lock;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: leisurexi
 * Date: 2019-09-15
 * Time: 10:09
 */
public class UnsafeLazyInitialization {

    private static UnsafeLazyInitialization instance;

    public static UnsafeLazyInitialization getInstance() {
        if (instance == null) {
            instance = new UnsafeLazyInitialization();
        }
        return instance;
    }

}
