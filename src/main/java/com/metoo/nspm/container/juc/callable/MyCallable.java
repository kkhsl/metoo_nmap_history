package com.metoo.nspm.container.juc.callable;

import com.metoo.nspm.core.mapper.zabbix.ItemMapper;
import com.metoo.nspm.core.utils.network.IpUtil;
import com.metoo.nspm.entity.nspm.ArpTemp;
import com.metoo.nspm.entity.nspm.MacTemp;
import com.metoo.nspm.entity.zabbix.Item;
import com.metoo.nspm.entity.zabbix.ItemTag;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Callable;

/**
 *
 */
public class MyCallable implements Callable<List<MacTemp>> {


//    private List<MacTemp> batchInsert = new ArrayList<>();

    private Map map;

    private Date time;

    private ItemMapper itemMapper;

    public MyCallable() {
    }

//    public MyCallable(List<MacTemp> batchInsert) {
//        this.batchInsert = batchInsert;
//    }

    public MyCallable(Date time, Map map, ItemMapper itemMapper) {
        this.time = time;
        this.map = map;
        this.itemMapper = itemMapper;
    }

    @Override
    public List call() {
        List<MacTemp> list = new Vector<>();
        System.out.println(Thread.currentThread().getName());
        String deviceName = String.valueOf(map.get("deviceName"));
        String deviceType = String.valueOf(map.get("deviceType"));
        String ip = String.valueOf(map.get("ip"));
        String uuid = String.valueOf(map.get("uuid"));
        String type = String.valueOf(map.get("type"));
        Map params = new HashMap();
        params.clear();
        params.put("ip", ip);
        params.put("tag", "macvlan");
        List<Item> vlanMacList = itemMapper.gatherItemByTagAndRtdata(params);
        Map<String, String> macMap = null;
        if(vlanMacList.size() > 0){
            macMap = macVlan(vlanMacList);
        }
        // 采集：ifbasic
        params.clear();
        params.put("ip", ip);
        params.put("tag", "ifbasic");
        params.put("tag_relevance", "ifbasic");
        params.put("index", "ifindex");
        params.put("index_relevance", "ifindex");
        params.put("name_relevance", "ifname");
        List<Item> items = itemMapper.gatherItemByTagAndRtdata(params);
        if (items.size() > 0) {
            final  Map<String, String> macVlan = macMap;
            items.stream().forEach(item -> {
                List<ItemTag> tags = item.getItemTags();
                MacTemp macTemp = new MacTemp();
                macTemp.setAddTime(time);
                macTemp.setDeviceName(deviceName);
                macTemp.setDeviceType(deviceType);
                macTemp.setUuid(uuid);
                macTemp.setDeviceIp(ip);
                macTemp.setTag("L");
                if (tags != null && tags.size() > 0) {
                    for (ItemTag tag : tags) {
                        String value = tag.getValue();
                        if (tag.getTag().equals("ifmac")) {
                            if(!value.contains(":")){
                                value = value.trim().replaceAll(" ", ":");
                            }
                            String mac = supplement(value);
                            macTemp.setMac(mac);
                            if(macVlan != null && !macVlan.isEmpty()){
                                String vlan = macVlan.get(value);
                                macTemp.setVlan(vlan);
                            }
                            if(StringUtils.isNotEmpty(value)){
                                macTemp.setType("local");
                            }
                        }
                        if (tag.getTag().equals("ifname")) {
                            macTemp.setInterfaceName(value);
                        }
                        if (tag.getTag().equals("ifindex")) {
                            macTemp.setIndex(value);
                        }
                    }
                    // 保存Mac条目
                    if (StringUtils.isNotEmpty(macTemp.getInterfaceName())
                            && StringUtils.isNotEmpty(macTemp.getMac())
                            && !macTemp.getMac().equals("{#MAC}")
                            && !macTemp.getMac().equals("{#IFMAC}")) {
                        macTemp.setTag("L");
                        macTemp.setAddTime(time);
                        list.add(macTemp);
                    }
                }
            });
        }

        // 采集Mac(Zabbix)(obj：mac)
        List<Item> itemTagList = new ArrayList<>();
        if(type.equals("H3C")){
            params.clear();
            params.put("ip", ip);
            params.put("tag", "h3cvlanmac");
            params.put("index", "portindex");
            params.put("tag_relevance", "ifbasic");
            params.put("index_relevance", "ifindex");
            params.put("name_relevance", "ifname");
            itemTagList = itemMapper.gatherItemByTagAndRtdata(params);
            if(itemTagList.size() > 0){
                final  Map<String, String> macVlan = macMap;
                itemTagList.stream().forEach(item -> {
                    List<ItemTag> tags = item.getItemTags();
                    MacTemp macTemp = new MacTemp();
                    macTemp.setAddTime(time);
                    macTemp.setDeviceName(deviceName);
                    macTemp.setDeviceType(deviceType);
                    macTemp.setUuid(uuid);
                    macTemp.setDeviceIp(ip);
                    if (tags != null && tags.size() > 0) {
                        for (ItemTag tag : tags) {
                            String value = tag.getValue();
                            if (tag.getTag().equals("vlanmac")) {
                                Map<String, String> map = this.macVlan(value);
                                if(map != null){
                                    for (Map.Entry<String, String> entry : map.entrySet()) {
                                        macTemp.setMac(entry.getKey());
                                        macTemp.setVlan(entry.getValue());
                                        break;
                                    }
                                }
                            }
                            if (tag.getTag().equals("portindex")) {
                                macTemp.setInterfaceName(tag.getName());
                                macTemp.setIndex(value);
                            }
                            if (tag.getTag().equals("attr")) {
                                switch (value){
                                    case "5":
                                        value = "static";
                                        break;
                                    case "4":
                                        value = "local";
                                        break;
                                    case "3":
                                        value = "dynamic";
                                        break;
                                    case "2":
                                        value = "invalid";
                                        break;
                                    case "1":
                                        value = "other";
                                        break;
                                    default:
                                        value = null;
                                        break;
                                }
                                macTemp.setType(value);
                            }
                        }
                        // 保存Mac条目
                        if(StringUtils.isNotEmpty(macTemp.getInterfaceName())
                                && StringUtils.isNotEmpty(macTemp.getMac())
                                && !macTemp.getMac().equals("{#MAC}")
                                && !macTemp.getMac().equals("{#IFMAC}")){
                            if(macTemp.getTag() == null || "".equals(macTemp.getTag())){
                                if(macTemp.getType() != null && "local".equals(macTemp.getType())
                                        && macTemp.getMac().contains("00:00:5e")){
                                    macTemp.setTag("LV");
                                }else if(macTemp.getMac().contains("00:00:5e")){
                                    macTemp.setTag("V");
                                }
                            }
                            list.add(macTemp);
                        }
                    }
                });
            }
        }else{
            params.clear();
            params.put("ip", ip);
            params.put("tag", "mac");
            params.put("index", "portindex");
            params.put("tag_relevance", "ifbasic");
            params.put("index_relevance", "ifindex");
            params.put("name_relevance", "ifname");
            itemTagList = itemMapper.gatherItemByTagAndRtdata(params);
            if(itemTagList.size() > 0){
                final  Map<String, String> macVlan = macMap;
                itemTagList.stream().forEach(item -> {
                    List<ItemTag> tags = item.getItemTags();
                    MacTemp macTemp = new MacTemp();
                    macTemp.setAddTime(time);
                    macTemp.setDeviceName(deviceName);
                    macTemp.setDeviceType(deviceType);
                    macTemp.setUuid(uuid);
                    macTemp.setDeviceIp(ip);
                    if (tags != null && tags.size() > 0) {
                        for (ItemTag tag : tags) {
                            String value = tag.getValue();
                            if (tag.getTag().equals("mac")) {
                                // 格式化Mac
                                if(!value.contains(":")){
                                    value = value.trim().replaceAll(" ", ":");
                                }
                                String mac = supplement(value);
                                macTemp.setMac(mac);
                                if(macVlan != null && !macVlan.isEmpty()){
                                    String vlan = macVlan.get(value);
                                    macTemp.setVlan(vlan);
                                }
                            }
                            if (tag.getTag().equals("portindex")) {
                                macTemp.setInterfaceName(tag.getName());
                                macTemp.setIndex(value);
                            }
                            if (tag.getTag().equals("attr")) {
                                switch (value){
                                    case "5":
                                        value = "static";
                                        break;
                                    case "4":
                                        value = "local";
                                        break;
                                    case "3":
                                        value = "dynamic";
                                        break;
                                    case "2":
                                        value = "invalid";
                                        break;
                                    case "1":
                                        value = "other";
                                        break;
                                    default:
                                        value = null;
                                        break;
                                }
                                macTemp.setType(value);
                            }
                        }
                        // 保存Mac条目
                        if(StringUtils.isNotEmpty(macTemp.getInterfaceName())
                                && StringUtils.isNotEmpty(macTemp.getMac())
                                && !macTemp.getMac().equals("{#MAC}")
                                && !macTemp.getMac().equals("{#IFMAC}")){
                            if(macTemp.getTag() == null || "".equals(macTemp.getTag())){
                                if(macTemp.getType() != null && "local".equals(macTemp.getType())
                                        && macTemp.getMac().contains("00:00:5e")){
                                    macTemp.setTag("LV");
                                }else if(macTemp.getMac().contains("00:00:5e")){
                                    macTemp.setTag("V");
                                }
                            }
                            list.add(macTemp);
                        }
                    }
                });
            }
        }

        // Begin(item)
        if (itemTagList.size() <= 0) {
            params.put("ip", ip);
            params.put("tag", "arp");
            params.put("index", "ifindex");
            params.put("tag_relevance", "ifbasic");
            params.put("index_relevance", "ifindex");
            params.put("name_relevance", "ifname");
            List<Item> arpList = itemMapper.gatherItemByTagAndRtdata(params);
            if (arpList.size() > 0) {
                arpList.stream().forEach(item -> {
                    List<ItemTag> tags = item.getItemTags();
                    ArpTemp arpTemp = new ArpTemp();
                    arpTemp.setDeviceName(deviceName);
                    arpTemp.setDeviceType(deviceType);
                    arpTemp.setUuid(uuid);
                    arpTemp.setDeviceIp(ip);
                    arpTemp.setTag("S");
                    if (tags != null && tags.size() > 0) {
                        for (ItemTag tag : tags) {
                            String value = tag.getValue();
                            if (tag.getTag().equals("ip")) {
                                arpTemp.setIp(IpUtil.ipConvertDec(value));
                            }
                            if (tag.getTag().equals("mac")) {
                                String mac = this.supplement(value);
                                arpTemp.setMac(mac);
                            }
                            if (tag.getTag().equals("type")) {
                                switch (value){
                                    case "4":
                                        value = "static";
                                        break;
                                    case "3":
                                        value = "dynamic";
                                        break;
                                    case "1":
                                        value = "other";
                                        break;
                                    default:
                                        value = null;
                                        break;
                                }
                                arpTemp.setType(value);
                            }
                            if (tag.getTag().equals("ifindex")) {
                                if(value != null){
                                    arpTemp.setInterfaceName(tag.getName());
                                    arpTemp.setIndex(value);
                                }else{
                                    //
                                    switch (value){
                                        case "1":
                                    }
                                }
                            }
                        }
                        // 获取arp，写入mac
                        if (StringUtils.isNotEmpty(arpTemp.getIp())
                                && StringUtils.isNotEmpty(arpTemp.getInterfaceName())
                                && StringUtils.isNotEmpty(arpTemp.getMac())
                                && "dynamic".equals(arpTemp.getType())) {
                            MacTemp macTemp = new MacTemp();
                            macTemp.setAddTime(time);
                            macTemp.setMac(arpTemp.getMac());
                            macTemp.setIndex(arpTemp.getIndex());
                            macTemp.setType(arpTemp.getType());
                            macTemp.setDeviceName(deviceName);
                            macTemp.setDeviceType(deviceType);
                            macTemp.setInterfaceName(arpTemp.getInterfaceName());
                            macTemp.setUuid(uuid);
                            macTemp.setDeviceIp(ip);
                            list.add(macTemp);
                        }
                    }
                });
            }
        }
        return list;
    }

    public Map<String, String> macVlan( List<Item> vlanMacList){
        Map map = new HashMap();
        if(vlanMacList.size() > 0) {
            for (Item item : vlanMacList) {
                List<ItemTag> tags = item.getItemTags();
                if (tags != null && tags.size() > 0) {
                    for (ItemTag tag : tags) {
                        String vlanMac = tag.getValue();
                        if (tag.getTag().equals("vlan")) {
//                            String vlanMac = "0.80.121.102.104.29";
                            int i = vlanMac.indexOf(".");
                            if(i != -1){
                                String vlan = vlanMac.substring(0, i);
                                String mac16 = vlanMac.substring(i + 1);
                                String mac = mac16ConvertMac10(mac16);
                                map.put(mac, vlan);
                            }
                        }
                    }
                }
            }
        }
        return map;
    }

    public Map<String, String> macVlan(String vlanmac){
        Map map = new HashMap();
        if(Strings.isNotBlank(vlanmac)) {
            int i = vlanmac.indexOf(".");
            if(i != -1){
                String vlan = vlanmac.substring(0, i);
                String mac16 = vlanmac.substring(i + 1);
                String mac = mac16ConvertMac10(mac16);
                map.put(mac, vlan);
                return map;
            }
        }
        return null;
    }


    public static String supplement(String macAddr){
        int one_index = macAddr.indexOf(":");
        if(one_index != -1){
            String[] strs = macAddr.split(":");
            StringBuffer stringBuffer = new StringBuffer();
            int i = 1;
            for(String str : strs){
                if(str.length() == 1){
                    stringBuffer.append(0).append(str);
                }else{
                    stringBuffer.append(str);
                }
                if(i < strs.length){
                    stringBuffer.append(":");
                }
                i++;
            }
            macAddr = stringBuffer.toString();
        }
        return macAddr;
    }


    public String mac16ConvertMac10(String param){
        String[] strs = param.split("\\.");
        StringBuffer mac = new StringBuffer();
        for (int i = 0; i < strs.length; i++) {
            String str = strs[i];
            String hex = this.toHex(Integer.parseInt(str));
            if(i+1 <strs.length){
                mac.append(hex).append(":");
            }else if(i+1 == strs.length){
                mac.append(hex);
            }
        }
        return mac.toString();
    }

    public String toHex(Integer str){
        String hex = Integer.toHexString(str);
        return hex;
    }
}


