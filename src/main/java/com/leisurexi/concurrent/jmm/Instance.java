package com.leisurexi.concurrent.jmm;

/**
 * @author: leisurexi
 * @date: 2019-11-23 9:28 下午
 * @description: 单例模式的实现: 通过禁止指令重排序，来保证线程安全的延迟初始化
 * @since JDK 1.8
 */
public class Instance {

    private volatile static Instance instance;

    public static Instance getInstance() {
        if (instance == null) {
            synchronized (Instance.class) {
                if (instance == null) {
                    /**
                     * 如果不加volatile关键字修饰变量，这里创建对象并分配内存空间可能会被重排序，
                     * 导致另外一个线程访问到未初始化完成的对象
                     */
                    return new Instance();
                }
            }
        }
        return instance;
    }

}
