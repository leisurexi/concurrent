package com.leisurexi.concurrent.cache;

import java.math.BigInteger;

/**
 * @author: leisurexi
 * @date: 2020-02-15 11:08
 * @description:
 * @since JDK 1.8
 */
public class ExpensiveFunction implements Computable<String, BigInteger> {

    @Override
    public BigInteger compute(String arg) throws InterruptedException {
        return new BigInteger(arg);
    }

}
