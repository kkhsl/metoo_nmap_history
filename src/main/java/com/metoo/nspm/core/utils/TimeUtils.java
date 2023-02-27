package com.metoo.nspm.core.utils;

import org.junit.Test;
import org.springframework.util.StopWatch;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class TimeUtils {


    @Test
    public void test() throws InterruptedException {
        StopWatch watch = new StopWatch();
        // 开始时间
        watch.start("Test1");
        // 执行时间（1s）
        Thread.sleep(1000);
        // 结束时间
        // 统计执行时间（秒）
        watch.stop();

        watch.start("Test2");
        Thread.sleep(1000);
        watch.stop();

        watch.start("Test3");
        Thread.sleep(1000);
        watch.stop();

        System.out.println("任务数用时：" + watch.getTotalTimeMillis() + " ms");
        System.out.println("任务数：" + watch.getTaskCount());
        System.out.println("任务执行的百分比：" + watch.prettyPrint());
    }

    @Test
    public void test1() throws InterruptedException {
        StopWatch watch = new StopWatch();
        // 开始时间
        watch.start();
        // 执行时间（1s）
        Thread.sleep(1000);
        System.out.println("第一段耗时：");
        Thread.sleep(1000);
        watch.start("Test3");
        Thread.sleep(1000);

    }



    @Test
    public void test2(){
        Instant start = Instant.now();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Instant end = Instant.now();

        Duration duration = Duration.between(start, end);

        System.out.println("millis = " + duration.toMillis());
    }
}
