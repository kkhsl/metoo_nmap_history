package com.metoo.nspm.container.juc.runnerble;

public class MyRunnerble implements Runnable {

    @Override
    public void run() {
        System.out.println("自定义Runnerble子线程");
    }
}
