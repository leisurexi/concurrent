package com.leisurexi.concurrent.thread.threadpool;

/**
 * @author: leisurexi
 * @date: 2019-11-28 8:25 下午
 * @description:
 * @since JDK 1.8
 */
public interface ThreadPool<Job extends Runnable> {

    /**
     * 执行一个Job，这个Job需要实现Runnable
     *
     * @param job
     */
    void execute(Job job);

    /**
     * 关闭线程池
     */
    void shutdown();

    /**
     * 增加工作者线程
     *
     * @param num
     */
    void addWorkers(int num);

    /**
     * 减少工作者线程
     *
     * @param num
     */
    void removeWorkers(int num);

    /**
     * 得到正在等待执行的任务数量
     *
     * @return
     */
    int getJobSize();

}
