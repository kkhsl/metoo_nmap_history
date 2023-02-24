package com.metoo.nspm.container.feature;

import com.metoo.nspm.container.java.collection.Collection;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class ListDemo {

    /**
     * 存字符排序
     */
    @Test
    public void strSort(){
        ArrayList<String> sites = new ArrayList<String>();
        sites.add("aa3/2obao");
        sites.add("aa3/1obao");
        sites.add("aa3/102iki");
        sites.add("aa3/101iki");
        sites.add("dunoob");
        sites.add("beibo");
        sites.add("foogle");
        Collections.sort(sites);  // 字母排序
        for (String i : sites) {
            System.out.println(i);
        }
    }

    /**
     * 数字排序
     */
    @Test
    public void numSort(){
        ArrayList<Integer> myNumbers = new ArrayList<Integer>();
        myNumbers.add(33);
        myNumbers.add(15);
        myNumbers.add(20);
        myNumbers.add(34);
        myNumbers.add(8);
        myNumbers.add(12);


        System.out.println("排序");
        Collections.sort(myNumbers);  // 数字排序

        for (int i : myNumbers) {
            System.out.println(i);
        }

        System.out.println("取反");
        Collections.reverse(myNumbers);

        for (int i : myNumbers) {
            System.out.println(i);
        }
    }

    /**
     * List集合排序 - Java 7
     */
    @Test
    public void sortJava7(){
        List<String> list = new ArrayList<>();
        list.add("aa3/2obao");
        list.add("aa3/1obao");
        list.add("aa3/102iki");
        list.add("aa3/101iki");
        list.add("dunoob");
        list.add("beibo");
        list.add("foogle");
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        System.out.println("打印");
        for (String i : list) {
            System.out.println(i);
        }
    }

    /**
     * List 集合排序 Java 8
     */
    @Test
    public void sortJava8(){
        List<String> list = new ArrayList<>();
        list.add("aa3/2obao");
        list.add("aa3/1obao");
        list.add("aa3/102iki");
        list.add("aa3/101iki");
        list.add("dunoob");
        list.add("beibo");
        list.add("foogle");
        Collections.sort(list, (o1, o2) -> o1.compareTo(o2));
    }
}
