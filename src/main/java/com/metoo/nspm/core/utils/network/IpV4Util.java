package com.metoo.nspm.core.utils.network;

import com.metoo.nspm.entity.zabbix.Arp;
import com.github.pagehelper.util.StringUtil;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class IpV4Util {

    /**
     * 掩码 转 掩码位
     * @param mask
     * @return
     */
    public static int getMaskBitByMask(String mask){
        StringBuffer sbf;
        String str;
        int inetmask = 0, count = 0;
        if(StringUtil.isEmpty(mask)){
            return inetmask;
        }
        String[] ipList = mask.split("\\.");
        for (int n = 0; n < ipList.length; n++) {
            sbf = toBin(Integer.parseInt(ipList[n]));
            str = sbf.reverse().toString();

            // 统计2进制字符串中1的个数
            count = 0;
            for (int i = 0; i < str.length(); i++) {
                i = str.indexOf('1', i); // 查找 字符'1'出现的位置
                if (i == -1) {
                    break;
                }
                count++; // 统计字符出现次数
            }
            inetmask += count;
        }
        return inetmask;
    }



    public static StringBuffer toBin(int x) {
        StringBuffer result = new StringBuffer();
        result.append(x % 2);
        x /= 2;
        while (x > 0) {
            result.append(x % 2);
            x /= 2;
        }
        return result;
    }

    /**
     *
     * @param mask
     * @return
     */
    public static int getHostNum(Integer mask){
        if(mask != null){
            int number = (int) Math.pow(2, 32 - mask);
            return number;
        }
        return 0;
    }


    /**
     * 获取主机地址
     * @param ip
     * @param mask
     * @return
     */
    public List<String> getHost(String ip, String mask){
        if(ip != null && mask != null){
            int bitByMask = getMaskBitByMask(mask);
            String[] ipList = ip.split("\\.");
            String ip_host = "";
            StringBuffer ip_network = new StringBuffer();
            for (int n = 0; n < ipList.length; n++) {
                if(n == 3){
                    ip_host = ipList[n];
                }else{
                    ip_network.append(ipList[n] + ".");
                }
            }
            String ip_net = ip_network.toString();

            // 获取掩码长度
            // 第一个主机ip为网络地址
            // 最后一个主机ip为广播地址
            // 其他主机ip为主机地址
            int length = getHostNum(bitByMask);
            if(length > 0){
                double host = Math.ceil(Integer.parseInt(ip_host) / length) * length;
                int doubleValue = new Double(host).intValue();
                String networkAddress = ip_network + String.valueOf(doubleValue);
                doubleValue = new Double(host).intValue() + 1;
                List list = new ArrayList<>();
                int n = 0;
                for (int i = doubleValue ; i < length + doubleValue - 2; i ++){
                    list.add(ip_net + (doubleValue + n));
                    n ++;
                }
                return list;
            }
        }
        return null;
    }

    /**
     * 获取网络地址
     * @param ip
     * @param mask
     * @return
     */
    public String getNetworkAddress(String ip, Integer mask){
        if(ip != null && mask != null){
            String[] ipList = ip.split("\\.");
            String ip_host = "";
            StringBuffer ip_network = new StringBuffer();
            for (int n = 0; n < ipList.length; n++) {
                if(n == 3){
                    ip_host = ipList[n];
                }else{
                    ip_network.append(ipList[n] + ".");
                }
            }
            // 获取掩码长度
            // 第一个主机ip为网络地址
            // 最后一个主机ip为广播地址
            // 其他主机ip为主机地址
            int length = getHostNum(mask);
            if(length > 0){
                double host = Math.ceil(Integer.parseInt(ip_host) / length) * length;
                int doubleValue = new Double(host).intValue();
                String networkAddress = ip_network + String.valueOf(doubleValue);
                return networkAddress;
            }
        }
        return null;
    }

    // 获取广播地址
    public static Long toNumeric(String ip) {
        Scanner sc = new Scanner(ip).useDelimiter("\\.");
        Long l = (sc.nextLong() << 24) + (sc.nextLong() << 16) + (sc.nextLong() << 8)
                + (sc.nextLong());
        sc.close();
        return l;
    }

//    public static SortedSet sort(List<JSONObject> list){
//        Comparator<JSONObject> ipComparator = new Comparator<JSONObject>() {
//            @Override
//            public int compare(JSONObject obj1, JSONObject obj2) {
//                String ip1 = obj1.getString("subnet");
//                String ip2 = obj2.getString("subnet");
//                return toNumeric(ip1).compareTo(toNumeric(ip2));
//            }
//        };
//        SortedSet<JSONObject> ips = new TreeSet<JSONObject>(ipComparator);
//        for (JSONObject object : list){
//            ips.add(object);
//        }
//
//        return ips;
//    }

    public static SortedSet sortIp(List<Arp> arps){
        Comparator<Arp> ipComparator = new Comparator<Arp>() {
            @Override
            public int compare(Arp obj1, Arp obj2) {
                Long ip1 = Long.parseLong(obj1.getIp());
                Long ip2 = Long.parseLong(obj2.getIp());
                return (ip1).compareTo((ip2));
            }
        };
        SortedSet<Arp> arpsets = new TreeSet<Arp>(ipComparator);
        for (Arp arp : arps){
            arpsets.add(arp);
        }

        return arpsets;
    }


    public static SortedSet sort(List<Arp> arps){
        Comparator<Arp> ipComparator = new Comparator<Arp>() {
            @Override
            public int compare(Arp obj1, Arp obj2) {
                String ip1 = obj1.getIp();
                String ip2 = obj2.getIp();
                return toNumeric(ip1).compareTo(toNumeric(ip2));
            }
        };
        SortedSet<Arp> arpsets = new TreeSet<Arp>(ipComparator);
        for (Arp arp : arps){
            arpsets.add(arp);
        }

        return arpsets;
    }

}
