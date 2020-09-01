package com.leisurexi.concurrent.cache;

/**
 * @author: leisurexi
 * @date: 2020-02-15 11:07
 * @description:
 * @since JDK 1.8
 */
public interface Computable<A, V> {

    V compute(A arg) throws InterruptedException;

}
