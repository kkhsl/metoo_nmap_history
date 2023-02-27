package com.metoo.nspm.core.utils;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ApacheTimeUtils {


    @Test
    public void test() throws InterruptedException {
        //创建后立即start，常用
        StopWatch watch = StopWatch.createStarted();
        // StopWatch watch = new StopWatch();
        // watch.start();

        Thread.sleep(1000);

        watch.split();

        System.out.println("从开始到第一个切入点运行时间：" + watch.getSplitTime());

//        watch.reset();
//        watch.start();
        Thread.sleep(3000);

        watch.split();

        System.out.println("从开始到第二个切入点运行时间：" + watch.getSplitTime());

        Thread.sleep(3000);

        System.out.println("结束时间：" + watch.getTime());
    }

    @Test
    public void test2() throws InterruptedException {
        //创建后立即start，常用
        StopWatch watchAll = StopWatch.createStarted();
        StopWatch watch = new StopWatch();

        watch.start();
        Thread.sleep(1000);
        watch.stop();
        System.out.println("从开始到第一个切入点运行时间：" + watch.getTime(TimeUnit.SECONDS) + " 秒.");

        watch.reset();
        watch.start();
        Thread.sleep(3000);
        watch.stop();
        System.out.println("从开始到第二个切入点运行时间：" + watch.getTime(TimeUnit.SECONDS) + " 秒.");

        watch.reset();
        watch.start();
        Thread.sleep(3000);
        watch.stop();
        watchAll.stop();
        System.out.println("结束时间：" +  + watchAll.getTime(TimeUnit.SECONDS) + " 秒.");
    }
}
