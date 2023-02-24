package com.metoo.nspm.container.primary.data.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description
 *  基本数据类型: private int i = 1;
 *
 *  包装类: private Integer ii = 1;
 *      使用泛型时，需要用到包装类，因为容器都是装Object类的
 *      POJO中使用包装类：原因是数据库查询可能出现Null值，使用基本类型，因为要自动拆箱（包装类转换为基本类型，导致抛出NullPointException异常）
 *  区别：
 *      包装类可以用作泛型；基础数据类型不可以
 *      包装类型可以为Null；基础数据类型不可以
 *      存储方式位置不同
 *          包装类型是将对象存储在栈堆中；基本类型是将变量值存储在栈中；
*
 *
 */
public class BasicDemo {


    private int i = 1;

    private Integer ii = 1;

    private int a = Integer.parseInt(null);

    private Integer b = null;

    List<Integer> list = new ArrayList();

    Map<String, Integer> map = new HashMap();

    public static void main(String[] args) {
        Integer num1=10;
        Integer num2=20;
        Integer num3=10;
        System.out.println(Integer.compare(num1, num2)); //-1
        System.out.println(Integer.compare(num1, num3)); //0
        System.out.println(Integer.compare(num2, num1)); //1
        System.out.println(Integer.max(num1, num2));//20
        System.out.println(Integer.min(num1, num2));//10
    }


}
