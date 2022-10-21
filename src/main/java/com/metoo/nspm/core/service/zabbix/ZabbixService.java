package com.metoo.nspm.core.service.zabbix;

import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.Map;

public interface ZabbixService {

    public Object getUsage(String ip, List itemName);

    /**
     * @param ip
     * @param itemName
     * @param limit
     * @param time_till
     * @param time_from
     * @return
     */
    Object getDevice(String ip, List itemName, Integer limit, Long time_till, Long time_from);

    Object getDeviceHistory(String ip, List itemName, Integer limit, Long time_till, Long time_from);

    Object refresh(String itemids, Integer limit);

    Object getInterfaceInfo(String ip);

    Object flow(String ip, String name);

    Object getInterfaceHistory(String ip, Integer limit, Long time_till, Long time_from);

    // 获取服务器信息
    Object getServer(String ip);

    // ARP
    List getItemArp(String ip,  String deviceName, String uuid, String deviceType);
    // Mac
    Object getItemMac(String ip, String deviceName, String uuid, String deviceType);
    // Rout
    List<Map<String, String>> getItemRoutByIp(String ip);

    void createRoutTable(String ip);

    List<String> getHostIps(String ip);

    // 采集arp表
    void gatherArp();

    // 采集Mac表
    void gatherMac();

    // 采集rout表
    void gatherRout();

    // 采集ip表
    void gatherIp();

    // 采集arp（多线程）
    void gatherArpThread();

    // 采集Mac（多线程）
    void gatherMacThread();

    // 采集路由表
    Object gatherRout(String ip, String deviceName, String uuid);

    // 采集路由表
    Object gatherIpaddress(String ip, String deviceName, String uuid);

}
