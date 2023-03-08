package com.metoo.nspm.container.data.structure.test;

import com.metoo.nspm.container.data.structure.IfElseEnumDemo;

public class MainTest {

    public static void main(String[] args) {

        String ip = getParentIp("192.168.5.1", 24);
        System.out.println(ip);

        // 使用策略枚举优化多重ifelse
        GetFlowers gf = new GetFlowers();
        String cidr = gf.getFlowers(IfElseEnumDemo.valueOf("ROSE"), "192.168.5.1", 24);
        System.out.println(cidr);
    }

    /**
     *
     * @param ip
     * @param mask
     * @return
     */
    public static String getParentIp(String ip, Integer mask){
        int index = 0;
        String segment = "";
        if (24 == mask) {
            index =  ip.indexOf(".");
            index =  ip.indexOf(".", index + 1);
            index =  ip.indexOf(".", index + 1);
            segment = ".0";
        } else if (16  == mask) {
            index =  ip.indexOf(".");
            index =  ip.indexOf(".", index + 1);
            segment = ".0.0";
        }else if (8  == mask) {
            index =  ip.indexOf(".");
            segment = ".0.0.0";
        }
        String parentIp = ip.substring(0, index);
        return parentIp + segment;
    }
}
