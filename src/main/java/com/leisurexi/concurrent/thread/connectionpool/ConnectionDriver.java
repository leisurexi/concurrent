package com.leisurexi.concurrent.thread.connectionpool;

import com.leisurexi.concurrent.util.SleepUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

/**
 * @author: leisurexi
 * @date: 2019-11-27 9:56 下午
 * @description: 由于java.sql.Connection是一个接口，最终的实现是由数据库驱动提供方来实现的，考虑到只是个示例，
 * 我们通过动态代理构造了一个Connection，该Connection的代理实现仅仅是在commit()方法调用休眠100毫秒。
 * @since JDK 1.8
 */
public class ConnectionDriver {

    static class ConnectionHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("commit")) {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            return null;
        }
    }

    /**
     * 创建一个Connection的代理，在commit是休眠100毫秒
     * @return
     */
    public static final Connection createConnection() {
        return (Connection) Proxy.newProxyInstance(ConnectionDriver.class.getClassLoader(),
                new Class[]{Connection.class},
                new ConnectionHandler());
    }

}
