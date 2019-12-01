package com.leisurexi.concurrent.atomic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author: leisurexi
 * @date: 2019-12-01 1:31 下午
 * @description: 原子更新字段类示例
 * @since JDK 1.8
 */
@Slf4j
public class AtomicIntegerFieldUpdaterTest {

    /**
     * 要想原子地更新字段类需要两步。
     * 第一步: 因为原子更新字段类都是抽象类，每次使用的时候必须使用静态
     * 方法newUpdater()创建一个更新器，并且需要设置想要更新的类和属性。
     * 第二步: 更新类的字段（属性）必须使用 public volatile 修饰符。
     */

    /**
     * 创建原子更新器，并设置需要更新的对象类和对象的属性
     */
    private static AtomicIntegerFieldUpdater<User> userFieldUpdater =
            AtomicIntegerFieldUpdater.newUpdater(User.class, "age");

    public static void main(String[] args) {
        //设置柯南的年龄是10岁
        User conan = new User("conan", 10);
        log.info("柯南长了一岁，但是仍然会输出旧的年龄: {}", userFieldUpdater.getAndIncrement(conan));
        log.info("柯南现在的年龄: {}", userFieldUpdater.get(conan));
    }

    @Getter
    @AllArgsConstructor
    @ToString
    private static class User {
        private String name;
        public volatile int age;
    }

}
