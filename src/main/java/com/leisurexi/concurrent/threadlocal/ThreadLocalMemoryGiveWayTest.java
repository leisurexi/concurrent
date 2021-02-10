package com.leisurexi.concurrent.threadlocal;

import lombok.Data;

/**
 * @author: leisurexi
 * @date: 2020-09-03 10:21 上午
 */
public class ThreadLocalMemoryGiveWayTest {

    public static void main(String[] args) {
        test();
        System.gc();
    }

    private static void test() {
        Content content = new Content();
        ThreadLocal<Content> threadLocal = new ThreadLocal<>();
        threadLocal.set(content);
        threadLocal.remove();
//        threadLocal = null;
    }

    @Data
    static class Content {
        private byte[] data = new byte[5 * 1024 * 1024];
    }

}
