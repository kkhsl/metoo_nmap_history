package com.metoo.nspm.container.feature;

import com.metoo.nspm.core.utils.query.PageInfo;
import com.metoo.nspm.entity.nspm.Project;
import com.metoo.nspm.entity.nspm.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @descriptin；Java5 新特新
 *      泛型提供了编译时类型安全检测机制。该机制允许程序员在编译时检测到非法的类型；
 *      泛型的本质是参数化类型，也就是说所操作的数据类型被指定为一个参数
 *
 *      泛型标记符：
 *          E - Element（在集合中使用，因为集合中存放的是元素）
 *          T - Type（Java 类）
 *          K - Key（键）
 *          V - Value（值）
 *          ? - 表示不确定的Java类型
 *     泛型集合
*      泛型方法
 *     泛型类
 *     泛型接口
*      优点：
 *         类型安全
 *         消除强制类型转换
 *         更高的运行效率
 *         潜在的性能效益
 *
 */
public class GenericityDemo {

    @Test
    public void testPrintArray(){
        // 创建不同类型数组
        Integer[] intArray = {1, 2, 3, 4, 5};
        Double[] doubleArray = {1.11, 1.1, 1.2, 1.3, 1.4};
        Character[] charArray = {'A', 'B', 'C', 'd', 'd'};

        System.out.println("整型数组元素为：");
        this.printArray(intArray);

        System.out.println("\n双精度型数组元素为");
        this.printArray(doubleArray);

        System.out.println("\n字符型数组元素为");
        this.printArray(charArray);

    }
    /**
     * @description 定义泛型方法
     * @param inputArray
     * @param <E>
     */
    public <E> void printArray(E[] inputArray){
        for(E e : inputArray){
            System.out.printf("%s", e);
        }
        System.out.println();
    }

    @Test
    public void dataTest(){
        List<String> name = new ArrayList<String>();
        List<Integer> age = new ArrayList<Integer>();
        List<Number> number = new ArrayList<Number>();
        List<Object> object = new ArrayList<Object>();

        name.add("icon");
        age.add(18);
        number.add(314);

        getData(name);
        getData(age);
        getData(number);

//        getDataExtends(name); // 参数已经限定了参数泛型上限为 Number
        getDataExtends(age);
        getDataExtends(number);


//        getDataSuper(name);
//        getDataSuper(age);
//        getDataSuper(number);
        getDataSuper(object);// 序列化下限


    }
    /**
     * 类型通配符 -
     * @param data
     */
    public void getData(List<?> data){
        System.out.println("Data：" + data.get(0));
    }
    /**
     * 类型通配符 - 上限
     * @param data
     */
    public void getDataExtends(List<? extends Number> data){
        System.out.println("Data：" + data.get(0));
    }
    /**
     * 类型通配符 - 下限
     * @param data
     */
    public void getDataSuper(List<? super Number> data){
        System.out.println("Data：" + data.get(0));
    }

}
