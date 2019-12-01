package com.leisurexi.concurrent.atomic;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @author: leisurexi
 * @date: 2019-12-01 1:21 下午
 * @description:
 * @since JDK 1.8
 */
@Slf4j
public class AtomicIntegerArrayTest {

    private static int[] value = new int[]{1, 2};

    /**
     * 需要注意的是，数组value通过构造方法传递进去，然后AtomicIntegerArray会将当前数组复制一份，
     * 所以当AtomicIntegerArray对内部的数组元素进行修改时，不会影响传入的数组。
     */
    private static AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(value);

    public static void main(String[] args) {
        atomicIntegerArray.getAndSet(0, 3);
        log.info(String.valueOf(atomicIntegerArray.get(0)));
        log.info(String.valueOf(value[0]));
    }

}
