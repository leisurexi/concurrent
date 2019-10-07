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

    /**
     * AtomicIntegerFieldUpdater/AtomicLongFieldUpdater/AtomicReferenceFieldUpdater是基于反射的原子更新字段的值。
     * 1.更新类的字段(属性)必须使用public volatile修饰符。
     * 2.字段的修饰符是与调用者与操作对象字段的关系一致。也就是说调用者能够直接操作对象字段，那么就可以反射进行原子操作。
     * 但是对于父类的字段，子类是不能直接操作的，尽管子类可以访问父类的字段。
     * 3.只能是实例变量，不可以是类变量，也就是说不能加static关键字。
     * 4.只能是可修改变量，不能使final变量，因为final的语义就是不可修改。实际上final的语义和volatile是有冲突的，这两个关键字不能同时存在。
     */
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
