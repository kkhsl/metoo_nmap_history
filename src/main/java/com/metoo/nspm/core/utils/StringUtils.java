package com.metoo.nspm.core.utils;

import org.junit.Test;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StringUtils {

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String getStr(String s) {
        if (s == null) {
            return "";
        }
        if (s.isEmpty()) {
            return s;
        }
        return s.trim();
    }

    @Test
    public static void acquireCharacterPosition() {
        int i = acquireCharacterPosition("00", ":", 1);
        System.out.println(i);
    }

    /**
     * 获取指定字符第N次出现的位置
     * @return
     */
    public static int acquireCharacterPosition(String param, String symbol, int num){
        if(param.contains(symbol) && param.indexOf(":") != -1){
            Pattern pattern = Pattern.compile(symbol);
            Matcher findMatcher = pattern.matcher(param);
            List<Integer> list = new ArrayList();
            while(findMatcher.find()) {
                list.add(findMatcher.start());
            }
            return list.get(num - 1);
        }
        return -1;
    }

}
