package com.metoo.nspm.container.juc.thread.method.priority;

public class Test {

    public static void main(String[] args) {
        for (int i = 1; i <= 3; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int sum = 0;
                    for (int i = 0; i <= 10; i ++){
                        sum += i;
                        System.out.println(Thread.currentThread().getName() + " num: " + i);
                    }
                }
            }).start();
        }
    }
}
