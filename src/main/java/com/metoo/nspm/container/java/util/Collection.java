package com.metoo.nspm.container.java.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @description 测试
 *
 * @author HKK
 *
 * @create 2023/02/15
 */
public class Collection {

    private static Logger log = LoggerFactory.getLogger(Collection.class);

    // 定义List<String>集合
    private static List<String> LIST = new ArrayList<>();

    static{
        LIST.add("a");
        LIST.add("b");
        LIST.add("c");
        LIST.add("d");
    }

    public static void main(String[] args) {
        log.info(String.valueOf(LIST));
    }


    /**
     *  List集合处理
     *  StreamDemo 流处理
     *  遍历插入数据
     */
    public void traverseList(){

    }
}
