package com.leisurexi.concurrent.atomic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: leisurexi
 * @date: 2019-12-01 1:25 下午
 * @description: 原子更新引用类型示例
 * @since JDK 1.8
 */
@Slf4j
public class AtomicReferenceTest {

    private static AtomicReference<User> userAtomicReference = new AtomicReference<>();

    public static void main(String[] args) {
        User user = new User("leisurexi", 21);
        userAtomicReference.set(user);
        User updateUser = new User("xidada", 22);
        userAtomicReference.compareAndSet(user, updateUser);
        log.info("姓名: {}", userAtomicReference.get().getName());
        log.info("年龄: {}", userAtomicReference.get().getAge());
    }

    @Getter
    @AllArgsConstructor
    private static class User {
        private String name;
        private int age;
    }

}
