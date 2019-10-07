package com.leisurexi.concurrent.atomic;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: leisurexi
 * Date: 2019-10-07
 * Time: 10:12 下午
 */
public class AtomicIntegerFieldUpdaterDemo {

    class DemoData {
        public volatile int value1 = 1;
        volatile int value2 = 2;
        protected volatile int value3 = 3;
        private volatile int value4 = 4;
    }

    AtomicIntegerFieldUpdater<DemoData> getUpdate(String filedName) {
        return AtomicIntegerFieldUpdater.newUpdater(DemoData.class, filedName);
    }

    void doit() {
        DemoData data = new DemoData();
        System.out.println("1===>" + getUpdate("value1").getAndSet(data, 10));
        System.out.println("2===>" + getUpdate("value2").incrementAndGet(data));
        System.out.println("3===>" + getUpdate("value3").decrementAndGet(data));
        //因为value4是私有修饰符，因此通过反射是不能直接修改该值得
        System.out.println("4===>" + getUpdate("value4").compareAndSet(data, 4, 5));
    }

    public static void main(String[] args) {
        new AtomicIntegerFieldUpdaterDemo().doit();
    }

}
