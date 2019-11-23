package com.leisurexi.concurrent.jmm;

/**
 * @author: leisurexi
 * @date: 2019-11-23 9:34 下午
 * @description: 单例模式实现: 借助内部类来实现
 * @since JDK 1.8
 */
public class InstanceFactory {

    /**
     * JVM在类的初始化阶段（即在Class被加载后，且被线程使用之前），会执行类的初始化。在执行类的初始化期间，
     * JVM会去获取一个锁。这个锁可以同步多个线程对同一个类的初始化。
     *
     * 初始化一个类，包括执行这个类的静态初始化和初始化在这个类中声明的静态字段。根据Java语言规范，在首次
     * 发生下列任意一种情况时，一个类或接口类型T将被立即初始化。
     * 1.T是一个类，而且一个T类型的实例被创建。
     * 2.T是一个类，且T中声明的一个静态方法被调用。
     * 3.T中声明的一个静态字段被赋值。
     * 4.T中声明的一个静态字段被使用，而且这个字段不是常量字段。
     * 5.T是一个顶级类，而且一个断言语句嵌套在T内部执行。
     *
     * 在该实例中，首次执行getInstance()方法的线程将导致InstanceHolder类被初始化（符合情况4）。
     */

    private static class InstanceHolder {
        public static Instance instance = new Instance();
    }

    public static Instance getInstance() {
        return InstanceHolder.instance;
    }

}
