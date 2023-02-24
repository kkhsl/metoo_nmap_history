package com.metoo.nspm.container.feature;

import lombok.ToString;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.*;


/**
 * @description Java8 新特性
 * 参考链接：https://blog.csdn.net/MinggeQingchun/article/details/123184273
 *
 * @author HKK
 *
 * @create 2023/02/15
 *
 * @version 1.0
 */
public class StreamDemo {

    Logger logger = LoggerFactory.getLogger(StreamDemo.class);

    // 定义List<String>集合
    private static List<String> LIST = new ArrayList<>();
    private static List<Integer> INTLIST = new ArrayList<>();
    private static List<String> DISORDERLIST = new ArrayList<>();
    private static List<String> DISTINCTLIST = new ArrayList<>();
    private static Integer[] ARRAY = new Integer[]{1, 2, 3, 4, 5};
    private static String[] ARRAY1 = new String[]{"a", "b", "c"};
    private static List<Inner> INNERLIST = new ArrayList<Inner>();
    private static Map<String, Object> MAP = new HashMap();

    static {
        LIST.add("a");
        LIST.add("b");
        LIST.add("c");
        LIST.add("d");
    }

    static {
        INTLIST.add(1);
        INTLIST.add(2);
        INTLIST.add(3);
        INTLIST.add(4);
    }

    static {
        DISORDERLIST.add("ab");
        DISORDERLIST.add("cc");
        DISORDERLIST.add("db");
        DISORDERLIST.add("bg");
        DISORDERLIST.add("eg");
    }

    static {
        DISTINCTLIST.add("ab");
        DISTINCTLIST.add("ab");
        DISTINCTLIST.add("cc");
        DISTINCTLIST.add("db");
        DISTINCTLIST.add("cc");
        DISTINCTLIST.add("bg");
        DISTINCTLIST.add("eg");
    }

    static {
        // 定义内部类bean数据
        Inner innser_1 = new Inner("曹操", 26, "男");
        Inner innser_2 = new Inner("刘备", 26, "女");
        Inner innser_3 = new Inner("关羽", 24, "男");
        Inner innser_4 = new Inner("张飞", 21, "女");
        Inner innser_5 = new Inner("张飞", 24, "女");
        Inner innser_6 = new Inner("张飞", 24, "女");
        INNERLIST.add(innser_1);
        INNERLIST.add(innser_2);
        INNERLIST.add(innser_3);
        INNERLIST.add(innser_4);
        INNERLIST.add(innser_5);
        INNERLIST.add(innser_6);
    }

    static {
        MAP.put("age", 26);
        MAP.put("name", "HKK");
    }

    public static void main(String[] args) {

    }

    // ---------- 创建 ----------


    /**
     * Stream创建
     * @return
     */
    public Stream stream() {
        Stream stream = Stream.of(1, 2, 3, 4, 5);
        return stream;
    }

    /**
     * collection
     */
    public Stream listStream() {
        Stream<String> stream = LIST.stream();
        return stream;
    }

    /**
     * Array
     */
    public Stream arrayStream() {
        Stream<Integer> stream = Arrays.stream(ARRAY);
        return stream;
    }

    public Stream fileStream() {
        try {
            Stream<String> fileStream = Files.lines(Paths.get("E:\\java\\project\\stream\\stream.txt"), Charset.defaultCharset());
            return fileStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 文件创建
     */
    public void fileLineStream() {
        try {

//            File file = new File("E:\\java\\project\\stream\\stream.txt");
            String file = "E:\\java\\project\\stream\\stream.txt";
            Files.lines(Paths.get(file)).skip(0).limit(1).collect(Collectors.toList());
            out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 函数创建
     *
     * @return
     */
    public Stream iteratorStream() {
        Stream stream = Stream.iterate(0, n -> n + 2).limit(10);
        return stream;
    }

    // ---------- 中间操作符 ----------

    /**
     * filter：
     * 用于通过设置的条件过滤出元素
     */
    @Test
    public void filterStream() {
        List list = DISORDERLIST.stream().filter(str -> !str.isEmpty()).collect(Collectors.toList());
        out.println(list);
    }

    /**
     * Map：
     * 接受一个函数作为参数。这个函数会被应用到每个元素上，
     * 并将其映射成一个新的元素（使用映射一词，是因为它和转换类似，
     * 但其中的细微差别在于它是“创建一个新版本”而不是去“修改”）
     */
    @Test
    public void mapStream() {
        List list = DISORDERLIST.stream().map(str -> str + "-itcast").collect(Collectors.toList());
        out.println(list);
    }

    /**
     * distinct：
     * 返回一个元素各异（根据流所生成元素的hashcode和equals方法实现）的流
     */
    @Test
    public void distinct() {
        List list = DISTINCTLIST.stream().distinct(). collect(Collectors.toList());
        out.println(list);

        // 去重无效
        List<Inner> inners_list = INNERLIST.stream().distinct().collect(Collectors.toList());
        logger.info("innert 去重数据：" + inners_list.toString());
        // 有效
        List<Inner> inners_list1 = INNERLIST.stream()
                .collect(Collectors
                            .collectingAndThen(
                                    Collectors.toCollection(()
                                            -> new TreeSet<>(
                                                    Comparator
                                                            .comparing(Inner::getName))), ArrayList::new));
        logger.info("innert 去重数据2：" + inners_list1.toString());
    }

    /**
     * Srot：返回排序后的流
     */
    @Test
    public void sortStream() {
        List list = DISORDERLIST.stream().sorted().collect(Collectors.toList());
        out.println(list);
    }

    /**
     * limit：返回一个给定长度的流
     */
    @Test
    public void limitStream() {
        List list = null;
        try {
            list = LIST.stream().limit(-1).collect(Collectors.toList());
            logger.info("给定长度的流：" + list);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            logger.info("Error message：" + e.getMessage());
        }
    }

    /**
     * skip：返回一个扔掉了前n个元素的流
     */
    @Test
    public void skipStream() {
        List list = LIST.stream().skip(1).collect(Collectors.toList());
        logger.info("返回一个扔掉了前n个元素的流：" + list);
    }

    @Test
    public void flatMap() {
//        Stream flatMap = LIST.stream().flatMap(Java8StreamTest::getCharacterByString);
    }

    /**
     * 对元素遍历处理
     */
    @Test
    public void peekStream() {
        LIST.stream()
                .peek(str ->
                        String.valueOf(str))
                .forEach(out::println);
    }

    // ---------- 终端操作符 ----------

    /**
     * collect：收集器，将流转换为其他形式
     */
    @Test
    public void collect() {
        List list = LIST.stream().collect(Collectors.toList());
        logger.info("List ={}", new Object[]{list});
        Set set = LIST.stream().collect(Collectors.toSet());
        logger.info("Set ={}", new Object[]{set});
        Map<String, String> innerMap = INNERLIST.stream()
                .collect(Collectors.toMap(Inner::getName, Inner::getSex));
        logger.info("Map ={}", new Object[]{innerMap});
    }

    /**
     * forEach：遍历流
     */
    @Test
    public void forEach() {
        LIST.stream().forEach(item -> System.out.println(item));
        INNERLIST.stream().forEach(item -> System.out.println(item.getName() + ":" + item.getSex() + ":" + item.getAge()));
    }

    /**
     * first：返回第一个元素
     */
    @Test
    public void first() {

        Optional optional = LIST.stream().findFirst();
        if (optional.isPresent()) System.out.println(optional.get());

        Optional<String> str = LIST.stream().findFirst();
        System.out.println(str.get());

        List<String> list = new ArrayList();
        String orElse = list.stream().findFirst().orElse(null);
        System.out.println(orElse);

    }

    /**
     * findAny：将返回当前流中的任意元素
     */
    @Test
    public void findAny() {
        String str = LIST.stream().findAny().get();
        System.out.println(str);
        ;
    }

    /**
     * count：返回流中元素总数
     */
    @Test
    public void count() {
        long count = LIST.stream().count();
        System.out.println(count);
    }

    /**
     * sum：求和
     */
    @Test
    public void sum() {
        int sum = Stream.of(1, 2, 3, 4, 5).mapToInt(e -> e).sum();
        System.out.println(sum);

        int sum2 = INNERLIST.stream().mapToInt(Innser -> Innser.getAge()).sum();
        System.out.println(sum2);


        IntSummaryStatistics summaryStatistics = INTLIST.stream().mapToInt((s) -> s).summaryStatistics();
        int sum3 = (int) summaryStatistics.getSum();
        int count = (int) summaryStatistics.getCount();
        int max = (int) summaryStatistics.getMax();
        int min = (int) summaryStatistics.getMin();
        int avg = (int) summaryStatistics.getAverage();
        System.out.println("sum3：" + sum3
                + "\ncount：" + count
                + "\nmax：" + max
                + "\nmin：" + min
                + "\navg：" + avg);
    }

    /**
     * Max：返回最大值
     * Min：返回最小值
     * Average：返回平均值
     */
    @Test
    public void max() {
        Optional<Inner> maxListOptional = INNERLIST.stream().max(Comparator.comparingInt(Inner::getAge));
        maxListOptional.ifPresent(e -> System.out.println("Max: " + e.getAge()));
        Optional<Inner> minListOptional = INNERLIST.stream().min(Comparator.comparingInt(Inner::getAge));
        minListOptional.ifPresent(e -> System.out.println("Min：" + e.getAge()));
        OptionalDouble average = INNERLIST.stream().mapToInt(e -> e.getAge()).average();
        average.ifPresent(e -> System.out.println("Avg：" + e));

        System.exit(0); //success
    }

    /**
     * anyMathc：检查是否至少匹配一个元素，返回boolean
     */
    @Test
    public void anyMatch() {
        // 数组
        boolean anyMatch = Stream.of(1, 2, 3, 4).anyMatch(e -> e.intValue() % 2 == 0);
        System.out.println("AnyMatche：" + anyMatch);

        // 集合
        boolean list = INTLIST.stream().anyMatch(e -> e.intValue() % 2 == 0);
        System.out.println("List：" + list);

        // 集合对象

        boolean object = INNERLIST.stream().anyMatch(e -> e.getAge() % 2 == 0);
        System.out.println("object：" + object);
    }


    /**
     * allMath：检查是否匹配所有元素，返回boolean
     */
    @Test
    public void allMatch() {
        // 数组
        boolean anyMatch = Stream.of(1, 2, 3, 4).anyMatch(e -> e.intValue() % 2 == 0);
        System.out.println("AnyMatche：" + anyMatch);

        // 集合
        boolean list = INTLIST.stream().allMatch(e -> e.intValue() % 2 == 0);
        System.out.println("List：" + list);

        // 集合对象

        boolean object = INNERLIST.stream().allMatch(e -> e.getAge() % 2 == 0);
        System.out.println("object：" + object);

        // Map
        boolean map = MAP.entrySet().stream().anyMatch(e -> e.getKey().equals("age"));
        System.out.println("Map：" + map);
        Map<String, String> x = new HashMap();

        Map<String, Integer> y = x.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> Integer.parseInt(e.getValue())
                ));
    }

    /**
     * noonMatch：检查是否没有匹配所有元素，返回boolean
     */
    @Test
    public void noonMatch() {
        boolean noonMatch = MAP.entrySet().stream().noneMatch(e -> e.getKey().equals("age"));
        logger.info("noonMatch：" + noonMatch);
    }

    private Inner inner1 = new Inner("Inner1", 26, "男");
    private Inner inner2 = new Inner("Inner2", 26, "女");

    @Test
    public void test(char x) {
        List<Integer> list =  Arrays.asList(2);

        //通过reduce方法得到一个Optional类
        int a =  list.stream().reduce(Integer::sum).orElse(get("a"));
        int b =  list.stream().reduce(Integer::sum).orElseGet(() -> get("b"));
        System.out.println("a  "+a);
        System.out.println("b  "+b);
    }
    public static int get(String name){
        System.out.println(name+"执行了方法");
        return 1;
    }

    /**
     * reduce：将流中元素反复结合，得到一个值
     */
    @Test
    public void reduce() {
        Optional reduce = LIST.stream().reduce((e, e1) -> {
            return e;
        });
        if (reduce.isPresent()) System.out.println(reduce.get());

        Optional reduce1 = INNERLIST.stream().reduce((inner, inner2) -> {
            return inner;
        });
        if (reduce1.isPresent()) System.out.println(reduce1.get());

        Optional reduce2 = INTLIST.stream().reduce((e, e1) -> {
            return e;
        });
        if (reduce2.isPresent()) System.out.println(reduce2.get());
    }

    // ------------- Collect收集 -------------------------------------

    /**
     * toList：
     */
    @Test
    public void toList() {
        List<String> names = INNERLIST.stream().map(Inner::getName).collect(Collectors.toList());
        System.out.println(names);
    }

    /**
     * toMap：
     * @return
     */
    @Test
    public void toMap(){
        Map map = MAP.entrySet().stream().collect(Collectors.toMap(e->e.getKey(), e->e.getValue()));
        Map map1 = INNERLIST.stream().collect(Collectors.toMap(Inner::getName, Inner::getAge));
        System.out.println(map);
        System.out.println(map1);
    }

    /**
     * toSet
     */
    @Test
    public void toSet(){
        Set<Integer> set = INTLIST.stream().filter(e -> e % 2 == 0)
                .map(e -> e * 2)
                .collect(Collectors.toSet());
        System.out.println(set);
    }

    /**
     * counting：符合条件的用户总数
     */
    @Test
    public void counting(){
        long i = INNERLIST.stream().filter(e -> e.getAge() % 2 == 0).collect(Collectors.counting());
        System.out.println(i);
    }

    /**
     * summingInt：对结果元素求和
     */
    @Test
    public void summingInt(){
        Integer sumInt = INNERLIST.stream().filter(e -> e.getAge() % 2 == 0).collect(Collectors.summingInt(Inner::getAge)) ;
        System.out.println(sumInt);
    }

    /**
     * minBy：筛选元素中ID最小的用户
     */
    @Test
    public void minBy(){
        Inner minBy = INNERLIST.stream().filter(e -> e.getAge() % 2 == 0)
                .collect(Collectors.minBy(Comparator.comparingInt(Inner::getAge))).get();
        System.out.println(minBy);
    }

    /**
     * joining：指定分隔符连接成字符串
     * @return
     */
    @Test
    public void joining(){
        String joining = LIST.stream().collect(Collectors.joining("-"));
        System.out.println(joining);
    }

    /**
     * groupingBy
     */
    @Test
    public void groupingBy(){
        Map<String, List<Inner>> groupingBy = INNERLIST.stream().collect(Collectors.groupingBy(Inner::getName));
        System.out.println(groupingBy);
    }



    public static Inner getInner(){
        // 内部类是延迟加载的，也就是说只有在使用的时候才会去加载内部类，避免了内存的浪费，线程安全等问题
        Inner inner = Inner.instance;
        return inner;
    }

    @ToString
    static class Inner {

        private static Inner instance = new Inner();

        private String name;
        private Integer age;
        private String sex;

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }

        public String getSex() {
            return sex;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public Inner() {
        }

        public Inner(String name, Integer age, String sex) {
            this.name = name;
            this.age = age;
            this.sex = sex;
        }
    }

    // ----------------- 测试 ------------------

    /**
     * 先将所先将所有字母转小写，在将首字母转大写
     */
    @Test
    public void streamFirstToLowerCase(){
        List<String> list = new ArrayList();
        list.add("abd");
        list.add("abcde");
        list.add("abc");
        // 转小写
        list.stream()
                .map(n -> n.toLowerCase()).forEachOrdered(n->System.out.println(n));

        System.out.println("------------------------------------------------");

        // 根据长度进行排序
        list.stream().map(n -> n.toLowerCase()).sorted(Comparator.comparingInt(String::length)).forEach(out::println);

        System.out.println("------------------------------------------------");

        // 映射字符串
        list.stream().map(n -> n.toLowerCase()).sorted(Comparator.comparingInt(String::length)).map(n -> Character.toUpperCase(n.charAt(0)) + n.substring(1)).forEachOrdered(n -> System.out.println(n));
        Character ch = 'a';
    }
}
