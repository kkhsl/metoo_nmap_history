package com.metoo.nspm.core.utils.collections;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ListSortUtil {

    public static void compareTo(List<Map<Object, Object>> list){
        Collections.sort(list, new Comparator<Map<Object, Object>>() {
            @Override
            public int compare(Map<Object, Object> o1, Map<Object, Object> o2) {
                String key1 = o1.get("policyCheckTotal").toString();
                String key2 = o2.get("policyCheckTotal").toString();
                return key2.compareTo(key1);
            }
        });

    }

    public static void sort(List<Map<String, Double>> list){
        Collections.sort(list, new Comparator<Map<String, Double>>() {
            @Override
            public int compare(Map<String, Double> o1, Map<String, Double> o2) {
                Double key1 = o1.get("grade");
                Double key2 = o2.get("grade");
                return key1.compareTo(key2);
            }
        });
    }

//    public static void sortByIp(List<Arp> list){
//        Collections.sort(list, new Comparator<Arp>() {
//            @Override
//            public int compare(Arp arp1, Arp arp2) {
//                String ip1 = arp1.getIp();
//                String ip2 = arp2.getIp();
//                return key1.compareTo(key2);
//            }
//        });
//    }

    public static void lambdaSort(List<Map<String, Double>> list){
        Collections.sort(list, (s1, s2) -> s1.get("grade").compareTo(s2.get("grade")));
    }


}
