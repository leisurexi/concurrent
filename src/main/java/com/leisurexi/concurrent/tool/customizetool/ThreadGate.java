package com.leisurexi.concurrent.tool.customizetool;

/**
 * @author: leisurexi
 * @date: 2020-02-19 20:57
 * @description:
 * @since JDK 1.8
 */
public class ThreadGate {

    private boolean isOpen;
    private int generation;

    public synchronized void close() {
        isOpen = false;
    }

    public synchronized void open() {
        ++generation;
        isOpen = true;
        notifyAll();
    }

    public synchronized void await() throws InterruptedException {
        int arrivalGeneration = this.generation;
        while (!isOpen && arrivalGeneration == generation) {
            wait();
        }
    }

}
