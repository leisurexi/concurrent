package com.leisurexi.concurrent.atomic;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created with IntelliJ IDEA.
 * Description: 原子更新引用类型示例
 * User: leisurexi
 * Date: 2019-09-26
 * Time: 22:45
 */
public class AtomicReferenceTest {

    public static AtomicReference<User> userAtomicReference = new AtomicReference<>();

    public static void main(String[] args) {
        User user = new User("leisurexi", 22);
        userAtomicReference.set(user);
        User updateUser = new User("xidada", 18);
        userAtomicReference.compareAndSet(user, updateUser);
        System.out.println(userAtomicReference.get().getName());
        System.out.println(userAtomicReference.get().getAge());
    }

    static class User {
        private String name;
        private int age;

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
