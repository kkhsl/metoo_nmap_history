package com.metoo.nspm.core.manager.admin.tools;

import java.util.ArrayList;
import java.util.List;

public class CompareUtils {

    private static final String DEFALUT_SUBNET_MASK = "255.255.255.255";

    public static void main(String[] args) {
        System.out.println(isSameNetWork("192.168.1.1", "192.166.2.1"));
        System.out.println(isSameNetWork("192.168.1.1", "192.168.3.3"));
        System.out.println(isSameNetWork("117.20.116.233", "117.20.116.40"));
    }

    public static boolean isSameNetWork(String ip, String compareIp){
        List<String> ipList = calculate(ip, DEFALUT_SUBNET_MASK);
        List<String> sub = calculate(compareIp, DEFALUT_SUBNET_MASK);
        if(!ipList.get(0).equals(sub.get(0))){
            return false;
        }
        return ipList.get(1).equals(sub.get(1));
    }

    /**
     * 根据原始ip子网掩码通过亦或计算得到网段
     * @param ip
     * @param subNetMask 子网掩码
     * @return
     */
    private static List<String> calculate(String ip, String subNetMask){
        //ip
        String[] ipArray = ip.split("\\.");
        String[] ipBinary = new String[4];
        for (int i=0; i<ipArray.length; i++) {
            ipBinary[i] = leftPadding0(Integer.toBinaryString(Integer.parseInt(ipArray[i])), 8);
        }
        //掩码
        String[] subNetMaskArray = subNetMask.split("\\.");
        String[] subNetMaskBinary = new String[4];
        for (int i=0; i<subNetMaskArray.length; i++) {
            subNetMaskBinary[i] = leftPadding0(Integer.toBinaryString(Integer.parseInt(subNetMaskArray[i])), 8);
        }

        List<String> binaryIp = new ArrayList();
        for(int i=0; i<ipBinary.length; i++){
            String item = ipBinary[i];
            String[] itemArray = new String [item.length()];
            for(int j=0; j<item.length(); j++){
                itemArray[j] = item.substring(j, j+1);
            }
            String sub = subNetMaskBinary[i];
            String[] subArray = new String [sub.length()];
            for(int j=0; j<sub.length(); j++){
                subArray[j] = sub.substring(j, j+1);
            }
            StringBuilder ipBuffer = new StringBuilder();
            for(int k=0; k< itemArray.length; k++) {
                String and  = String.valueOf(Integer.parseInt(itemArray[k]) ^ Integer.parseInt(subArray[k]));
                ipBuffer.append(and);
            }
            binaryIp.add(ipBuffer.toString());
        }
        return binaryIp;
    }

    /**
     * 左补0
     * @param oriStr 原字符串
     * @param length 补0后的总长度
     * @return
     */
    private static String leftPadding0(String oriStr, int length) {
        if (oriStr.length() == length) {
            return oriStr;
        }
        StringBuilder zero = new StringBuilder();
        for (int i = 0; i < length - oriStr.length(); i++) {
            zero.append("0");
        }
        return zero.toString() + oriStr;
    }
}
