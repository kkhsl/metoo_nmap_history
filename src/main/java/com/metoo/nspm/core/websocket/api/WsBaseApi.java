package com.metoo.nspm.core.websocket.api;

import com.metoo.nspm.entity.nspm.Terminal;
import org.apache.shiro.crypto.hash.Md5Hash;

import java.util.ArrayList;
import java.util.List;

public class WsBaseApi {
    /**
     *  1：
     *  2：
     *  3：
     *  4：
     *  5：
     *  6：
     *  7：
     *  8：Problem分页数据
     *  9：网元|端口列表
     */

    public static void main(String[] args) {
        List<Terminal> t1 = new ArrayList<>();
        Terminal terminal = new Terminal();
        terminal.setTerminalTypeName("a");
        t1.add(terminal);
        Object obj = t1;
        List<Terminal> t2 = new ArrayList<>();
        Terminal terminal_2 = new Terminal();
        terminal_2.setTerminalTypeName("a");
        t2.add(terminal_2);
        boolean flag = getDiffrent3(obj, t2);
        System.out.println(flag);
    }

    /**
     * md5加密法使用
     * 方法4
     */

    private static boolean getDiffrent3(Object obj1, List<Terminal> list1) {

        long st = System.currentTimeMillis();
        /** 使用Security的md5方法进行加密 */
        String str = md5(obj1.toString(), "aa");
        String str1 = md5(list1.toString(), "aa");
        System.out.println("消耗时间为： " + (System.currentTimeMillis() - st));
        return str.equals(str1);
    }

    public static String md5(String str, String salt){
        Md5Hash md5Hash = new Md5Hash(str, salt);
        return md5Hash.toHex();
    }
}
