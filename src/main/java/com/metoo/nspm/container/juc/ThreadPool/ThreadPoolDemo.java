package com.metoo.nspm.container.juc.ThreadPool;

import org.apache.poi.ss.formula.functions.T;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolDemo {

    private static Logger log = LoggerFactory.getLogger(ThreadPoolDemo.class);

    private static int POOL_SIZE = Integer.max(Runtime.getRuntime().availableProcessors(), 0);
    private static ExecutorService exe = Executors.newFixedThreadPool(POOL_SIZE);

    public static void main(String[] args) {
        for (int i = 1; i <= 1; i++){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    test2();
                }
            }).start();

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    test1();
//                }
//            }).start();

            log.info("for"+i+"程结束");
        }
        log.info("主线程结束");

//        test2();
    }

    @Test
    public static void test1(){
        for (int i = 1; i <= 10; i++){
            exe.execute(new Runnable() {
                @Override
                public void run() {

                    System.out.println("Thread-1:");
                }
            });
        }
    }

    @Test
    public static void test2(){
        AtomicInteger num =  new AtomicInteger();
        AtomicInteger num1 =  new AtomicInteger();
        List list = new ArrayList();
        for (int i = 1; i <= 10; i++){
            exe.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        synchronized (list){
                            list.add(num1.getAndIncrement());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Thread-2: " + num.getAndIncrement());
                }
            });
        }

        if(exe != null){
            exe.shutdown();
        }
        while (true){
            if (exe.isTerminated()) {
                System.out.println("关闭");
                System.out.println(list);
                break;
            }
        }
    }
}
