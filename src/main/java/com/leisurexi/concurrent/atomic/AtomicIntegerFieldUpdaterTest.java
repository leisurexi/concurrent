package com.leisurexi.concurrent.atomic;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Created with IntelliJ IDEA.
 * Description: 原子更新字段类
 * User: leisurexi
 * Date: 2019-09-26
 * Time: 22:51
 */
public class AtomicIntegerFieldUpdaterTest {

    /**
     * 要想原子地更新字段类需要两步。
     * 第一步: 因为原子更新字段类(如 AtomicIntegerFieldUpdater)都是抽象类，每次使用的时候必须使用静态方法newUpdater()创建
     * 更新器，并且需要设置想要更新的类和属性。
     * 第二步: 更新类的字段(属性)必须使用public volatile修饰符。
     */

    //创建原子更新器，并设置需要更新的对象类和对象的属性
    private static AtomicIntegerFieldUpdater<User> fieldUpdater = AtomicIntegerFieldUpdater.newUpdater(User.class, "age");

    public static void main(String[] args) {
        //设置柯南的年龄是10岁
        User conan = new User("conan", 10);
        //柯南长了一岁，但是仍然会输出旧的年龄
        System.out.println(fieldUpdater.getAndIncrement(conan));
        //输出柯南现在的年龄
        System.out.println(fieldUpdater.get(conan));
    }

    private static class User {
        private String name;
        //要更新的字段必须用public volatile修饰
        public volatile int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

}
