package com.leisurexi.concurrent.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: leisurexi
 * Date: 2019-09-15
 * Time: 10:58
 */
public class MultiThread {

    public static void main(String[] args) {
        //获取Java线程关联MXBean
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        //不需要获取同步的monitor和synchronized信息，仅获取线程和线程堆栈信息
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
        for (ThreadInfo threadInfo : threadInfos) {
            System.out.println(threadInfo);
        }
    }

}
