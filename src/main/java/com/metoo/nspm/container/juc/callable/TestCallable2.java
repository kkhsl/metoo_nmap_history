package com.metoo.nspm.container.juc.callable;

import java.util.concurrent.Callable;

public class TestCallable2 implements Callable<Object> {

    @Override
    public Object call() throws Exception {
        System.out.println(Thread.currentThread().getName());
        Thread.sleep(100);
        return "";
    }
}
