package com.metoo.nspm.core.service.zabbix.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.metoo.nspm.core.manager.admin.tools.DateTools;
import com.metoo.nspm.core.manager.zabbix.utils.ItemUtil;
import com.metoo.nspm.core.service.IAddressService;
import com.metoo.nspm.core.service.IThresholdService;
import com.metoo.nspm.core.service.topo.ITopoNodeService;
import com.metoo.nspm.core.service.zabbix.*;
import com.metoo.nspm.core.utils.ResponseUtil;
import com.metoo.nspm.core.utils.network.IpUtil;
import com.metoo.nspm.core.utils.network.IpV4Util;
import com.metoo.nspm.dto.zabbix.HistoryDTO;
import com.metoo.nspm.dto.zabbix.HostDTO;
import com.metoo.nspm.dto.zabbix.ItemDTO;
import com.metoo.nspm.entity.Address;
import com.metoo.nspm.entity.Threshold;
import com.metoo.nspm.entity.zabbix.*;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Hash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ZabbixServiceImpl implements ZabbixService {

    @Autowired
    private IThresholdService thresholdService;
    @Autowired
    private ZabbixHostService zabbixHostService;
    @Autowired
    private ZabbixItemService zabbixItemService;
    @Autowired
    private ZabbixHistoryService historyService;
    @Autowired
    private IpV4Util ipV4Util;
    @Autowired
    private DateTools dateTools;
    @Autowired
    private IIPAddressServie ipAddressService;
    @Autowired
    private IMacService macService;
    @Autowired
    private IArpService arpService;
    @Autowired
    private ILocalIpAddressService localIpAddressService;
    @Autowired
    private ItemUtil itemUtil;
    @Autowired
    private IpDetailService ipDetailService;
    @Autowired
    private ITopoNodeService topoNodeService;
    @Autowired
    private ZabbixHostInterfaceService zabbixHostInterfaceService;
    @Autowired
    private IRoutService routService;
    @Autowired
    private IIPAddressServie ipaddressService;
    @Autowired
    private ZabbixSubnetService zabbixSubnetService;
    @Autowired
    private IAddressService addressService;

    public static void main(String[] args) {
        String a = "0:0:5e:0:1:c8";
        String b = "0:0:5e:0";
        System.out.println(a.contains(b));
    }

    public Object getItem(String ip){
        if(StringUtils.isNotEmpty(ip)){
            HostDTO dto = new HostDTO();
            Map map = new HashMap();
            map.put("ip", Arrays.asList(ip));
            dto.setFilter(map);
            dto.setMonitored(true);
            Object object = this.zabbixHostService.getHost(dto);
            JSONObject jsonObject = JSONObject.parseObject(object.toString());
            if(jsonObject.get("result") != null){
                JSONArray arrays = JSONArray.parseArray(jsonObject.getString("result"));
                if(arrays.size() > 0){
                    JSONObject host = JSONObject.parseObject(arrays.get(0).toString());
                    String hostid = host.getString("hostid");
                    if(hostid != null){
                        ItemDTO itemDto = new ItemDTO();
                        itemDto.setHostids(Arrays.asList(hostid));
                        Map filterMap = new HashMap();
                        itemDto.setFilter(filterMap);
                        itemDto.setMonitored(true);
                        Object itemObejct = this.zabbixItemService.getItem(itemDto);
                        return itemObejct;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据Ip获取Item
     * @param ip
     * @return
     */
    public JSONArray getItemsByIp(String ip){
        if(StringUtils.isNotEmpty(ip)){
            HostDTO dto = new HostDTO();
            Map map = new HashMap();
            map.put("ip", Arrays.asList(ip));
            dto.setFilter(map);
            dto.setMonitored(true);
            Object object = this.zabbixHostService.getHost(dto);
            JSONObject jsonObject = JSONObject.parseObject(object.toString());
            if(jsonObject.get("result") != null){
                JSONArray arrays = JSONArray.parseArray(jsonObject.getString("result"));
                if(arrays.size() > 0){
                    JSONObject host = JSONObject.parseObject(arrays.get(0).toString());
                    String hostid = host.getString("hostid");
                    if(hostid != null){
                        ItemDTO itemDto = new ItemDTO();
                        itemDto.setHostids(Arrays.asList(hostid));
                        Map filterMap = new HashMap();
                        itemDto.setFilter(filterMap);
                        itemDto.setMonitored(true);
                        Object itemObejct = this.zabbixItemService.getItem(itemDto);
                        JSONObject itemJSON = JSONObject.parseObject(itemObejct.toString());
                        if(itemJSON.get("result") != null) {
                            JSONArray itemArray = JSONArray.parseArray(itemJSON.getString("result"));
                            return itemArray;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Object getItemByName(String ip, List names){
        if(StringUtils.isNotEmpty(ip)){
            HostDTO dto = new HostDTO();
            Map map = new HashMap();
            map.put("ip", Arrays.asList(ip));
            dto.setFilter(map);
            Object object = this.zabbixHostService.getHost(dto);
            JSONObject jsonObject = JSONObject.parseObject(object.toString());
            if(jsonObject.get("result") != null){
                JSONArray arrays = JSONArray.parseArray(jsonObject.getString("result"));
                if(arrays.size() > 0){
                    JSONObject host = JSONObject.parseObject(arrays.get(0).toString());
                    String hostid = host.getString("hostid");
                    if(hostid != null){
                        ItemDTO itemDto = new ItemDTO();
                        itemDto.setHostids(Arrays.asList(hostid));
                        Map filterMap = new HashMap();
                        filterMap.put("name", names);
                        itemDto.setFilter(filterMap);
                        Object itemObejct = this.zabbixItemService.getItem(itemDto);
                        return itemObejct;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据Ip获取主机本地ip地址（不包含127.0.0.1）
     * @param ip
     * @return
     */
    public List<Map<String, String>> parseItemIpAddress(String ip){
        // ipaddresses
        JSONArray items = this.zabbixItemService.getItemIpAddress(ip);
        if(items != null && items.size() > 0){
            List<Map<String, String>> ips = new ArrayList<>();
            for (Object obj : items){
                JSONObject item = JSONObject.parseObject(obj.toString());
                JSONArray tags = JSONArray.parseArray(item.getString("tags"));
                if(tags != null && tags.size() > 0){
                    Map tagMap = new HashMap();
                    for (Object t : tags){
                        JSONObject tag = JSONObject.parseObject(t.toString());
                        if(tag.getString("tag").equals("ipaddr")){
//                            String ipaddr = tag.getString("value");// 查询过滤
//                            String first = tag.getString("value").substring(0, ipaddr.indexOf("."));
                            tagMap.put("ipaddr", tag.getString("value"));
                        }
                        if(tag.getString("tag").equals("mask")){
                            tagMap.put("mask", tag.getString("value"));
                        }
                    }
                    ips.add(tagMap);
                }
            }
            return ips;
        }
        return null;
    }

    @Override
    public Object getUsage(String ip, List itemName) {
        if(StringUtils.isNotEmpty(ip)){
            HostDTO dto = new HostDTO();
            Map map = new HashMap();
            map.put("ip", Arrays.asList(ip));
            dto.setFilter(map);
            Object object = this.zabbixHostService.getHost(dto);
            JSONObject jsonObject = JSONObject.parseObject(object.toString());
            if(jsonObject.get("result") != null){
                JSONArray arrays = JSONArray.parseArray(jsonObject.getString("result"));
                if(arrays.size() > 0){
                    JSONObject host = JSONObject.parseObject(arrays.get(0).toString());
                    String hostid = host.getString("hostid");
                    if(hostid != null){
                        ItemDTO itemDto = new ItemDTO();
                        itemDto.setHostids(Arrays.asList(hostid));
                        Map filterMap = new HashMap();
                        filterMap.put("name", itemName);
                        itemDto.setFilter(filterMap);
                        Object itemObejct = this.zabbixItemService.getItem(itemDto);
                        Object result = this.parseUsage(itemObejct);
                        return result;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Object getDevice(String ip, List itemName, Integer limit, Long time_till, Long time_from) {
        Object itemObejct = this.getItemByName(ip, itemName);
        if(itemObejct != null) {
            Object result = this.parseHistory(itemObejct, limit, time_till, time_from);
            return result;
        }
        return null;
    }

    @Override
    public Object getDeviceHistory(String ip, List itemName, Integer limit, Long time_till, Long time_from) {
        Object itemObejct = this.getItemByName(ip, itemName);
        if(itemObejct != null){
            Object result = this.parseHistoryServer(itemObejct, itemName, time_till, time_from);
            return result;
        }
        return null;
    }

    @Override
    public Object refresh(String itemids, Integer limit) {
        Object object = this.getHistory(Arrays.asList(itemids), limit);
        return object;
    }

    @Override
    public Object getInterfaceInfo(String ip) {
        JSONArray itemList = this.zabbixItemService.getItemInterfaces(ip);
        if (itemList.size() > 0) {
            List list = new ArrayList();
            ExecutorService exe = Executors.newFixedThreadPool(itemList.size());
            for (Object obj : itemList) {
                exe.execute(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 端口名、端口状态、端口描述、ip地址
                        JSONObject item = JSONObject.parseObject(obj.toString());
                        JSONArray tags = JSONArray.parseArray(item.getString("tags"));
                        if (tags != null && tags.size() > 0) {
                            Map<String, Object> tagMap = new HashMap<String, Object>();
                            tagMap.put("name", "");
                            tagMap.put("description", "");
                            tagMap.put("status", "");
                            tagMap.put("ip", "");
                            tagMap.put("mask", "");
                            for (Object t : tags) {
                                JSONObject tag = JSONObject.parseObject(t.toString());
                                if (tag.getString("tag").equals("ifindex")) {
                                    tagMap.put("index", tag.getString("value"));
                                }
                                if (tag.getString("tag").equals("ifname")) {
                                    tagMap.put("name", tag.getString("value"));
                                }
                                if (tag.getString("tag").equals("description")) {
                                    tagMap.put("description", tag.getString("value"));
                                }
                            }
                            // 获取端口状态
                            // 获取端口ip
                            if(tagMap.get("index") != null){
                                JSONArray items =  zabbixItemService.getItemOperationalTagByIndex(ip, Integer.parseInt(tagMap.get("index").toString()));
                                if(items.size() > 0 ){
                                    for(Object object : items){
                                        JSONObject operational = JSONObject.parseObject(object.toString());
                                        JSONArray ipaddressTag = JSONArray.parseArray(operational.getString("tags"));
                                        if (ipaddressTag != null && ipaddressTag.size() > 0) {
                                            for (Object t : ipaddressTag) {
                                                JSONObject tag = JSONObject.parseObject(t.toString());
                                                if (tag.getString("tag").equals("ifoperstatus")) {
                                                    String status = tag.getString("value");
                                                    switch (status){
                                                        case "1":
                                                            status = "up";
                                                            break;
                                                        case "2":
                                                            status = "down";
                                                            break;
                                                        default:
                                                            status = "unknown";
                                                    }
                                                    tagMap.put("status", status);
                                                }
                                            }
                                        }
                                    }
                                }

                                JSONArray ipaddressList =  zabbixItemService.getItemIpAddressTagByIndex(ip, Integer.parseInt(tagMap.get("index").toString()));
                                if(ipaddressList.size() > 0 ){
                                    for(Object ipaddressObj : ipaddressList){
                                        JSONObject ipaddress = JSONObject.parseObject(ipaddressObj.toString());
                                        JSONArray ipaddressTag = JSONArray.parseArray(ipaddress.getString("tags"));
                                        if (ipaddressTag != null && ipaddressTag.size() > 0) {
                                            for (Object t : ipaddressTag) {
                                                JSONObject tag = JSONObject.parseObject(t.toString());
                                                if (tag.getString("tag").equals("ipaddr")) {
                                                    tagMap.put("ip", tag.getString("value"));
                                                }
                                                if (tag.getString("tag").equals("mask")) {
                                                    tagMap.put("mask", tag.getString("value"));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            list.add(tagMap);
                        }
                    }
                }));
            }
            if(exe != null){
                exe.shutdown();
            }
            while (true) {
                if (exe.isTerminated()) {
                    return list;
                }
            }

        }
        return null;
    }


    /**
     * 采集路由表
     * @param ip
     * @return
     */
    @Override
    public  List<Map<String, String>> getItemRoutByIp(String ip) {
        // 获取主机本地网络地址
//        List<Map<String, String>> items = this.parseItemIpAddress(ip);
        JSONArray items = this.zabbixItemService.getItemIpAddress(ip);
        if(items != null && items.size() > 0) {
            List<Map<String, String>> routs = new ArrayList<>();
            for (Object obj : items) {
                JSONObject item = JSONObject.parseObject(obj.toString());
                String lastcolock = item.getString("lastclock");
                if(lastcolock != null && !lastcolock.equals("")){
                    Long currentSecond = DateTools.currentTimeSecond();
                    Long interval = DateTools.secondInterval(currentSecond, Long.parseLong(lastcolock));
                    if(interval >= 60){
                        continue;
                    }
                }
                JSONArray tags = JSONArray.parseArray(item.getString("tags"));
                if (tags != null && tags.size() > 0) {
                    Map tagMap = new HashMap();
                    for (Object t : tags) {
                        JSONObject tag = JSONObject.parseObject(t.toString());
                        if (tag.getString("tag").equals("ipaddr")) {
                            tagMap.put("ipaddr", tag.getString("value"));
                        }
                        if (tag.getString("tag").equals("mask")) {
                            tagMap.put("mask", tag.getString("value"));
                        }
                    }
                    routs.add(tagMap);
                }
            }

            // 获取所有网络地址(启用：判断ip是否与当前ip同网段)
//            List<String> networkIps = new ArrayList<>();
//            if(items != null && items.size() > 0){
//                for (Map<String, String> rout : routs){
//                    int bitByMask = this.ipV4Util.getMaskBitByMask(rout.get("mask"));
//                    if(rout.get("ipaddr") != null && bitByMask >= 16){
//                        List<String> ips = this.ipV4Util.getHost(rout.get("ipaddr"), rout.get("mask"));
//                        networkIps.addAll(ips);
//                    }
//                }
//            }
            List<Map<String, String>> maps = this.itemUtil.getRoutItems(ip);
            return maps;
        }
        return null;
    }

    /**
     * ipaddress表
     * @param ip
     * @return
     */
    @Override
    public  List<Map<String, String>> gatherIpaddress(String ip, String deviceName, String uuid) {
        JSONArray items = this.zabbixItemService.getItemIpAddressTag(ip);
        if(items != null && items.size() > 0){
            for(Object item : items){
                JSONObject arp = JSONObject.parseObject(item.toString());
                JSONArray tags = JSONArray.parseArray(arp.getString("tags"));
                if (tags != null && tags.size() > 0) {
                    // 记录全网ip信息
                    IpAddress ipAddress = new IpAddress();
                    ipAddress.setDeviceName(deviceName);
                    ipAddress.setDeviceUuid(uuid);
                    for(Object t : tags){
                        JSONObject tag = JSONObject.parseObject(t.toString());
                        if (tag.getString("tag").equals("ipaddr")) {
                            ipAddress.setIp(IpUtil.ipConvertDec(tag.getString("value")));
                            ipAddress.setIpAddress(tag.getString("value"));
                        }
                        if (tag.getString("tag").equals("mask")) {
                            ipAddress.setMask(IpUtil.getBitMask(tag.getString("value")));
                        }
                        if (tag.getString("tag").equals("ifindex")) {
                            if(com.metoo.nspm.core.utils.StringUtils.isInteger(tag.getString("value"))){
//                                ipAddress.setIndex(Integer.parseInt(tag.getString("value")));
//                                Map<String, String> detail = this.itemUtil.getInterfaceDetail(ip, tag.getString("value"));
//                                if(detail != null && detail.size() > 0){
//                                    ipAddress.setInterfaceName(detail.get("interfaceName"));
//                                    ipAddress.setMac(detail.get("mac"));
//                                }
                                ipAddress.setInterfaceName(tag.getString("value"));
                            }
                        }
                    }
                    Map params = new HashMap();
                    params.put("deviceName", ipAddress.getDeviceName());
                    params.put("interfaceName", ipAddress.getInterfaceName());
                    params.put("ip", ipAddress.getIp());
                    List<IpAddress> ips = this.ipAddressService.selectObjByMap(params);
                    if(ips.size() == 0){
                        this.ipAddressService.save(ipAddress);
                    }else{
                        // 比较uuid是否相同，更新uuid
                        IpAddress ipAddress1 = ips.get(0);
                        if(!ipAddress1.getDeviceUuid().equals(uuid)){
                            this.ipAddressService.update(ipAddress);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 采集路由表
     * @param ip
     * @return
     */
    @Override
    public  List<Map<String, String>> gatherRout(String ip, String deviceName, String uuid) {
        List<Map<String, String>> maps = this.itemUtil.getRoutItems(ip);
        if(maps != null && maps.size() > 0){
            for (Map<String, String> map : maps){
                Rout rout = new Rout();
                rout.setDestination(map.get("destination"));
                rout.setMask(IpUtil.getBitMask(map.get("mask")));
                rout.setCost(map.get("routemetric"));
                rout.setFlags(map.get("flags"));
                rout.setInterfaceName(map.get("interfaceName"));
                rout.setProto(map.get("proto"));
                rout.setNextHop(map.get("nextHop"));
                rout.setDeviceName(deviceName);
                rout.setDeviceUuid(uuid);
                this.routService.save(rout);
            }
        }
        return maps;
//        }
//        return null;
    }
//    @Override
//    public  List<Map<String, String>> gatherRout(String ip, String deviceName) {
////        JSONArray items = this.zabbixItemService.getItemIpAddress(ip);
////        if(items != null && items.size() > 0) {
////            List<Map<String, String>> routs = new ArrayList<>();
////            for (Object obj : items) {
////                JSONObject item = JSONObject.parseObject(obj.toString());
////                String lastcolock = item.getString("lastclock");
////                if(lastcolock != null && !lastcolock.equals("")){
////                    Long currentSecond = DateTools.currentTimeSecond();
////                    Long interval = DateTools.secondInterval(currentSecond, Long.parseLong(lastcolock));
////                    if(interval >= 60){
////                        continue;
////                    }
////                }
////                JSONArray tags = JSONArray.parseArray(item.getString("tags"));
////                if (tags != null && tags.size() > 0) {
////                    Map tagMap = new HashMap();
////                    for (Object t : tags) {
////                        JSONObject tag = JSONObject.parseObject(t.toString());
////                        if (tag.getString("tag").equals("ipaddr")) {
////                            tagMap.put("ipaddr", tag.getString("value"));
////                        }
////                        if (tag.getString("tag").equals("mask")) {
////                            tagMap.put("mask", tag.getString("value"));
////                        }
////                    }
////                    routs.add(tagMap);
////                }
////            }
//            // 获取所有网络地址(启用：判断ip是否与当前ip同网段)
////            List<String> networkIps = new ArrayList<>();
////            if(items != null && items.size() > 0){
////                for (Map<String, String> rout : routs){
////                    int bitByMask = this.ipV4Util.getMaskBitByMask(rout.get("mask"));
////                    if(rout.get("ipaddr") != null && bitByMask >= 16){
////                        List<String> ips = this.ipV4Util.getHost(rout.get("ipaddr"), rout.get("mask"));
////                        networkIps.addAll(ips);
////                    }
////                }
////            }
//
////            List<String> ips = new ArrayList<>();
////            if(items != null && items.size() > 0){
////                for (Map<String, String> rout : routs){
////                    if(rout.get("ipaddr") != null){
////                        ips.add(rout.get("ipaddr") + "/" + rout.get("mask"));
////                    }
////                }
////            }
//            List<Map<String, String>> maps = this.itemUtil.getRoutItems(ip);
//            if(maps != null && maps.size() > 0){
//                for (Map<String, String> map : maps){
//                    Rout rout = new Rout();
//                    rout.setDestination(map.get("destination"));
//                    rout.setMask(IpUtil.getBitMask(map.get("mask")));
//                    rout.setCost(map.get("routemetric"));
//                    rout.setFlags(map.get("flags"));
//                    rout.setInterfaceName(map.get("interfaceName"));
//                    rout.setProto(map.get("proto"));
//                    rout.setNextHop(map.get("nextHop"));
//                    rout.setDeviceName(deviceName);
//                    this.routService.save(rout);
//                }
//            }
//            return maps;
////        }
////        return null;
//    }

    @Override
    public List getItemArp(String ip, String deviceName, String uuid, String deviceType) {
        List ips = new ArrayList();
        JSONArray arps = this.zabbixItemService.getItemArpTag(ip);
        ExecutorService exe = null;
        if(arps != null && arps.size() > 0){
            exe = Executors.newFixedThreadPool(arps.size());
            for(Object obj : arps){
                JSONObject item = JSONObject.parseObject(obj.toString());
                String lastcolock = item.getString("lastclock");
                if(lastcolock != null && !lastcolock.equals("")){
                    Long currentSecond = DateTools.currentTimeSecond();
                    Long interval = DateTools.secondInterval(currentSecond, Long.parseLong(lastcolock));
                    if(interval >= 300){
//                        continue;
                    }
                }
                if(!item.get("error").equals("")){
                    continue;
                }
                exe.execute(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray tags = JSONArray.parseArray(item.getString("tags"));
                        if (tags != null && tags.size() > 0) {
                            // 记录全网ip信息
                            IpDetail ipDetail = new IpDetail();
                            Arp arp = new Arp();
                            arp.setDeviceName(deviceName);
                            arp.setDeviceType(deviceType);
                            arp.setDeviceIp(ip);
//                            arp.setInterfaceName(item.getString("lastvalue"));
                            arp.setUuid(uuid);
                            for (Object t : tags) {
                                JSONObject tag = JSONObject.parseObject(t.toString());
                                if (tag.getString("tag").equals("ip")) {
                                    arp.setIp(IpUtil.ipConvertDec(tag.getString("value")));
                                    arp.setIpAddress(tag.getString("value"));
                                    ipDetail.setIp(IpUtil.ipConvertDec(tag.getString("value")));
                                    ips.add(IpUtil.ipConvertDec(tag.getString("value")));
                                }
                                if (tag.getString("tag").equals("mac")) {
                                    arp.setMac(tag.getString("value"));
                                    ipDetail.setMac(tag.getString("value"));
                                }
                                if (tag.getString("tag").equals("mask")) {
                                    arp.setMask(tag.getString("value"));
                                }
                                if (tag.getString("tag").equals("ifindex")) {
                                    if(com.metoo.nspm.core.utils.StringUtils.isInteger(tag.getString("value"))){
                                        arp.setIndex(Integer.parseInt(tag.getString("value")));
                                        arp.setInterfaceName(tag.getString("value"));
                                    }

                                }
                            }
                            arp.setTag("S");
//                          IP表：记录Ip使用率 (优化到IP表)
                            ipDetail.setDeviceName(deviceName);
                            IpDetail existingIp = ipDetailService.selectObjByIp(ipDetail.getIp());
                            if(existingIp == null && ipDetail != null){
                                ipDetailService.save(ipDetail);
                            }
                            // 查询arp
                            Map<String, Object> params = new HashMap();
                            params.clear();
                            params.put("ip", arp.getIp());
                            params.put("deviceName", arp.getDeviceName());
                            params.put("interfaceName", arp.getInterfaceName());
                            List<Arp> localArps = arpService.selectObjByMap(params);
                            if(localArps.size() == 0){
                                arpService.save(arp);
                            }
                        }
                    }
                }));
            }
        }
        if(exe != null){
            exe.shutdown();
        }
        while (true) {
            if (exe == null || exe.isTerminated()) {
                // 补全（ipaddress）
                JSONArray ipaddressitems = this.zabbixItemService.getItemIpAddress(ip);
                if(ipaddressitems != null && ipaddressitems.size() > 0){
                    Map params = new HashMap();
                    for (Object obj : ipaddressitems){
                        JSONObject item = JSONObject.parseObject(obj.toString());
                        JSONArray tags = JSONArray.parseArray(item.getString("tags"));
                        if(item.getString("lastvalue") != null
                                && !item.getString("lastvalue").equals("")
                                && item.getString("lastvalue").equals("1")){
                            if (tags != null && tags.size() > 0) {
                                Arp arp = new Arp();
                                arp.setDeviceName(deviceName);
                                arp.setDeviceType(deviceType);
                                arp.setDeviceIp(ip);
                                //                                    arp.setInterfaceName(item.getString("lastvalue"));
                                arp.setUuid(uuid);
                                for(Object t : tags){
                                    JSONObject tag = JSONObject.parseObject(t.toString());
                                    if (tag.getString("tag").equals("ipaddr")) {
                                        arp.setIp(IpUtil.ipConvertDec(tag.getString("value")));
                                        arp.setIpAddress(tag.getString("value"));
                                    }
                                    if (tag.getString("tag").equals("mask")) {
                                        arp.setMask(tag.getString("value"));
                                    }
                                    if (tag.getString("tag").equals("ifindex")) {
                                        if(com.metoo.nspm.core.utils.StringUtils.isInteger(tag.getString("value"))){
                                            arp.setIndex(Integer.parseInt(String.valueOf(tag.get("value"))));
                                            arp.setInterfaceName(String.valueOf(tag.get("value")));
                                        }
                                    }
                                }
                                // 查询arp
                                params.clear();
                                params.put("ip", arp.getIp());
                                params.put("deviceName", arp.getDeviceName());
                                params.put("interfaceName", arp.getInterfaceName());
                                List<Arp> localArps = arpService.selectObjByMap(params);
                                if(localArps.size() == 0
                                        && arp.getInterfaceName() != null
                                        && !arp.getInterfaceName().equals("")){
                                    arp.setTag("L");
                                    arpService.save(arp);
                                }else  if(localArps.size() >  0){
                                    Arp local = localArps.get(0);
                                    local.setTag("L");
                                    local.setMask(arp.getMask());
                                    arpService.update(local);
                                }
                            }
                        }

                    }
                }
                break;
            }
        }
        return ips;
    }

//    @Override
//    public void getItemArpThread(String ip, String deviceName, String uuid) {
//        JSONArray arps = this.zabbixItemService.getItemArpTag(ip);
//        ExecutorService exe = null;
//        if(arps != null && arps.size() > 0){
//            exe = Executors.newFixedThreadPool(arps.size());
//            for(Object obj : arps){
//                JSONObject item = JSONObject.parseObject(obj.toString());
//                String lastcolock = item.getString("lastclock");
//                if(lastcolock != null && !lastcolock.equals("")){
//                    Long currentSecond = DateTools.currentTimeSecond();
//                    Long interval = DateTools.secondInterval(currentSecond, Long.parseLong(lastcolock));
//                    if(interval >= 300){
////                        continue;
//                    }
//                }
//                if(!item.get("error").equals("")){
//                    continue;
//                }
//                exe.execute(new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        JSONArray tags = JSONArray.parseArray(item.getString("tags"));
//                        if (tags != null && tags.size() > 0) {
//                            // 记录全网ip信息
//                            IpDetail ipDetail = new IpDetail();
//                            Arp arp = new Arp();
//                            arp.setDeviceName(deviceName);
////                            arp.setInterfaceName(item.getString("lastvalue"));
//                            arp.setUuid(uuid);
//                            for (Object t : tags) {
//                                JSONObject tag = JSONObject.parseObject(t.toString());
//                                if (tag.getString("tag").equals("ip")) {
//                                    arp.setIp(IpUtil.ipConvertDec(tag.getString("value")));
//                                    arp.setIpAddress(tag.getString("value"));
//                                    ipDetail.setIp(IpUtil.ipConvertDec(tag.getString("value")));
//                                }
//                                if (tag.getString("tag").equals("mac")) {
//                                    arp.setMac(tag.getString("value"));
//                                }
//                                if (tag.getString("tag").equals("mask")) {
//                                    arp.setMask(tag.getString("value"));
//                                }
//                                if (tag.getString("tag").equals("ifindex")) {
//                                    if(StringUtils.isInteger(tag.getString("value"))){
//                                        arp.setIndex(Integer.parseInt(tag.getString("value")));
//                                        arp.setInterfaceName(tag.getString("value"));
////                                                                        long begin = System.currentTimeMillis();
////                                        String interfaceName = itemUtil.getInterfaceName(ip, tag.getString("value"));
////                                        if(interfaceName == null || interfaceName.equals("")){
////                                            interfaceName = itemUtil.getInterfaceNameBy(ip, Integer.parseInt(tag.getString("value")));
////                                        }
////                                        arp.setInterfaceName(interfaceName);
////                                        long end = System.currentTimeMillis();
////                                        System.out.println("获取接口信息所需时间：" + (end - begin));
//                                    }
//
//                                }
//                            }
//                            arp.setTag("S");
////                   IP表：记录Ip使用率 (优化到IP表)
//                            ipDetail.setDeviceName(deviceName);
//                            IpDetail existingIp = ipDetailService.selectObjByIp(ipDetail.getIp());
//                            IpDetail init = ipDetailService.selectObjByIp("0.0.0.0");
//                            if(existingIp == null && ipDetail != null){
//                                ipDetailService.save(ipDetail);
//                            }else  if(existingIp != null){
//                                existingIp.setOnline(true);
//                                //                        existingIp.setTime(existingIp.getTime() + 1);
//                                //                        float num =(float)existingIp.getTime() / init.getTime();
//                                //                        int usage = Math.round(num * 100);
//                                //                        existingIp.setUsage(usage);
//                                ipDetailService.update(existingIp);
//                                init.setTime(existingIp.getTime() + 1);
//                                ipDetailService.update(init);
//                            }
//                            // 查询arp
//                            Map<String, Object> params = new HashMap();
//                            params.clear();
//                            params.put("ip", arp.getIp());
//                            params.put("deviceName", arp.getDeviceName());
//                            params.put("interfaceName", arp.getInterfaceName());
//                            List<Arp> localArps = arpService.selectObjByMap(params);
//                            if(localArps.size() == 0){
//                                arpService.save(arp);
//                            }
//                        }
//                    }
//                }));
//            }
//        }
//        if(exe != null){
//            exe.shutdown();
//        }
//        while (true) {
//            if (exe == null || exe.isTerminated()) {
//                // 补全（ipaddress）
//                JSONArray ipaddressitems = this.zabbixItemService.getItemIpAddressTag(ip);
//                if(ipaddressitems != null && ipaddressitems.size() > 0){
//                    Map params = new HashMap();
//                    ExecutorService exe2 = Executors.newFixedThreadPool(arps.size());
//                    for (Object obj : ipaddressitems){
//                        exe2.execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                JSONObject item = JSONObject.parseObject(obj.toString());
//                                JSONArray tags = JSONArray.parseArray(item.getString("tags"));
//                                if (tags != null && tags.size() > 0) {
//                                    // 记录全网ip信息
//                                    IpDetail ipDetail = new IpDetail();
//                                    Arp arp = new Arp();
//                                    arp.setDeviceName(deviceName);
////                                    arp.setInterfaceName(item.getString("lastvalue"));
//                                    arp.setUuid(uuid);
//                                    for(Object t : tags){
//                                        JSONObject tag = JSONObject.parseObject(t.toString());
//                                        if (tag.getString("tag").equals("ipaddr")) {
//                                            arp.setIp(IpUtil.ipConvertDec(tag.getString("value")).toString());
//                                            arp.setIpAddress(tag.getString("value"));
//                                            ipDetail.setIp(IpUtil.ipConvertDec(tag.getString("value")).toString());
//                                        }
//                                        if (tag.getString("tag").equals("mask")) {
//                                            arp.setMask(tag.getString("value"));
//                                        }
//                                        if (tag.getString("tag").equals("ifindex")) {
//                                            if(StringUtils.isInteger(tag.getString("value"))){
//                                                arp.setIndex(Integer.parseInt(String.valueOf(tag.get("value"))));
//                                                arp.setInterfaceName(String.valueOf(tag.get("value")));
//                                            }
//                                        }
//                                    }
//
////                  IP表：记录Ip使用率 (优化到IP表)
//                                    ipDetail.setDeviceName(deviceName);
//                                    IpDetail existingIp = ipDetailService.selectObjByIp(ipDetail.getIp());
//                                    IpDetail init = ipDetailService.selectObjByIp("0.0.0.0");
//                                    if(existingIp == null && ipDetail != null){
//                                        ipDetailService.save(ipDetail);
//                                    }else{
//                                        existingIp.setOnline(true);
////                        existingIp.setTime(existingIp.getTime() + 1);
////                        float num =(float)existingIp.getTime() / init.getTime();
////                        int usage = Math.round(num * 100);
////                        existingIp.setUsage(usage);
//                                        ipDetailService.update(existingIp);
//                                        init.setTime(existingIp.getTime() + 1);
//                                        ipDetailService.update(init);
//                                    }
//                                    // 查询arp
//                                    params.clear();
//                                    params.put("ip", arp.getIp());
//                                    params.put("deviceName", arp.getDeviceName());
//                                    params.put("interfaceName", arp.getInterfaceName());
//                                    List<Arp> localArps = arpService.selectObjByMap(params);
//                                    if(localArps.size() == 0
//                                            && arp.getInterfaceName() != null
//                                            && !arp.getInterfaceName().equals("")){
//                                        arp.setTag("L");
//                                        arpService.save(arp);
//                                    }else  if(localArps.size() >  0){
//                                        Arp local = localArps.get(0);
//                                        local.setTag("L");
//                                        local.setMask(arp.getMask());
//                                        arpService.update(local);
//                                    }
//                                }
//                            }
//                        });
//                    }
//
//                    exe2.shutdown();
//                    while (true) {
//                        if (exe2 == null || exe2.isTerminated()) {
//                            break;
//                        }
//                    }
//                }
//                break;
//            }
//        }
//    }

    @Override
    public Object getItemMac(String ip, String deviceName, String uuid, String deviceType) {
        Map params = new HashMap();
        // 先录入接口mac信息-默认标记L
        JSONArray interfaceItems = this.zabbixItemService.getItemInterfaces(ip);
        if(interfaceItems != null && interfaceItems.size() > 0) {
            // 录入Mac信息
            for (Object interfaceItem : interfaceItems) {
                Mac mac = new Mac();
                mac.setDeviceName(deviceName);
                mac.setUuid(uuid);
                mac.setDeviceIp(ip);
                mac.setDeviceType(deviceType);
                JSONObject item = JSONObject.parseObject(interfaceItem.toString());
                String lastcolock = item.getString("lastclock");
                if (lastcolock != null && !lastcolock.equals("")) {
                    Long currentSecond = DateTools.currentTimeSecond();
                    Long interval = DateTools.secondInterval(currentSecond, Long.parseLong(lastcolock));
                    if (interval >= 300) {
//                        continue;
                    }
                }
                if(!item.get("error").equals("")){
                    continue;
                }
                JSONArray tags = JSONArray.parseArray(item.getString("tags"));
                if (tags != null && tags.size() > 0) {
                    for (Object t : tags) {
                        JSONObject tag = JSONObject.parseObject(t.toString());

                        if (tag.getString("tag").equals("ifindex")) {
                            mac.setIndex(tag.getString("value"));
                            mac.setInterfaceName(tag.getString("value"));
                        }
                        if (tag.getString("tag").equals("ifmac")) {
                            mac.setMac(tag.getString("value"));
                            params.clear();
                            params.put("mac", tag.getString("value"));
                            List<Arp> arps = this.arpService.selectObjByMap(params);
                            if(arps.size() > 0){
                                Arp arp = arps.get(0);
                                mac.setIp(arp.getIp());
                                mac.setIpAddress(arp.getIpAddress());
                            }
                        }
//                        if (tag.getString("tag").equals("ifname")) {
//                            mac.setInterfaceName(tag.getString("value"));
//                        }
                    }
                    if (mac.getInterfaceName() != null && !mac.getInterfaceName().equals("")) {
                        params.clear();
                        params.put("deviceName", mac.getDeviceName());
//                        params.put("interfaceName", mac.getInterfaceName());
                        params.put("mac", mac.getMac());
                        List<Mac> macs = this.macService.selectByMap(params);
                        if (macs.size() == 0) {
                            mac.setTag("L");
                            if(mac.getMac().contains("0:0:5e:0")){
                                mac.setTag("LV");
                            }
                            this.macService.save(mac);
                        }
                    }
                }

            }
        }
        // 获取Mac item
        JSONArray items = this.zabbixItemService.getItemMac(ip);
        if(items != null && items.size() > 0) {
            params.clear();
            // 录入Mac信息
            for (Object obj : items) {
                JSONObject item = JSONObject.parseObject(obj.toString());
                Mac mac = new Mac();
                mac.setDeviceName(deviceName);
                mac.setDeviceIp(ip);
                mac.setDeviceType(deviceType);
                mac.setUuid(uuid);
                String lastcolock = item.getString("lastclock");
                if(lastcolock != null && !lastcolock.equals("")){
                    Long currentSecond = DateTools.currentTimeSecond();
                    Long interval = DateTools.secondInterval(currentSecond, Long.parseLong(lastcolock));
                    if(interval >= 300){
//                        continue;
                    }
                }
                if(!item.get("error").equals("")){
                    continue;
                }
                JSONArray tags = JSONArray.parseArray(item.getString("tags"));
                if (tags != null && tags.size() > 0) {
                    for (Object t : tags) {
                        JSONObject tag = JSONObject.parseObject(t.toString());
                        if (tag.getString("tag").equals("mac")) {
                            mac.setMac(tag.getString("value"));
                            params.clear();
                            params.put("mac", tag.getString("value"));
                            List<Arp> arps = this.arpService.selectObjByMap(params);
                            if(arps.size() > 0){
                                Arp arp = arps.get(0);
                                mac.setIp(arp.getIp());
                                mac.setIpAddress(arp.getIpAddress());
                            }
                        }
                        if (tag.get("tag").equals("portindex")) {
                            if(com.metoo.nspm.core.utils.StringUtils.isInteger(tag.getString("value"))){
                                mac.setInterfaceName(tag.getString("value"));
//                                try {
////                                    // 获取接口信息
//                                    long begin = System.currentTimeMillis();
//                                    String interfaceName = this.itemUtil.getInterfaceName(ip, tag.getString("value"));
//                                    long end = System.currentTimeMillis();
//                                    System.out.println("获取接口信息所需时间：" + (end - begin));
//                                    mac.setInterfaceName(interfaceName);
//                                    mac.setIndex(tag.getString("value"));
//                                } catch (NumberFormatException e) {
//                                    e.printStackTrace();
//                                }
                            }
                        }
                    }
                    if(mac.getInterfaceName() != null && !mac.getInterfaceName().equals("")){
                        params.clear();
                        params.put("deviceName", mac.getDeviceName());
//                        params.put("interfaceName", mac.getInterfaceName());
                        params.put("mac", mac.getMac());
                        List<Mac> macs = this.macService.selectByMap(params);
                        if(macs.size() == 0){
                            if(mac.getMac().contains("0:0:5e:0")){
                                mac.setTag("LV");
                            }
                            this.macService.save(mac);
                        }
                    }
                }
            }
        }
        return null;
    }



    /**
     * 获取ARP信息
     * @param ip 主机IP
     * @param deviceName 主机设备名
     * @return
     */
//    @Override
//    public Object getItemArpSByName(String ip, String deviceName) {
//        // 解析 Item 获取 接口信息
//        JSONArray items = this.getItemsByIp(ip);
//        if( items != null && items.size() > 0){
//            // 清空ip、mac、localIp表
//            this.ipAddressService.truncateTable();
//            this.macService.truncateTable();
//            this.localIpAddressService.truncateTable();
//            // 解析Item
//            // 获取mac信息
////            List<Map> macs = this.parseArpMac(items);// 获取mac信息
////            for(Map map : macs){
////                Mac mac = Json.fromJson(Mac.class, Json.toJson(map));
////                mac.setDeviceName(deviceName);
////                Mac obj = this.macService.getObjByInterfaceName(mac.getName());////// 增加设备名
////                if(obj == null){
////                    this.macService.save(mac);
////                }
////            }
//            // 获取mac2信息
//            JSONArray macs = this.zabbixItemService.getItemMacTag(ip);
//            if(macs != null && macs.size() > 0){
//                for(Object obj : macs){
//                    JSONObject mac = JSONObject.parseObject(obj.toString());
//                    JSONArray tags = JSONArray.parseArray(mac.getString("tags"));
//                    if (tags != null && tags.size() > 0) {
//                        Mac mac = new Mac();
//                        mac.setDeviceName(deviceName);
//                        for (Object t : tags) {
//                            JSONObject tag = JSONObject.parseObject(t.toString());
//                            if (tag.getString("tag").equals("mac")) {
//                                mac.setMac(tag.getString("value"));
//                            }
//                            if (tag.getString("tag").equals("portindex")) {
////                                mac.setSequence(tag.getInteger("value"));
//                                String interfaceName = this.itemUtil.getInterfaceName(ip, tag.getInteger("value"));
//                                mac.setName(interfaceName);
//                            }
//                        }
//                        Mac instance = this.macService.getObjByInterfaceName(mac.getName());////// 增加设备名
//                        if(instance == null){
//                            this.macService.save(mac);
//                        }
//                    }
//                }
//            }
//            // 获取Ip信息
////            List<Map> ips = this.parseArpIp(items);
////            for(Map map : ips){
////                // 临时表
////                IpAddress ipAddress = Json.fromJson(IpAddress.class, Json.toJson(map));
////                ipAddress.setDeviceName(deviceName);
////                IpAddress obj = this.ipAddressService.selectObjByIp(ipAddress.getIp());
////                if(obj == null && ipAddress != null){
////                    this.ipAddressService.save(ipAddress);
////                }
////                // IP表
////                IpDetail ipDetail = Json.fromJson(IpDetail.class, Json.toJson(map));
////                ipDetail.setDeviceName(deviceName);
////                IpDetail existingIp = this.ipDetailService.selectObjByIp(ipDetail.getIp());
////                IpDetail init = this.ipDetailService.selectObjByIp("0.0.0.0");
////                if(existingIp == null && ipDetail != null){
////                    this.ipDetailService.save(ipDetail);
////                }else{
////                    existingIp.setLine(true);
////                    existingIp.setTime(existingIp.getTime() + 1);
////                    float num =(float)ipDetail.getTime() / init.getTime();
////                    int usage = Math.round(num * 100);
////                    existingIp.setUsage(usage);
////                    this.ipDetailService.update(ipDetail);
////                    init.setTime(existingIp.getTime() + 1);
////                    this.ipDetailService.update(init);
////                }
////            }
//
//            JSONArray arps = this.zabbixItemService.getItemArpTag(ip);
//            if(arps != null && arps.size() > 0){
//                for(Object obj : arps){
//                    JSONObject arp = JSONObject.parseObject(obj.toString());
//                    JSONArray tags = JSONArray.parseArray(arp.getString("tags"));
//                    if (tags != null && tags.size() > 0) {
//                        IpAddress ipAddress = new IpAddress();
//                        ipAddress.setDeviceName(deviceName);
//                        // 记录全网ip信息
//                        IpDetail ipDetail = new IpDetail();
//                        for (Object t : tags) {
//                            JSONObject tag = JSONObject.parseObject(t.toString());
//                            if (tag.getString("tag").equals("ip")) {
//                                ipAddress.setIp(tag.getString("value"));
//                                Scanner sc = new Scanner(tag.getString("value")).useDelimiter("\\.");
//                                StringBuffer sb = new StringBuffer();
//                                sb.append(sc.nextLong()).append(".").append(sc.nextLong()).append(".").append(sc.nextLong());
//                                ipAddress.setIpSegment(sb.toString());
//
//                                ipDetail.setIp(tag.getString("value"));
//                            }
//                            if (tag.getString("tag").equals("mac")) {
//                                ipAddress.setMac(tag.getString("value"));
//                            }
//                            if (tag.getString("tag").equals("ifindex")) {
//                                ipAddress.setSequence(tag.getInteger("value"));
//                            }
//                        }
//                        IpAddress instance = this.ipAddressService.selectObjByIp(ipAddress.getIp());
//                        if(instance == null){
//                            this.ipAddressService.save(ipAddress);
//                        }
////                         IP表
//                        ipDetail.setDeviceName(deviceName);
//                        IpDetail existingIp = this.ipDetailService.selectObjByIp(ipDetail.getIp());
//                        IpDetail init = this.ipDetailService.selectObjByIp("0.0.0.0");
//                        if(existingIp == null && ipDetail != null){
//                            this.ipDetailService.save(ipDetail);
//                        }else{
//                            existingIp.setOnline(true);
//                            existingIp.setTime(existingIp.getTime() + 1);
//                            float num =(float)ipDetail.getTime() / init.getTime();
//                            int usage = Math.round(num * 100);
//                            existingIp.setUsage(usage);
//                            this.ipDetailService.update(ipDetail);
//                            init.setTime(existingIp.getTime() + 1);
//                            this.ipDetailService.update(init);
//                        }
//                    }
//                }
//            }
//
//            // 获取补全Ip
////            List<Map> localIpAddressList = this.parseArpIpAddress(items);
////            for(Map map : localIpAddressList){
////                LocalIpAddress localIpAddress = Json.fromJson(LocalIpAddress.class, Json.toJson(map));
////                Map localIpAddressMap = new HashMap();
////                localIpAddress.setDeviceName(deviceName);
////                localIpAddressMap.put("deviceName", deviceName);
////                localIpAddressMap.put("sequence", localIpAddress.getSequence());
////                List<LocalIpAddress> obj = this.localIpAddressService.selectObjByMap(localIpAddressMap);
////                if(obj.size() == 0){
////                    this.localIpAddressService.save(localIpAddress);
////                }
////            }
//            JSONArray ipaddresses = this.zabbixItemService.getItemIpAddressTag(ip);
//            if(ipaddresses != null && ipaddresses.size() > 0){
//                for (Object obj : ipaddresses){
//                    JSONObject arp = JSONObject.parseObject(obj.toString());
//                    JSONArray tags = JSONArray.parseArray(arp.getString("tags"));
//                    if (tags != null && tags.size() > 0) {
//                        LocalIpAddress localIpAddress = new LocalIpAddress();
//                        localIpAddress.setDeviceName(deviceName);
//                        for(Object t : tags){
//                            JSONObject tag = JSONObject.parseObject(t.toString());
//                            if (tag.getString("tag").equals("ip")) {
//                                localIpAddress.setIp(tag.getString("value"));
//                                Scanner sc = new Scanner(tag.getString("value")).useDelimiter("\\.");
//                                StringBuffer sb = new StringBuffer();
//                                sb.append(sc.nextLong()).append(".").append(sc.nextLong()).append(".").append(sc.nextLong());
//                                localIpAddress.setIpSegment(sb.toString());
//                            }
//                            if (tag.getString("tag").equals("ifindex")) {
//                                localIpAddress.setSequence(tag.getInteger("value"));
//                            }
//                        }
//                        Map localIpAddressMap = new HashMap();
//                        localIpAddressMap.put("deviceName", deviceName);
//                        localIpAddressMap.put("sequence", localIpAddress.getSequence());
//                        List<LocalIpAddress> instances = this.localIpAddressService.selectObjByMap(localIpAddressMap);
//                        if(instances.size() == 0){
//                            this.localIpAddressService.save(localIpAddress);
//                        }
//                    }
//                }
//            }
//
//            // 生成ERP表
//            List<Mac> macList = this.macService.selectByMap(null);
//            for (Mac mac : macList){
//                Map macMap = new HashMap();
//                macMap.put("mac", mac.getMac());
//                macMap.put("sequence", mac.getSequence());
//                macMap.put("device_name", deviceName);
//                List<IpAddress> ipAddressList = this.ipAddressService.selectObjByMap(macMap);
//                if(ipAddressList.size() > 0){
//                    IpAddress ipAddress = ipAddressList.get(0);
//                    Arp LocalArp = new Arp();
//                    LocalArp.setDeviceName(deviceName);
//                    LocalArp.setInterfaceName(mac.getName());
//                    LocalArp.setSequence(mac.getSequence());
//                    LocalArp.setMac(ipAddress.getMac());
//                    LocalArp.setIp(ipAddress.getIp());
//                    LocalArp.setTag("L");
//                    this.arpService.save(LocalArp);
//                    // 查询shar
//                    Map map = new HashMap();
//                    map.put("ipSegment", ipAddress.getIpSegment());
//                    map.put("ipSelf", ipAddress.getIp());
//                    map.put("deviceName", ipAddress.getDeviceName());
//                    List<IpAddress> shares = this.ipAddressService.selectObjByMap(map);
//                    for (IpAddress share : shares){
//                        Arp shareArp = new Arp();
//                        shareArp.setDeviceName(deviceName);
//                        shareArp.setInterfaceName(mac.getName());
//                        shareArp.setSequence(mac.getSequence());
//                        shareArp.setMac(share.getMac());
//                        shareArp.setIp(share.getIp());
//                        shareArp.setTag("S");
//                        this.arpService.save(shareArp);// 循环插入和批量插入那个速度快
//                    }
//                }
//            }
//            // 补全Arp
//            Map map = new HashMap();
//            map.put("device_name", deviceName);
//            List<LocalIpAddress> localIpAddresses = this.localIpAddressService.selectObjByMap(map);
//            if(localIpAddresses.size() > 0){
//                for(LocalIpAddress localIp : localIpAddresses){
//                    Map macMap = new HashMap();
//                    macMap.put("sequence", localIp.getSequence());
//                    macMap.put("deviceName", localIp.getDeviceName());
//                    List<Mac> macList = this.macService.selectByMap(macMap);
//                    if(macList.size() > 0){
//                        Mac mac = macList.get(0);
//                        Map macMap = new HashMap();
//                        macMap.put("mac", mac.getMac());
//                        macMap.put("deviceName", deviceName);
//                        List<Arp> arpList = this.arpService.selectObjByMap(macMap);
//                        if(arpList.size() == 0){
//                            Arp shareArp = new Arp();
//                            shareArp.setDeviceName(deviceName);
//                            shareArp.setInterfaceName(mac.getName());
//                            shareArp.setSequence(mac.getSequence());
//                            shareArp.setMac(mac.getMac());
//                            shareArp.setIp(localIp.getIp());
//                            shareArp.setTag("L");
//                            this.arpService.save(shareArp);// 循环插入和批量插入那个速度快
//                            // 查询shar
//                            Map map1 = new HashMap();
//                            map1.put("ipSegment", localIp.getIpSegment());
//                            map1.put("ipSelf", localIp.getIp());
//                            map1.put("deviceName", localIp.getDeviceName());
//                            map1.put("sequence", localIp.getSequence());
//                            List<IpAddress> shares = this.ipAddressService.selectObjByMap(map1);
//                            for (IpAddress share : shares){
//                                Arp shareArp1 = new Arp();
//                                shareArp1.setDeviceName(deviceName);
//                                shareArp1.setInterfaceName(mac.getName());
//                                shareArp1.setSequence(mac.getSequence());
//                                shareArp1.setMac(share.getMac());
//                                shareArp1.setIp(share.getIp());
//                                shareArp1.setTag("S");
//                                this.arpService.save(shareArp1);// 循环插入和批量插入那个速度快
//                            }
//                        }
//                    }
//                }
//            }
//
//        }
//        return null;
//    }

    /**
     * 解析item（获取接口-mac）
     * @param
     * @return
     */
//    public List<Map> parseArpMac(JSONArray items){
//        List list = new ArrayList();
//        for (Object array : items) {
//            JSONObject item = JSONObject.parseObject(array.toString());
//            // 获取索引
//            Integer index = null;
//            String interfaceName = "";
//            String name = item.getString("name");
//            String lastvalue = item.getString("lastvalue");
//            String error = item.getString("error");
//            if(name.contains("if") && name.contains("MAC") && error.equals("")){
//                Map map = new HashMap();
//                int i = name.indexOf(" ") + 1;
//                int ii = name.indexOf(" ", i + 1);
//                String sequence = name.substring(i, ii);
//                index = Integer.parseInt(sequence);
//                int start = name.indexOf(" ", i + 1) + 1;
//                int last = name.indexOf(":");
//                interfaceName = name.substring(start, last);
//                if(lastvalue != null && !lastvalue.equals("")){
//                    map.put("name", interfaceName);
//                    map.put("mac", lastvalue);
//                    map.put("sequence", index);
//                    list.add(map);
//                }
//            }
//        }
//        return list;
//    }
//
//    /**
//     * 解析Item（获取IP信息）
//     * @param items
//     * @return
//     */
//    public List<Map> parseArpIp(JSONArray items){
//        List list = new ArrayList();
//        for (Object array : items) {
//            JSONObject element = JSONObject.parseObject(array.toString());
//            // 获取索引
//            String name = element.getString("name");
//            String lastvalue = element.getString("lastvalue");
//            String error = element.getString("error");
//            if(name.contains("ip") && name.contains("MAC") && error.equals("")){
//                Map map = new HashMap();
//                int n = name.indexOf(" ") + 1;
//                int j = name.indexOf(".");
//                Integer sequence = Integer.parseInt(name.substring(n,j));
//                StringBuffer nameReverse = new StringBuffer(name).reverse();
//                String key = nameReverse.toString();
//                int start = 4;
//                int i = key.indexOf(".");
//                i = key.indexOf(".", i+ 1);
//                i = key.indexOf(".", i+ 1);
//                int end = key.indexOf(".", i+ 1);
//                String ip = nameReverse.substring(start, end);
//                StringBuffer ipReverse = new StringBuffer(ip).reverse();
//                if(lastvalue != null && !lastvalue.equals("")){
//                    map.put("ip", ipReverse.toString().trim());
//                    map.put("mac", lastvalue);
//                    int ipSegmentIndex = ipReverse.indexOf(".");
//                    ipSegmentIndex = ipReverse.indexOf(".", ipSegmentIndex + 1);
//                    ipSegmentIndex = ipReverse.indexOf(".", ipSegmentIndex + 1);
//                    map.put("ipSegment", ipReverse.substring(0, ipSegmentIndex).trim());
//                    map.put("sequence", sequence);
//                    list.add(map);
//                }
//            }
//        }
//        return list;
//    }
//
//    /**
//     * 解析Item（获取补全IP）
//     * @param items
//     * @return
//     */
//    public List<Map> parseArpIpAddress(JSONArray items){
//        List list = new ArrayList();
//        for (Object array : items) {
//            JSONObject element = JSONObject.parseObject(array.toString());
//            // 获取索引
//            String name = element.getString("name");
//            String lastvalue = element.getString("lastvalue");
//            String error = element.getString("error");
//            if(name.contains("Ipaddress") && name.contains("IPMASK") && error.equals("")){
//                Map map = new HashMap();
//                int n = name.indexOf(" ") + 1;
//                int j = name.indexOf(" ", n + 1);
//                Integer sequence = Integer.parseInt(name.substring(n,j));
//                int i = name.indexOf(":");
//                String ipAddress = name.substring(j, i);
//                if(lastvalue != null && !lastvalue.equals("")){
//                    map.put("ip", ipAddress.trim());
//                    int ipSegmentIndex = ipAddress.indexOf(".");
//                    ipSegmentIndex = ipAddress.indexOf(".", ipSegmentIndex + 1);
//                    ipSegmentIndex = ipAddress.indexOf(".", ipSegmentIndex + 1);
//                    map.put("ipSegment", ipAddress.substring(0, ipSegmentIndex).trim());
//                    map.put("sequence", sequence);
//                    list.add(map);
//                }
//            }
//        }
//        return list;
//    }


    public Object getInterfaceHistory1(String ip, Integer limit, Long time_till, Long time_from) {
        Object object = this.getItem(ip);
        // 解析item 信息
        Object inteface = this.parseHistory2(object, time_till, time_from);
        return inteface;
    }
    @Override
    public Object getInterfaceHistory(String ip, Integer limit, Long time_till, Long time_from) {
        JSONArray items = this.zabbixItemService.getItemTags(ip);
        if(items.size() > 0){
            List list = new ArrayList();
            ExecutorService exe = Executors.newFixedThreadPool(items.size());
            for(Object obj : items){
                    exe.execute(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Map tagMap = new HashMap();
                            JSONObject item = JSONObject.parseObject(obj.toString());
                            if(item.getString("tags") != null){
                                JSONArray tags = JSONArray.parseArray(item.getString("tags"));
                                for(Object tagObj : tags){
                                    JSONObject tag = JSONObject.parseObject(tagObj.toString());
                                    if(tag != null){
                                        if(tag.getString("tag").equals("obj") && !tag.getString("value").equals("ifbasic")){
                                            break;
                                        }
                                        if(tag.getString("tag").equals("ifname")){
                                            tagMap.put("name", tag.getString("value"));
                                        }
                                        if(tag.getString("tag").equals("ifindex")){
                                            tagMap.put("index", tag.getString("value"));
                                            //sent
                                            // received
                                            // Speed
                                            int index = Integer.parseInt(tag.getString("value"));
                                            if(index >= 0){
                                                for(Object other : items){
                                                    JSONObject otherItems = JSONObject.parseObject(other.toString());
                                                    if(otherItems.getString("tags") != null){
                                                        JSONArray otherTags = JSONArray.parseArray(otherItems.getString("tags"));
                                                        for(Object oterObj : otherTags){
                                                            JSONObject oterTag = JSONObject.parseObject(oterObj.toString());
                                                            if(oterTag.getString("tag").equals("obj") && oterTag.getString("value").equals("ifsent")){
                                                                for(Object childObj : otherTags){
                                                                    JSONObject childTag = JSONObject.parseObject(childObj.toString());
                                                                    if(childTag.getString("tag").equals("ifindex")){
                                                                        int otherIndex = Integer.parseInt(childTag.getString("value"));
                                                                        if(otherIndex == index){
                                                                            Object getHistory = getHistory(Arrays.asList(otherItems.getString("itemid")), null, time_till, time_from);
                                                                            Object history =  parseHistoryZeroize(getHistory, time_till, time_from);
                                                                            tagMap.put("sentHistory", history);
                                                                        }
                                                                    }
                                                                }

                                                                continue;
                                                            }
                                                            if(oterTag.getString("tag").equals("obj") && oterTag.getString("value").equals("ifreceived")){
                                                                for(Object childObj : otherTags){
                                                                    JSONObject childTag = JSONObject.parseObject(childObj.toString());
                                                                    if(childTag.getString("tag").equals("ifindex")){
                                                                        int otherIndex = Integer.parseInt(childTag.getString("value"));
                                                                        if(otherIndex == index){
                                                                            Object historyItems = getHistory(Arrays.asList(otherItems.getString("itemid")), null, time_till, time_from);
                                                                            Object history =  parseHistoryZeroize(historyItems, time_till, time_from);
//                                                               JSONObject json = JSONObject.parseObject(hisotory.toString());
                                                                            tagMap.put("receivedHistory", history);
                                                                        }
                                                                    }
                                                                }
                                                                continue;
                                                            }
                                                            if(oterTag.getString("tag").equals("obj") && oterTag.getString("value").equals("ifspeed")){
                                                                for(Object childObj : otherTags){
                                                                    JSONObject childTag = JSONObject.parseObject(childObj.toString());
                                                                    if(childTag.getString("tag").equals("ifindex")){
                                                                        int otherIndex = Integer.parseInt(childTag.getString("value"));
                                                                        if(otherIndex == index){
                                                                            tagMap.put("speed", item.getString("lastvalue"));
                                                                        }
                                                                    }
                                                                }
                                                                continue;
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            if(!tagMap.isEmpty()){
                                list.add(tagMap);
                            }
                        }
                    }));

            }
            if(exe != null){
                exe.shutdown();
            }
            while (true) {
                if (exe.isTerminated()) {
                    return list;
                }
            }

        }
        return null;
    }



    public Object parseHistory2(Object object, Long time_till, Long time_from){
        JSONObject item = JSONObject.parseObject(object.toString());
        if(item.get("result") != null){
            JSONArray result = JSONArray.parseArray(item.get("result").toString());
            Map<Integer, Map<String, String>> map = new HashMap();
            for (Object array : result){
                JSONObject element = JSONObject.parseObject(array.toString());
                // 获取索引
                Integer index = null;
                String interfaceName = "";
                String name = element.getString("name");
                String lastvalue = element.getString("lastvalue");
                String itemid = element.getString("itemid");
                String error = element.getString("error");
                if(name.contains("Interface") && name.contains("sent")
                        || name.contains("Interface") && name.contains("received")
                        || name.contains("Interface") && name.contains("Speed")
                        && error.equals("")){
                    int i = name.indexOf(" ") + 1;
                    int ii = name.indexOf(" ", i + 1);
                    String sequence = name.substring(i, ii);
                    index = Integer.parseInt(sequence);
                    int start = name.indexOf(" ", i + 1) + 1;
                    int last = name.indexOf(":");
                    interfaceName = name.substring(start, last);
                }
                if(index != null){
                    Map eleMap = map.get(index);
                    if(eleMap == null){
                        eleMap = new HashMap();
                        map.put(index, eleMap);
                    }
                    if(StringUtils.isNotEmpty(interfaceName)){
                        eleMap.put("name", interfaceName);
                    }
                    if(name.contains("sent")){
                        Object obj = this.getHistory(Arrays.asList(itemid), null, time_till, time_from);
                        Object hisotory =  this.parseHistoryZeroize(obj, time_till, time_from);
//                        JSONObject json = JSONObject.parseObject(hisotory.toString());
                        eleMap.put("sentHistory", hisotory);
                    }
                    if(name.contains("received")){
                        Object obj = this.getHistory(Arrays.asList(itemid), null, time_till, time_from);
                        Object hisotory =  this.parseHistoryZeroize(obj, time_till, time_from);
//                        JSONObject json = JSONObject.parseObject(hisotory.toString());
                        eleMap.put("receivedHistory", hisotory);
                    }
                    if(name.contains("Speed")){
                        eleMap.put("speed", lastvalue);
                    }
                }
            }
            if(!map.isEmpty()){
                List list = new ArrayList();
                for (Integer key : map.keySet()){
                    list.add(map.get(key));
                }
                return list;
            }
            return map;
        }
        return null;
    }

    @Override
    public Object getServer(String ip) {
        Object object = this.getItem(ip);
        if(object != null){
            // 解析item
            List names = Arrays.asList(
                    "Load average (5m avg)",
                    "CPU utilization",
                    "Number of CPUs",
                    "Available memory",
                    "Memory utilization",
                    "Total memory",
                    "/etc/hosts: Space utilization",
                    "/etc/hosts: Total space",
                    "/etc/hosts: Used space",
                    "Host name of Zabbix agent running",
                    "Operating system",
                    "System uptime");

            return this.parseServer(object, names);
        }
        return null;
    }

    /**
     * 解析item获取服务器信息
     * @param object
     * @param names
     * @return
     */
    public Object parseServer(Object object, List names){
        JSONObject item = JSONObject.parseObject(object.toString());
        if(item.get("result") != null){
            JSONArray result = JSONArray.parseArray(item.get("result").toString());
            Map map = new HashMap();
            for (Object array : result){
                JSONObject element = JSONObject.parseObject(array.toString());
                // 获取索引
                String name = element.getString("name");
                if(names.contains(name)){
                    String lastvalue = element.getString("lastvalue");
                    if(name.equalsIgnoreCase("Load average (5m avg)")){
                        map.put("load_average", lastvalue);
                    }
                    if(name.equalsIgnoreCase("CPU utilization")){
                        map.put("cpu_utilization", lastvalue);
                    }
                    if(name.equalsIgnoreCase("Number of CPUs")){
                        map.put("number_of_cpus", lastvalue);
                    }
                    if(name.equalsIgnoreCase("Available memory")){
                        map.put("available_memory", lastvalue);
                    }
                    if(name.equalsIgnoreCase("Memory utilization")){
                        map.put("memory_utilization", lastvalue);
                    }
                    if(name.equalsIgnoreCase("Total memory")){
                        map.put("total_memory", lastvalue);
                    }
                    if(name.equalsIgnoreCase("/etc/hosts: Space utilization")){
                        map.put("space_utilization", lastvalue);
                    }
                    if(name.equalsIgnoreCase("/etc/hosts: Total space")){
                        map.put("total_space", lastvalue);
                    }
                    if(name.equalsIgnoreCase("/etc/hosts: Used space")) {
                        map.put("used_space", lastvalue);
                    }
                    if(name.equalsIgnoreCase("Host name of Zabbix agent running")) {
                        map.put("host_name_if_zabbix_agent_running", lastvalue);
                    }
                    if(name.equalsIgnoreCase("Operating system")) {
                        map.put("operating_system", lastvalue);
                    }
                    if(name.equalsIgnoreCase("System uptime")) {
                        map.put("system_uptime", lastvalue);
                    }
                }
            }
            return map;
        }
        return null;
    }

    /**
     * 解析item获取历史信息
     * @param object
     * @param names
     * @return
     */
    public Object parseHistoryServer(Object object, List names, Long time_till, Long time_from){
        JSONObject item = JSONObject.parseObject(object.toString());
        if(item.get("result") != null){
            JSONArray result = JSONArray.parseArray(item.get("result").toString());
            Map map = new HashMap();
            for (Object array : result){
                JSONObject element = JSONObject.parseObject(array.toString());
                // 获取索引
                String name = element.getString("name");
                if(names.contains(name)){
                    String lastvalue = element.getString("lastvalue");
                    if(name.equalsIgnoreCase("Load average (1m avg)")){
                        map.put("load_average", lastvalue);
                        Object historys =  this.getHistoryResult(Arrays.asList(element.getInteger("itemid")), null, time_till, time_from);
                        map.put("load_average_history", historys);
                    }
                    if(name.equalsIgnoreCase("CPU utilization")){
                        map.put("cpu_utilization", lastvalue);
                        Object historys =  this.getHistoryResult(Arrays.asList(element.getInteger("itemid")), null, time_till, time_from);
                        map.put("cpu_utilization_history", historys);
                    }
                    if(name.equalsIgnoreCase("Memory utilization")){
                        map.put("memory_utilization", lastvalue);
                        Object historys =  this.getHistoryResult(Arrays.asList(element.getInteger("itemid")), null, time_till, time_from);
                        map.put("memory_utilization_history", historys);
                    }
                    if(name.equalsIgnoreCase("Number of CPUs")){
                        map.put("number_of_cpus", lastvalue);
                    }
                    if(name.equalsIgnoreCase("Available memory")){
                        map.put("available_memory", lastvalue);
                    }
                    if(name.equalsIgnoreCase("Total memory")){
                        map.put("total_memory", lastvalue);
                    }
                    if(name.equalsIgnoreCase("/etc/hosts: Space utilization")){
                        map.put("space_utilization", lastvalue);
                    }
                    if(name.equalsIgnoreCase("/etc/hosts: Total space")){
                        map.put("total_space", lastvalue);
                    }
                    if(name.equalsIgnoreCase("/etc/hosts: Used space")) {
                        map.put("used_space", lastvalue);
                    }
                    if(name.equalsIgnoreCase("Host name of Zabbix agent running")) {
                        map.put("host_name_if_zabbix_agent_running", lastvalue);
                    }
                    if(name.equalsIgnoreCase("Operating system")) {
                        map.put("operating_system", lastvalue);
                    }
                    if(name.equalsIgnoreCase("System uptime")) {
                        map.put("system_uptime", lastvalue);
                    }
                }
            }
            return map;
        }
        return null;
    }

    @Override
    public Object flow(String ip, String name) {
        Object object = this.getItem(ip);
        // 解析item 信息
        Object inteface = this.parseItemFlow(object, name);
        return inteface;
    }

    public Object parseUsage(Object object){
        JSONObject itemJSON = JSONObject.parseObject(object.toString());
        Map data = new HashMap();
        if(itemJSON.get("result") != null){
            JSONArray itemArray = JSONArray.parseArray(itemJSON.getString("result"));
            if(itemArray.size() > 0){
                data.put("flag", 0);
                for(Object array : itemArray){
                    JSONObject item = JSONObject.parseObject(array.toString());
                    data.put(item.getString("name"), item.getString("lastvalue"));
                    data.put("flag", this.verifyThresholdValue(data));
                }
            }
        }
        return data;
    }

    /**
     * 解析item获取历史信息
     * @param object
     * @param limit
     * @param time_till
     * @param time_from
     * @return
     */
    public Object parseHistory(Object object, Integer limit, Long time_till, Long time_from){
        JSONObject itemJSON = JSONObject.parseObject(object.toString());
        Map data = new HashMap();
        if(itemJSON.get("result") != null){
            JSONArray itemArray = JSONArray.parseArray(itemJSON.getString("result"));
            if(itemArray.size() > 0){
                for(Object array : itemArray){
                    JSONObject item = JSONObject.parseObject(array.toString());
                    String error = item.getString("error");
                    if(error.equals("")){
                        data.put(item.getString("name"), item.getString("lastvalue"));
                        // 获取 CPU 图形
                        if(item.getString("name").equals("CpuUsage")){
                            Object historys =  this.getHistory(Arrays.asList(item.getInteger("itemid")), limit, time_till, time_from);
                            Object result = this.parseHistoryZeroize(historys, time_till, time_from);
                            data.put("CpuUsage", result);
                        }
                        // 获取 Mem 图形
                        if(item.getString("name").equals("MemUsage")){
                            Object historys =  this.getHistory(Arrays.asList(item.getInteger("itemid")), limit, time_till, time_from);
                            Object result = this.parseHistoryZeroize(historys, time_till, time_from);
                            data.put("MemUsage", result);
                        }
                    }
                }
            }
        }
        return data;
    }

    /**
     * item历史记录，图形，数据补零，便于前端画图
     *
     * @param obj
     * @param time_till
     * @param time_from
     * @return
     */
    public Object parseHistoryZeroize(Object obj, Long time_till, Long time_from){
        if(obj != null){
            JSONObject json = JSONObject.parseObject(obj.toString());
            if (json.getString("result") != null){
                JSONArray arrays = JSONArray.parseArray(json.getString("result"));
                Long start = time_from;
                Long end = null;
                List list = new ArrayList();
                for (int j = 0; j < arrays.size(); j++){
                    JSONObject result = JSONObject.parseObject(arrays.get(j).toString());
                    if(start != null){
                        end = result.getLong("clock");
                        // 比较两个记录时间间隔，间隔为/分钟
                        Long diff = (end - start) * 1000 ;
                        long diffMinutes = diff / (60 * 1000) % 60;
                        if(diffMinutes > 1){
                            for(long i = 1; i < diffMinutes; i++){
                                long n = 0;
                                if(j == 0){
                                    n = i-1;
                                }
                                String startTime = this.dateTools.longToStr((start * 1000) + n * 60000 , "yyyy-MM-dd HH:mm");
                                Map map = new HashMap();
                                map.put("clock", startTime);
                                map.put("itemid", result.get("itemid"));
                                map.put("value", 0);
                                list.add(map);
                            }
                        }
                        start = end;
                        result.put("clock", this.dateTools.longToStr(result.getLong("clock") * 1000, "yyyy-MM-dd HH:mm"));
                        list.add(result);
                        // 后补
                        if(j+1 == arrays.size()){
                            Long till_diff = (time_till - end) * 1000;
                            long till_diffMinutes = till_diff / (60 * 1000) % 60;
                            if(till_diffMinutes > 1){
                                for(long i = 0; i < diffMinutes; i++){
                                    String startTime = this.dateTools.longToStr((start * 1000) + i * 60000 , "yyyy-MM-dd HH:mm");
                                    Map map = new HashMap();
                                    map.put("clock", startTime);
                                    map.put("itemid", result.get("itemid"));
                                    map.put("value", 0);
                                    list.add(map);
                                }
                            }
                        }
                    }
                }
                return list;
            }
        }
        return null;
    }



    public Object parseItemFlow(Object object, String portName){
        if(object != null){
            JSONObject item = JSONObject.parseObject(object.toString());
            if(item.get("result") != null){
                JSONArray result = JSONArray.parseArray(item.get("result").toString());
                Map<Integer, Map<String, String>> map = new HashMap<Integer, Map<String, String>>();
                for (Object array : result){
                    JSONObject element = JSONObject.parseObject(array.toString());
                    // 获取索引
                    Integer index = null;
                    String name = element.getString("name");
                    String error = element.getString("error");
                    if(name.contains(portName) && error.equals("")){
                        String interfaceName = "";
                        Long speed = null;
                        String status = "";
                        String lastvalue = element.getString("lastvalue");
                        if(name.contains("Interface") && name.contains("received") || name.contains("sent") || name.contains("Speed")){
                            int i = name.indexOf(" ") + 1;
                            int ii = name.indexOf(" ", i + 1);
                            String sequence = name.substring(i, ii);
                            try {
                                index = Integer.parseInt(sequence);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                index = null;
                            }
                            int start = name.indexOf(" ", i + 1) + 1;
                            int last = name.indexOf(":");
                            interfaceName = name.substring(start, last);
                            if(name.contains("Speed")){
                                speed = element.getLong("lastvalue");
                            }
                        }

                        if(name.contains("Operational status")){
                            int i = name.indexOf(" ") + 1;
                            int ii = name.indexOf(" ", i + 1);
                            String sequence = name.substring(i, ii);
                            index = Integer.parseInt(sequence);
                            switch (lastvalue){
                                case "1":
                                    status = "up";
                                    break;
                                case "2":
                                    status = "down";
                                    break;
                                default:
                                    status = "unknown";
                            }
                        }

                        if(index != null){
                            Map eleMap = map.get(index);
                            if(eleMap == null){
                                eleMap = new HashMap();
                                map.put(index, eleMap);
                            }
                            Integer i = null;
                            if(name.contains("received")){
                                i = 1;
                            }
                            if(name.contains("sent")){
                                i = 2;
                            }
                            if(i != null && StringUtils.isNotEmpty(lastvalue)){
                                eleMap.put(i != null && i == 1 ? "received" : "sent", lastvalue);
                            }
                            if(StringUtils.isNotEmpty(interfaceName)){
                                eleMap.put("name", interfaceName);
                            }
                            if(null != speed){
                                eleMap.put("speed", speed);
                            }
                            if(StringUtils.isNotEmpty(status)){
                                eleMap.put("status", status);
                            }
                        }
                    }
                }
                return map;
            }
        }
        return null;
    }


    public Object getHistory(List itemid, Integer limit){
        HistoryDTO dto = new HistoryDTO();
        dto.setItemids(itemid);
        dto.setLimit(limit);
        Object historys = this.historyService.getHistory(dto);
//        JSONObject json = JSONObject.parseObject(historys.toString());
//        return json.get("result");
        return historys;
    }

    public Object getHistory(List itemid, Integer limit, Long time_till, Long time_from){
        HistoryDTO dto = new HistoryDTO();
        dto.setItemids(itemid);
        dto.setTime_from(time_from);
        dto.setTime_till(time_till);
        Object historys = this.historyService.getHistory(dto);
//        JSONObject json = JSONObject.parseObject(historys.toString());
//        return json.get("result");
        return historys;
    }

    public Object getHistoryResult(List itemid, Integer limit, Long time_till, Long time_from){
        HistoryDTO dto = new HistoryDTO();
        dto.setItemids(itemid);
        dto.setLimit(limit);
        dto.setTime_from(time_from);
        dto.setTime_till(time_till);
        Object historys = this.historyService.getHistory(dto);
        JSONObject json = JSONObject.parseObject(historys.toString());
        if(json.getString("result") != null){
            return json.get("result");
        }
        return null;
    }



    // 验证阈值
    public int verifyThresholdValue(Map data){
        int lastValue = 0;
        for (Object key : data.keySet()){
            if(key.equals("CpuUsage") ){
                Threshold threshold = this.thresholdService.query();
                if(Integer.parseInt(data.get(key).toString()) >= threshold.getCpu()){
                    lastValue = 1;
                }
            }
            if(key.equals("MemUsage") ){
                Threshold threshold = this.thresholdService.query();
                if(Integer.parseInt(data.get(key).toString()) >= threshold.getMemory()){
                    lastValue = 1;
                }
            }
        }
        return lastValue;
    }

    @Override
    public List<String> getHostIps(String ip) {
        // 定义与要查询的标签

        return null;
    }

    @Override
    public void gatherArp() {
        List<Map> ipList = this.topoNodeService.queryMetoo();
        if (ipList != null && ipList.size() > 0) {
            // truncate
            this.arpService.truncateTable();
            List<Long> arpIpList = new ArrayList();
            for (Map map : ipList) {
                List<Long> arpIps = this.getItemArp(map.get("ip").toString(), map.get("deviceName").toString(),
                        String.valueOf(map.get("uuid")), String.valueOf(map.get("deviceType")));
                arpIpList.addAll(arpIps);
            }
            if(arpIpList.size() > 0){
                Set set = new HashSet();
                set.addAll(arpIpList);
                arpIpList.clear();
                arpIpList.addAll(set);
                IpDetail init = ipDetailService.selectObjByIp("0.0.0.0");
                init.setTime(init.getTime() + 1);
                ipDetailService.update(init);
                // 总时长
                Map params = new HashMap();
                params.put("ipId", init.getId());
                params.put("arpIpList", arpIpList);
                List<IpDetail> IpDetails = ipDetailService.selectObjByMap(params);
                IpDetails.forEach((item)->{
                    item.setOnline(true);
                    item.setTime(item.getTime() + 1);
                    int initTime = init.getTime() + 1;
                    float num =(float)item.getTime() / initTime;
                    int usage = Math.round(num * 100);
                    item.setUsage(usage);
                    ipDetailService.update(item);
                });

            }
            Map params = new HashMap();
            params.put("notIpList", arpIpList);
            List<IpDetail> ips = ipDetailService.selectObjByMap(params);
            ips.forEach((item)->{
                item.setOnline(false);
                ipDetailService.update(item);
            });

            this.zabbixItemService.arpTag();
            // 录入子网
//            arpIpList.forEach((item)->{
//                if(!IpUtil.verifyIp(IpUtil.decConvertIp(item))){
//
//                }
//            });
            // 获取所有子网一级
//            List<Subnet> subnets = this.zabbixSubnetService.selectSubnetByParentId(null);
//            if(subnets.size() > 0){
//                for(Long decIp : arpIpList){
//                    String ip = IpUtil.decConvertIp(decIp);
//                    if(!IpUtil.verifyIp(ip)){
//                        continue;
//                    }
//                    // 判断ip地址是否属于子网
//                    for(Subnet subnet : subnets){
//                        genericNoSubnet(subnet, ip);
//                    }
//                }
//            }
        }
    }

    public Subnet genericNoSubnet(Subnet subnet, String ip){
        List<Subnet> childs = this.zabbixSubnetService.selectSubnetByParentId(subnet.getId());
        if(childs.size() > 0){
            for(Subnet child : childs){
                genericNoSubnet(child, ip);
            }
        }
        // 判断ip是否属于从属子网
        boolean flag = IpUtil.ipIsInNet(ip, subnet.getIp() + "/" + subnet.getMask());
        if(flag){
            Address obj = this.addressService.selectObjByIp(IpUtil.ipConvertDec(ip));
            if(obj != null){
                obj.setSubnetId(subnet.getId());
                int i = this.addressService.update(obj);
            }else{
                Address address = new Address();
                IpDetail ipDetail = this.ipDetailService.selectObjByIp(IpUtil.ipConvertDec(ip));
                address.setIp(IpUtil.ipConvertDec(ip));
                address.setHostName(ipDetail.getDeviceName());
                address.setMac(ipDetail.getMac());
                int i = this.addressService.save(address);
            }
        }
        return subnet;
    }

    @Override
    public void gatherArpThread() {
        List<Map> ipList = this.topoNodeService.queryMetoo();
        if (ipList != null && ipList.size() > 0) {
            ExecutorService exe = Executors.newFixedThreadPool(ipList.size());
            // truncate
            this.arpService.truncateTable();
            for (Map map : ipList) {
                boolean flag = this.zabbixHostService.verifyHost(String.valueOf(map.get("ip")));
                if (flag) {
                    exe.execute(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getItemArp(map.get("ip").toString(), map.get("deviceName").toString(),
                                    String.valueOf(map.get("uuid")), String.valueOf(map.get("deviceType")));
                        }
                    }));
                }
            }
            exe.shutdown();
            while (true) {
                if (exe.isTerminated()) {
                    this.zabbixItemService.arpTag();
                    break;
                }
            }
        }
    }

    @Override
    public void gatherMac() {
        List<Map> ipList = this.topoNodeService.queryMetoo();
        this.macService.truncateTable();
        if(ipList != null && ipList.size() > 0){
            ExecutorService exe = Executors.newFixedThreadPool(ipList.size());
            for (Map map :ipList){
                if(map.get("ip") != null){
                    boolean flag = this.zabbixHostService.verifyHost(String.valueOf(map.get("ip")));
                    if (flag){
                        if(map.get("ip") != null && !map.get("ip").toString().equals("")
                        /*
                        && map.get("deviceName") != null && !map.get("deviceName").toString().equals("")
                            && map.get("uuid") != null && !map.get("uuid").toString().equals("")*/){
                            // 采集mac信息
                            exe.execute(new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getItemMac(map.get("ip").toString(),
                                                    map.get("deviceName").toString(),
                                                    String.valueOf(map.get("uuid")),
                                                    String.valueOf(map.get("deviceType")));
                                        }
                                    }
                                    )
                            );
                        }
                    }
                }
            }
            exe.shutdown();
            while (true) {
                if (exe.isTerminated()) {
                    this.zabbixItemService.macTag();
                    break;
                }
            }
        }
    }

    @Override
    public void gatherRout() {
        List<Map> ipList = this.topoNodeService.queryMetoo();
        if(ipList != null && ipList.size() > 0) {
            // truncate
            this.routService.truncateTable();
            ExecutorService exe = Executors.newFixedThreadPool(ipList.size());
            for (Map map : ipList) {
                exe.execute(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean flag = zabbixHostService.verifyHost(String.valueOf(map.get("ip")));
                        if (flag) {
                            gatherRout(map.get("ip").toString(), map.get("deviceName").toString(), String.valueOf(map.get("uuid")));
                        }
                    }
                }));
            }
            if(exe != null){
                exe.shutdown();
            }
        }
    }

    @Override
    public void gatherIp() {
        List<Map> ipList = this.topoNodeService.queryMetoo();
        if(ipList != null && ipList.size() > 0) {
            // truncate
            this.ipaddressService.truncateTable();
            ExecutorService exe = Executors.newFixedThreadPool(ipList.size());
            for (Map map : ipList) {
                exe.execute(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean flag = zabbixHostService.verifyHost(String.valueOf(map.get("ip")));
                        if (flag) {
                            System.out.println(map.get("ip").toString());
                            gatherIpaddress(map.get("ip").toString(), map.get("deviceName").toString(), String.valueOf(map.get("uuid")));
                        }
                    }
                }));
            }
            if(exe != null){
                exe.shutdown();
            }
        }
    }

    @Override
    public void gatherMacThread() {
        List<Map> ipList = this.topoNodeService.queryMetoo();
        this.macService.truncateTable();
        if(ipList != null && ipList.size() > 0){
            ExecutorService exe = Executors.newFixedThreadPool(ipList.size());
            for (Map map :ipList){
                if(map.get("ip") != null){
                    boolean flag = this.zabbixHostService.verifyHost(String.valueOf(map.get("ip")));
                    if (flag){
                        if(map.get("ip") != null && !map.get("ip").toString().equals("")
                        /*
                        && map.get("deviceName") != null && !map.get("deviceName").toString().equals("")
                            && map.get("uuid") != null && !map.get("uuid").toString().equals("")*/){
                            // 采集mac信息
                            exe.execute(new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getItemMac(map.get("ip").toString(),
                                                    map.get("deviceName").toString(),
                                                    String.valueOf(map.get("uuid")),
                                                    String.valueOf(map.get("deviceType")));
                                        }
                                    }
                                    )
                            );
                        }
                    }
                }
            }
            exe.shutdown();
            while (true) {
                if (exe.isTerminated()) {
                    this.zabbixItemService.macTag();
                    break;
                }
            }
        }
    }

    @Override
    public void createRoutTable(String ip) {
        try {
//            C:\Users\46075\Desktop\metoo\需求记录\4，策略可视化\监控系统（Zabbix）\routTable.conf
            String path = ResourceUtils.getURL("classpath:").getPath() + "/static/routs/routTable.conf";
            path = "C:\\Users\\46075\\Desktop\\metoo\\需求记录\\4，策略可视化\\监控系统（Zabbix）\\routTable.conf";
            String routPath = "routTable.conf";
            File file = new File(URLDecoder.decode(path, "utf-8"));
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true),"utf-8"), 1024*1000);
            out.write("display ip routing-table");
            out.write("\r\n");
            out.write(itemUtil.strLenComplement("Destination/Mask", 25));
            out.write(itemUtil.strLenComplement("Proto", 10));
            out.write(itemUtil.strLenComplement("Pre", 10));
            out.write(itemUtil.strLenComplement("Cost", 10));
            out.write(itemUtil.strLenComplement("Flags", 10));
            out.write(itemUtil.strLenComplement("NextHop", 30));
            out.write(itemUtil.strLenComplement("interface", 20));
            out.write("\r\n");
            List<Map<String, String>> maps = this.getItemRoutByIp(ip);
            for (Map map : maps){
                out.write(itemUtil.strLenComplement(map.get("destination").toString() + "/" + map.get("destination_mask").toString(), 25));
                out.write(itemUtil.strLenComplement(map.get("proto").toString(), 10));
                out.write(itemUtil.strLenComplement("0", 10));
                out.write(itemUtil.strLenComplement(map.get("cost") == null ? "0" : map.get("cost").toString(), 10));
                out.write(itemUtil.strLenComplement(map.get("flags").toString(), 10));
                out.write(itemUtil.strLenComplement(map.get("nexthop").toString(), 30));
                out.write(itemUtil.strLenComplement(map.get("interface_name") == null ? "0" : map.get("interface_name").toString(), 20));
                out.write("\r\n");
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
