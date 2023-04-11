package com.metoo.nspm.container.juc.thread.test;

import lombok.extern.slf4j.Slf4j;

/**
 * 线程死锁
 *
 */
@Slf4j
public class TestDeadLock {

    // 测试对象锁
    public static void main(String[] args) {

        Object A = new Object();
        Object B = new Object();

        new Thread(() -> {
            synchronized (A){
                log.info("Lock A ...");
                try {
                    Thread.sleep(1000);
                    synchronized (B){
                        log.info("Lock B ...");
                        log.info("操作");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("执行其他代码");
            }
        }, "t1").start();

        new Thread(() -> {
            synchronized (B){
                log.info("Lock B ...");
                try {
                    Thread.sleep(1000);
                    synchronized (A){
                        log.info("Lock A ...");
                        log.info("操作");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("执行其他代码");
            }
        }, "t2").start();

    }
}
