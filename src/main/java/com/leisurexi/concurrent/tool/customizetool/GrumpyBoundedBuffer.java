package com.leisurexi.concurrent.tool.customizetool;

import com.leisurexi.concurrent.util.SleepUtils;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: leisurexi
 * @date: 2020-02-19 19:36
 * @description: 第一个简单的有界缓存实现。put和take方法都进行了同步以确保对缓存状态的独占访问，
 * 因为这两个方法在访问缓存时都采用 “先检查再运行” 的逻辑策略。尽管这种方法实现起来很简单，但使用
 * 起来却并非如此。异常应该用于发生异常条件的情况中。缓存已满并不是一个异常条件，所有在使用时调用者
 * 必须做好捕获异常的准备，并且在每次缓存操作时都需要重试。
 * @since JDK 1.8
 */
@Slf4j
public class GrumpyBoundedBuffer<V> extends BaseBoundedBuffer<V> {

    protected GrumpyBoundedBuffer(int capacity) {
        super(capacity);
    }

    public synchronized void put(V v) throws IllegalStateException {
        if (isFull()) {
            throw new IllegalStateException("容器已满，不可放入元素");
        }
        doPut(v);
    }

    public synchronized V take() throws IllegalStateException {
        if (isEmpty()) {
            throw new IllegalStateException("容器为空，不可取出元素");
        }
        return doTake();
    }

    public static void main(String[] args) {
        GrumpyBoundedBuffer<Integer> buffer = new GrumpyBoundedBuffer<>(10);
        buffer.put(1);
        buffer.put(2);
        while (true) {
            try {
                log.info(String.valueOf(buffer.take()));
            } catch (IllegalStateException e) {
                e.printStackTrace();
                SleepUtils.second(1);
            }
        }
    }


}
