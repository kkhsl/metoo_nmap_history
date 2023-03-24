package com.metoo.nspm.container.juc.ThreadPool;

import org.apache.poi.ss.formula.functions.T;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@RequestMapping("/threadpool")
@RestController
public class ThreadPoolDemo {

    private static Logger log = LoggerFactory.getLogger(ThreadPoolDemo.class);

    private static int POOL_SIZE = Integer.max(Runtime.getRuntime().availableProcessors(), 0);
    private static ExecutorService exe = Executors.newFixedThreadPool(POOL_SIZE);

    public static void main(String[] args) {
//        for (int i = 1; i <= 1; i++){
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    test2();
//                }
//            }).start();
//
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
////                    test2();
////                }
////            }).start();
////
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
////                    test1();
////                }
////            }).start();
//
//            log.info("for"+i+"程结束");
//        }
//        log.info("主线程结束");

        test2();
    }

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

    public static void test2(){

        int POOL_SIZE = Integer.max(Runtime.getRuntime().availableProcessors(), 0);
        ExecutorService exe = Executors.newFixedThreadPool(POOL_SIZE);


        AtomicInteger num =  new AtomicInteger();
        AtomicInteger num1 =  new AtomicInteger();
        List list = new ArrayList();
        for (int i = 1; i <= 20; i++){
            exe.execute(new Runnable() {
                @Override
                public void run() {
//                    try {
//                        Thread.sleep(1000);
//                    synchronized (this){
//                        list.add(num1.getAndIncrement());
//                    }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

//                    synchronized (this){
//                        list.add(1);
//                    }

                    list.add(1);

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

    @GetMapping("/test3")
    public void test3(){
        List list = new ArrayList();
        for (int i = 1; i <= 10; i++){
            list.add(i);
            System.out.println("Thread-2: " + i);
        }
        System.out.println(list);
    }


    @Test
    public void test4(){
        AtomicInteger num =  new AtomicInteger();
        AtomicInteger num1 =  new AtomicInteger();
        List list = new ArrayList();
        for (int i = 1; i <= 3; i++){
            exe.execute(new Runnable() {
                @Override
                public void run() {
//                    try {
//                        Thread.sleep(1000);
//                    synchronized (this){
//                        list.add(num1.getAndIncrement());
//                    }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

//                    synchronized (this){
//                        list.add(1);
//                    }

                    list.add(1);

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
