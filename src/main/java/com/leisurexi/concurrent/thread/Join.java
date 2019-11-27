package com.leisurexi.concurrent.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: leisurexi
 * @date: 2019-11-27 9:33 下午
 * @description: 利用Thread.join()方法，让10个线程顺序打印1到10
 * @since JDK 1.8
 */
@Slf4j
public class Join {

    public static void main(String[] args) {
        Thread previous = Thread.currentThread();
        for (int i = 1; i <= 10; i++) {
            Thread thread = new Thread(new Domino(previous, i));
            thread.start();
            previous = thread;
        }
    }

    static class Domino implements Runnable {

        private Thread thread;
        private int i;

        public Domino(Thread thread, int i) {
            this.thread = thread;
            this.i = i;
        }

        @Override
        public void run() {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info(String.valueOf(i));
        }
    }

}
