package com.metoo.nspm.core.service.zabbix.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.metoo.nspm.core.manager.zabbix.utils.ItemUtil;
import com.metoo.nspm.core.manager.zabbix.utils.ZabbixApiUtil;
import com.metoo.nspm.core.service.zabbix.IArpService;
import com.metoo.nspm.core.service.zabbix.IMacService;
import com.metoo.nspm.core.service.zabbix.ZabbixHostService;
import com.metoo.nspm.core.service.zabbix.ZabbixItemService;
import com.metoo.nspm.core.utils.network.IpUtil;
import com.metoo.nspm.core.utils.network.IpV4Util;
import com.metoo.nspm.dto.zabbix.HostDTO;
import com.metoo.nspm.dto.zabbix.ItemDTO;
import com.metoo.nspm.entity.zabbix.Arp;
import com.metoo.nspm.entity.zabbix.Mac;
import io.github.hengyunabc.zabbix.api.Request;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ZabbixItemServiceImpl implements ZabbixItemService {

    @Autowired
    private ZabbixApiUtil zabbixApiUtil;
    @Autowired
    private IpV4Util ipV4Util;
    @Autowired
    private ZabbixHostService zabbixHostService;
    @Autowired
    private IArpService arpService;
    @Autowired
    private IMacService macService;

    @Override
    public JSONObject getItem(ItemDTO dto) {
        Request request = this.zabbixApiUtil.parseParam(dto, "item.get");
        return zabbixApiUtil.call(request);
    }

    public JSONArray getItemAndTagByHostId(String ip, List tags, List output){
        String hostid = this.zabbixHostService.getHostId(ip);
        if(hostid != null){
            ItemDTO itemDto = new ItemDTO();
            itemDto.setHostids(Arrays.asList(hostid));
            Map filterMap = new HashMap();
            itemDto.setFilter(filterMap);
            itemDto.setMonitored(true);
            if(tags != null){
                itemDto.setTags(tags);
            }
            if(output != null){
                itemDto.setOutput(output);
            }
            itemDto.setSelectTags("extend");
            JSONObject items = this.getItem(itemDto);
            if(items.get("result") != null) {
                JSONArray itemArray = JSONArray.parseArray(items.getString("result"));
                return itemArray;
            }
        }
        return null;
    }

    public JSONArray getItemAndTagByIp(String ip, List tags, List output){
        ItemDTO itemDto = new ItemDTO();
        Map filterMap = new HashMap();
        filterMap.put("ip",ip);
        itemDto.setFilter(filterMap);
        itemDto.setMonitored(true);
        if(tags != null){
            itemDto.setTags(tags);
        }
        if(output != null){
            itemDto.setOutput(output);
        }
        itemDto.setSelectTags("extend");
        JSONObject items = this.getItem(itemDto);
        if(items.get("result") != null) {
            JSONArray itemArray = JSONArray.parseArray(items.getString("result"));
            return itemArray;
        }
        return null;
    }

    @Override
    public JSONArray getItemByIpAndTag(String ip, List tags, List output){
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
                        itemDto.setTags(tags);
                        itemDto.setOutput(output);
                        itemDto.setSelectTags("extend");
                        Object itemObejct = this.getItem(itemDto);
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

    @Override
    public JSONArray getItemIpAddress(String ip) {
        if(ip != null){
            List tags = new ArrayList();
            tags.add(ItemUtil.packaging("obj", "ifipaddr", 1));
            tags.add(ItemUtil.packaging("ipaddr", "127.0.0.1", 3));
            return this.getItemAndTagByHostId(ip, tags, null);
        }
        return null;
    }

    @Override
    public JSONArray getItemIpAddressTag(String ip) {
        if(ip != null){
            List tags = new ArrayList();
            tags.add(ItemUtil.packaging("obj", "ifipaddr", 1));
            tags.add(ItemUtil.packaging("ipaddr", "127.0.0.1", 3));
            List output = new ArrayList();
            output.add("tag");
            output.add("value");
            return this.getItemAndTagByHostId(ip, tags, output);
        }
        return null;
    }

    @Override
    public JSONArray getItemIpAddressTagByIndex(String ip, Integer index) {
        if(ip != null){
            List tags = new ArrayList();
            tags.add(ItemUtil.packaging("obj", "ifipaddr", 1));
            tags.add(ItemUtil.packaging("ipaddr", "127.0.0.1", 3));
            if(index != null){
                tags.add(ItemUtil.packaging("ifindex", index, 1));
            }
            List output = new ArrayList();
            output.add("tag");
            output.add("value");
            return this.getItemAndTagByHostId(ip, tags, output);
        }
        return null;
    }


    @Override
    public JSONArray getItemOperationalTagByIndex(String ip, Integer index) {
        if(ip != null){
            List tags = new ArrayList();
            tags.add(ItemUtil.packaging("obj", "ifoperstatus", 1));
            if(index != null){
                tags.add(ItemUtil.packaging("ifindex", index, 1));
            }
            List output = new ArrayList();
            output.add("tag");
            output.add("value");
            return this.getItemAndTagByHostId(ip, tags, output);
        }
        return null;
    }

    @Override
    public JSONArray getItemTags(String ip) {
        if(ip != null){
            List tags = new ArrayList();
            tags.add(ItemUtil.packaging("obj", "ifbasic", 1));
            tags.add(ItemUtil.packaging("obj", "ifsent", 1));
            tags.add(ItemUtil.packaging("obj", "ifspeed", 1));
            tags.add(ItemUtil.packaging("obj", "ifreceived", 1));

//            List output = new ArrayList();
//            output.add("tag");
//            output.add("value");
            return this.getItemAndTagByHostId(ip, tags, null);
        }
        return null;
    }

    @Override
    public JSONArray getItemMac(String ip) {
        if(ip != null){
            List tags = new ArrayList();
            tags.add(ItemUtil.packaging("obj", "mac", 1));
            return this.getItemAndTagByHostId(ip, tags, null);
        }
        return null;
    }


    @Override
    public JSONArray getItemMacTag(String ip) {
        if(ip != null){
            List tags = new ArrayList();
            tags.add(ItemUtil.packaging("obj", "mac", 1));
            List output = new ArrayList();
            output.add("tag");
            output.add("value");
            return this.getItemAndTagByHostId(ip, tags, output);
        }
        return null;
    }

    @Override
    public JSONArray getItemArpTag(String ip) {
        if(ip != null){
            List tags = new ArrayList();
            tags.add(ItemUtil.packaging("obj", "arp", 1));
//            List output = new ArrayList();
//            output.add("tag");
//            output.add("value");
            return this.getItemAndTagByHostId(ip, tags, null);
        }
        return null;
    }

    @Override
    public JSONArray getItemInterfacesByIndex(String ip, Integer index) {
        if(ip != null){
            List tags = new ArrayList();
            tags.add(ItemUtil.packaging("obj", "ifbasic", 1));
            if(index != null){
                tags.add(ItemUtil.packaging("ifindex", index, 1));
            }
            return this.getItemAndTagByHostId(ip, tags, null);
        }
        return null;
    }

    @Override
    public JSONArray getItemInterfacesTagByIndex(String ip, String index) {
        if(ip != null){
            List tags = new ArrayList();
            tags.add(ItemUtil.packaging("obj", "ifbasic", 1));
            if(index != null){
                tags.add(ItemUtil.packaging("ifindex", index, 1));
            }
            List output = new ArrayList();
            output.add("tag");
            output.add("value");
            return this.getItemAndTagByHostId(ip, tags, output);
        }
        return null;
    }

    @Override
    public JSONArray getItemIfIndexByIndexTag(String ip) {
        if(ip != null){
            List tags = new ArrayList();
            tags.add(ItemUtil.packaging("obj", "ifindex", 1));
            List output = new ArrayList();
            output.add("tag");
            output.add("value");
            return this.getItemAndTagByHostId(ip, tags, output);
        }
        return null;
    }

    @Override
    public JSONArray getItemInterfacesTag(String ip) {
        if(ip != null){
            List tags = new ArrayList();
            tags.add(ItemUtil.packaging("obj", "ifbasic", 1));
            List output = new ArrayList();
            output.add("tag");
            output.add("value");
            return this.getItemAndTagByHostId(ip, tags, output);
        }
        return null;
    }

    @Override
    public JSONArray getItemInterfaces(String ip) {
        if(ip != null){
            List tags = new ArrayList();
            tags.add(ItemUtil.packaging("obj", "ifbasic", 1));
            return this.getItemAndTagByHostId(ip, tags, null);
        }
        return null;
    }

    @Override
    public JSONArray getItemRout(String ip) {
        if(ip != null){
            List tags = new ArrayList();
            tags.add(ItemUtil.packaging("obj", "route", 1));
            return this.getItemAndTagByHostId(ip, tags, null);
        }
        return null;
    }

    @Override
    public JSONArray getItemRoutTag(String ip) {
        if(ip != null){
            List tags = new ArrayList();
            tags.add(ItemUtil.packaging("obj", "route", 1));
            List output = new ArrayList();
            output.add("tag");
            output.add("value");
            return this.getItemAndTagByHostId(ip, tags, output);
        }
        return null;
    }


    @Override
    public Map<String, List<Object>> ipAddressCombing(JSONArray items) {
        if(items.size() == 0){
            return null;
        }

        Map<String, Integer> map = new HashMap();
        List<Integer> masks = new ArrayList();
        for (Object array : items){
            JSONObject item = (JSONObject) JSON.toJSON(array);
            JSONArray tags = JSONArray.parseArray(item.getString("tags"));
            if(tags != null && tags.size() > 0){
                String ip = null;
                String mask = item.getString("mask");
                for (Object t : tags){
                    JSONObject tag = JSONObject.parseObject(t.toString());
                    if(tag.getString("tag").equals("ipaddr")){
                        ip = tag.getString("value");
                    }
                    if(tag.getString("tag").equals("mask")){
                        mask = tag.getString("value");
                    }
                }
                int bitMask = ipV4Util.getMaskBitByMask(mask);// 获取掩码位
                Map networkMap = IpUtil.getNetworkIp(ip, mask);
                map.put(networkMap.get("network").toString(), bitMask);
                masks.add(bitMask);
            }
        }
        // 第二步：提取最短掩码，生成上级网段
        HashSet set = new HashSet(masks);
        masks.clear();
        masks.addAll(set);
        Collections.sort(masks);
        Integer firstMask = masks.get(0);// 最短掩码
        Map<String, Integer> firstMap = new HashMap();
        Map<String, Integer> otherMap = new HashMap();
        for (Map.Entry<String, Integer> entry : map.entrySet()){
            if(entry.getValue() == firstMask){
                firstMap.put(entry.getKey(), entry.getValue());
            }else{
                otherMap.put(entry.getKey(), entry.getValue());
            }
        }
        // 提取
        Map<String, List<Object>> parentMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : firstMap.entrySet()) {
            Integer mask = entry.getValue();
            String ip = entry.getKey();
            Integer parentMask = null;
            if (mask > 24) {
                parentMask = 24;
            } else if (24 >= mask && mask > 16) {
                parentMask = 16;
            } else if (16 >= mask && mask > 8) {
                parentMask = 8;
            }
            String parentIp = this.getParentIp(ip, parentMask);
            parentIp = parentIp + "/" + parentMask;
            if (parentMap.get(parentIp) == null) {
                List<Object> list = new ArrayList<>();
                list.add(ip + "/" + mask);
                parentMap.put(parentIp, list);
            } else {
                List<Object> list = parentMap.get(parentIp);
                list.add(ip + "/" + mask);
            }
        }
//        遍历
        Map parentSegment = new HashMap();
        for (Map.Entry entry1 : parentMap.entrySet()) {
            String parentIpMask = (String) entry1.getKey();
            String parentIp = null;
            Integer parentMask = null;
            int sequence = parentIpMask.indexOf("/");
            parentIp = parentIpMask.substring(0, sequence);
            parentMask = Integer.parseInt(parentIpMask.substring(sequence + 1));
            int parentIndex = 0;
            String parentIpSeggment = null;
            if (parentMask == 24) {
                parentIndex =  parentIpMask.indexOf(".");
                parentIndex =  parentIpMask.indexOf(".", parentIndex + 1);
                parentIndex =  parentIpMask.indexOf(".", parentIndex + 1);
            } else if (parentMask == 16) {
                parentIndex =  parentIpMask.indexOf(".");
                parentIndex =  parentIpMask.indexOf(".", parentIndex + 1);
            } else if (parentMask == 8) {
                parentIndex =  parentIpMask.indexOf(".");
            }
            parentIpSeggment = parentIpMask.substring(0, parentIndex);
            parentSegment.put(parentIpSeggment, parentIp);
        }

        // 判断是否属于第一级
        for (Map.Entry<String, Integer> entry : otherMap.entrySet()) {
            Integer mask = entry.getValue();
            String ip = entry.getKey();
            String ip_segment = null;
            int index = 0;
            if (mask > 24) {
                index =  ip.indexOf(".");
                index =  ip.indexOf(".", index + 1);
                index =  ip.indexOf(".", index + 1);
            } else if (24 >= mask && mask > 16) {
                index =  ip.indexOf(".");
                index =  ip.indexOf(".", index + 1);
            } else if (16 >= mask && mask > 8) {
                index =  ip.indexOf(".");
            }
            ip_segment = ip.substring(0, index);
            if(parentSegment.get(ip_segment) != null){
                List<Object> list = parentMap.get(parentSegment.get(ip_segment));
                list.add(ip + "/" + mask);
            }else{
                Integer parentMask = null;
                if (mask > 24) {
                    parentMask = 24;
                } else if (24 >= mask && mask > 16) {
                    parentMask = 16;
                } else if (16 >= mask && mask > 8) {
                    parentMask = 8;
                }
                String parentIp = this.getParentIp(ip, parentMask);
                parentIp = parentIp + "/" + parentMask;
                List<Object> list = new ArrayList<>();
                list.add(ip + "/" + mask);
                parentMap.put(parentIp, list);
                parentSegment.put(ip_segment, parentIp);
            }
        }
        // 遍历二级ip，生成上级Ip
        if(parentMap.size() > 1){
            Map<String, List<Object>> parent = this.getShorMask(parentMap);
            if(parent != null && parent.size() > 0){
                return parent;
            }
        }else{

        }
        return parentMap;
    }

    @Override
    public void arpTag() {
        // 检测 0:0:5e:0
        Map params = new HashMap();
        params.put("like", 0);
        List<Arp> arps = this.arpService.selectObjByMap(params);
        for (Arp arp : arps) {
            arp.setTag("V");
            this.arpService.update(arp);
        }
        // 标记LS
        params.clear();
        params.put("tag", "L");
        params.put("count", 2);
        List<Arp> ls = this.arpService.arpTag(params);
        ls.forEach(item -> {
            item.setTag("LS");
            this.arpService.update(item);
        });

        // 对端设备查询(开启三个线程？检索仅有两台mac的，检索拥有多个mac的,检索对端设备)
        // 1-0：arp条目默认设置为S(除本接口arp条目外)
        // 1-1: 端口仅有1条arp条目（除本接口条目外），此条目标记为U（Unsure）
        // 单台设备（标记'U'）
        List<Arp> tagArp = this.arpService.selectObjByDistinct();
        for (Arp arp : tagArp) {
            params.clear();
            params.put("count", 1);
            params.put("interfaceName", arp.getInterfaceName());
            params.put("deviceName", arp.getDeviceName());
            List<Arp> arpList = this.arpService.selectObjByInterface(params);
            for (Arp obj : arpList) {
                if (!obj.getTag().equals("L")) {
                    obj.setTag("U");
                    this.arpService.update(obj);
                }
            }
        }
        // 单台设备（标记'US'）
        params.clear();
        params.put("tag1", "L");
        params.put("tag2", "LS");
        params.put("count", 2);
        List<Arp> arpList = this.arpService.arpTag(params);
        arpList.forEach(item -> {
            item.setTag("US");
            this.arpService.update(item);
        });
        // 多台设备 标记‘E’在全网中有U和L两个标记，将U改为E(Equipment) 不加LS
        params.clear();
        params.put("tagL", "L");
        params.put("tagU", "U");
        params.put("count", 1);
        List<Arp> ulAapList = this.arpService.selectOppositeByMap(params);
        for (Arp arp : ulAapList) {
            arp.setTag("E");
            this.arpService.update(arp);
        }
        // 多台设备 标记‘EM’ 全网存在US和LS两个标记，US改EM
        params.clear();
        params.put("tag1", "US");
        params.put("tag2", "LS");
        params.put("count", 1);
        List<Arp> emArps = this.arpService.selectSubquery(params);
        for (Arp arp : emArps) {
            arp.setTag("EM");
            params.clear();
            params.put("tag", "LS");
            params.put("mac", arp.getMac());
            List<Arp> remotes = this.arpService.selectObjByMap(params);
            if (remotes.size() > 0) {
                Arp remote = remotes.get(0);
                arp.setRemoteDevice(remote.getDeviceName());
                arp.setRemoteInterface(remote.getInterfaceName());
                arp.setRemoteUuid(remote.getUuid());
            }
            this.arpService.update(arp);
        }

//            Map slMap = new HashMap(); // 1-3 同一arp条目，在全网有“S”和“L”两个标记，“S”改为“ES”
//            slMap.put("tagL", "L");
//            slMap.put("tagS", "S");
//            slMap.put("count", 2);
//            List<Arp> slArpList = this.arpService.selectES(slMap);
//            for(Arp sl : slArpList){
//                sl.setTag("ES");
//                this.arpService.update(sl);
//            }

        params.clear(); // 全网“L”
        params.put("tag", "L");
        params.put("tagLS", "LS");
        List<Arp> larps = this.arpService.selectObjByGroupMap(params);
        // 获取全网主机ip
        Map<Map<String, String>, List> hostIpAddresses = new HashMap();
        for (Arp arp : larps) {
            Map key = new HashMap();
            key.put("ip", arp.getIp());
            key.put("deviceName", arp.getDeviceName());
            key.put("interfaceName", arp.getInterfaceName());
            key.put("uuid", arp.getUuid());
            hostIpAddresses.put(key, null/*this.ipV4Util.getHost(arp.getIp(), arp.getMask())*/);
        }

        params.clear(); // 1-3 同一arp条目，在全网有“S”和“L”两个标记，“S”改为“ES”
        params.put("tag", "S");
        List<Arp> arpS = this.arpService.selectObjByGroupMap(params);
        for (Arp arp : arpS) {
            for (Map.Entry<Map<String, String>, List> entry : hostIpAddresses.entrySet()) {
                Map<String, String> key = entry.getKey();
                if (key.get("ip").equals(arp.getIp())) {
//                        if (entry.getValue() != null && entry.getValue().contains(arp.getIp())) {
                    arp.setTag("ES");
                    arp.setRemoteDevice(key.get("deviceName"));
                    arp.setRemoteInterface(key.get("interfaceName"));
                    arp.setRemoteUuid(key.get("uuid"));
                    this.arpService.update(arp);
//                        }
                }
            }
        }

        // 标记T 同一arp条目在全网只有S标记(没有L)，只在一台设备有S标记则将S改为T
        params.clear();
        params.put("count", 1);
        params.put("tag", "S");
        List<Arp> sArpList = this.arpService.selectGroupByHavingMac(params);
        for (Arp s : sArpList) {
            s.setTag("T");
            this.arpService.update(s);
        }

//            params.clear();
//            params.put("count", 1);
//            List<Arp> sArpList = this.arpService.selectGroupByHavingMac(params);
//            for(Arp s : sArpList){
//                Map distinct = new HashMap();// 查询改mac地址是否存在"L"
//                distinct.put("mac", s.getMac());
//                distinct.put("tag", "L");
//                List<Arp> arpMac = this.arpService.selectObjByMap(distinct);
//                if(arpMac.size() == 0){
//                    s.setTag("T");
//                    this.arpService.update(s);
//                }
//
//            }
        // 同一arp条目在全网只有S标记,在多台设备有S标记则将S改为TS
        params.clear();
        params.put("count", 2);
        List<Arp> tsAapList = this.arpService.selectObjByMac(params);
        for (Arp ts : tsAapList) {
            Map distinct = new HashMap();// 查询改mac地址是否存在"L"
            distinct.put("mac", ts.getMac());
            distinct.put("tag", "L");
            List<Arp> arpMac = this.arpService.selectObjByMap(distinct);
            if (arpMac.size() == 0) {
                ts.setTag("TS");
                this.arpService.update(ts);
            }
        }
    }

    @Override
    public void macTag() {
        // 单台设备 标记’U|S‘
        Map params = new HashMap();
        params.put("u", 1);
        List<Mac> umac = this.macService.getMacUS(params);
        umac.forEach(item ->{
            item.setTag("U");
            this.macService.update(item);
        });
        params.clear();
        params.put("s", 2);
        List<Mac> smac = this.macService.getMacUS(params);
        smac.forEach(item ->{
            item.setTag("S");
            this.macService.update(item);
        });

        // 标记E|UE|UT(优化)
        params.clear();
        params.put("tag", "U");
        List<Mac> emac = this.macService.selectByMap(params);
        for (Mac obj :  emac){
            params.clear();
            params.put("tag", "L");
            params.put("mac", obj.getMac());
            params.put("ip", obj.getIp());
            params.put("unDeviceName", obj.getDeviceName());
            List<Mac> macs = this.macService.selectByMap(params);
            if(macs.size() > 0){
                Mac instancce = macs.get(0);
                obj.setTag("E");
                obj.setRemoteDevice(instancce.getDeviceName());
                obj.setRemoteUuid(instancce.getUuid());
                obj.setRemoteInterface(instancce.getInterfaceName());
                this.macService.update(obj);
//                if(macs.size() == 1){
//                    obj.setTag("E");
//                    obj.setRemoteDevice(instancce.getDeviceName());
//                    obj.setRemotePort(instancce.getRemotePort());
//                    obj.setRemoteUuid(instancce.getUuid());
//                    this.macService.update(obj);
//                }else{
//                    obj.setTag("UE");
//                    obj.setRemoteDevice(instancce.getDeviceName());
//                    obj.setRemotePort("UnSure");
//                    obj.setRemoteUuid(instancce.getUuid());
//                    this.macService.update(obj);
//                }
                continue;
            }
            params.clear();
            params.put("other", "L");
            params.put("mac", obj.getMac());
            List<Mac> macList = this.macService.selectByMap(params);
            if(macList.size() > 0){
                obj.setTag("UT");
                this.macService.update(obj);
            }
        }
        // 标记E|RT|UE
        params.clear();
        params.put("tag", "S");
        List<Mac> a = this.macService.selectByMap(params);
        for (Mac obj :  a){
            // 查询arp
            params.clear();
            params.put("tag", "L");
            params.put("mac", obj.getMac());
            params.put("unDeviceName", obj.getDeviceName());
            List<Mac> macs = this.macService.selectByMap(params);
            if(macs.size() > 0){
                Mac instancce = macs.get(0);
                obj.setTag("E");
                obj.setRemoteDevice(instancce.getDeviceName());
                obj.setRemoteUuid(instancce.getUuid());
                obj.setRemoteInterface(instancce.getInterfaceName());
                this.macService.update(obj);
//                if(macs.size() == 1){
//                    obj.setTag("E");
//                    obj.setRemoteDevice(instancce.getDeviceName());
//                    obj.setRemotePort(instancce.getRemotePort());
//                    obj.setRemoteUuid(instancce.getUuid());
//                    this.macService.update(obj);
//                }else{
//                    obj.setTag("E");
//                    obj.setRemoteDevice(instancce.getDeviceName());
//                    obj.setRemotePort("UE");
//                    obj.setRemoteUuid(instancce.getUuid());
//                    this.macService.update(obj);
//                }
                continue;
            }
//            params.clear();
//            params.put("other", "L");
//            params.put("mac", obj.getMac());
//            List<Mac> macList = this.macService.selectByMap(params);
//            if(macList.size() > 0){
//                obj.setTag("UT");
//                this.macService.update(obj);
//            }
        }
        // 查询剩余S条目
        params.clear();
        params.put("tag", "S");
        List<Mac> residueS = this.macService.selectByMap(params);
        for (Mac obj :  residueS){
            obj.setTag("RT");
            this.macService.update(obj);
        }
        // 为DE的条目，查询mac对应的L条目的portindex < 4069标记为DE,记录端口名

        params.clear();
//        params.put("tag", "E");
//        params.put("hvparam", "remote_device");
//        params.put("byParam", "device_name");
//        params.put("count", 2);
        List<Mac> emacs = this.macService.groupByObjByMap(params);
        for(Mac eobj : emacs){
            params.clear();
            params.put("deviceName", eobj.getDeviceName());
            List<Mac> demacs = this.macService.groupByObjByMap2(params);
            if(demacs.size() > 0){
                for (Mac demac : demacs){
                    params.clear();
                    params.put("deviceName", demac.getDeviceName());
                    params.put("remoteDevice", demac.getRemoteDevice());
                    List<Mac> macs = this.macService.selectByMap(params);
                    if(macs.size() >= 2){
                        for(Mac mac : macs){
                            params.clear();
                            params.put("tag", "L");
                            params.put("device_name", mac.getRemoteDevice());
                            params.put("mac", mac.getMac());
                            List<Mac> remoteMacs = this.macService.selectByMap(params);
                            for(Mac remoteMac : remoteMacs){
                                if(com.metoo.nspm.core.utils.StringUtils.isInteger(remoteMac.getIndex())){
                                    if(remoteMac.getIndex() != null && Integer.parseInt(remoteMac.getIndex()) < 4096){
                                        mac.setTag("DE");
                                        this.macService.update(mac);
                                    }
                                    if(remoteMac.getIndex() != null && Integer.parseInt(remoteMac.getIndex()) >= 4096){
                                        mac.setTag("E");
                                        this.macService.update(mac);
                                        break;
                                    }
                                }

                            }
                        }
                    }
                }
            }

        }
//        params.clear();
//        params.put("tag", "E");
//        List<Mac> emacs = this.macService.selectByMap(params);
//        if(emacs.size() > 0){
//            for(Mac mac : emacs){
//                if(mac.getIndex() != null){
//                    String macAdr = mac.getMac();
//                    macAdr = macAdr.substring(macAdr.length() - 1);
//                    if(Integer.parseInt(macAdr) == 1){
//                        mac.setTag("DE");
//                        this.macService.update(mac);
//                    }
//                }
//            }
//        }
//        params.clear();
//        params.put("tag", "E");
//        List<Mac> emacs = this.macService.selectByMap(params);
//        if(emacs.size() > 0){
//            for(Mac mac : emacs){
//                params.clear();
//                params.put("tag", "L");
//                params.put("mac", mac.getMac());
//                List<Mac> lmacs = this.macService.selectByMap(params);
//                boolean flag = false;
//                if(lmacs.size() > 0){
//                for(Mac macAddress : lmacs){
////                    if(macAddress.getIndex() != null && macAddress.getIndex() < 4096){
//                    if(macAddress.getIndex() != null){
//                        Integer index = macAddress.getIndex();
//                        String indexStr = index.toString();
//                        indexStr = indexStr.substring(indexStr.length() - 1);
//                        if(Integer.parseInt(indexStr) == 1){
//                            flag = true;
//                            break;
//                          }
//                        }
//                    }
//                }
//                if(flag){
//                    mac.setTag("DE");
//                    this.macService.update(mac);
//                }
//            }
//        }
        // mac|arp联查
        params.clear();
        params.put("tag", "S");
        List<Mac> macs = this.macService.macJoinArp(params);
        macs.forEach(item ->{
            this.macService.update(item);
        });
    }

    public Map<String, List<Object>> getShorMask(Map<String, List<Object>> parentMap){
//        String parentIp = null;
        Integer shorMask = 0;
        for (Map.Entry<String, List<Object>> entry : parentMap.entrySet()){
            String ip = entry.getKey();
            int index = ip.indexOf("/");
            int mask = Integer.parseInt(ip.substring(index + 1));
            if(mask > shorMask || shorMask == 0){
                shorMask = mask;
            }
        }
        // 遍历parentMap 获取掩码位等于parentmask网段集合

        Map<String, List<Object>> map = new HashMap<>();

        for (Map.Entry<String, List<Object>> entry : parentMap.entrySet()){
            String ipMask = entry.getKey();
            int index = ipMask.indexOf("/");
            int mask = Integer.parseInt(ipMask.substring(index + 1));
            // 判断当前mask是否等于最短mask
            if(mask != shorMask){
                map.put(ipMask, parentMap.get(ipMask));
            }
        }
        for (Map.Entry<String, List<Object>> entry : parentMap.entrySet()){
            String ipMask = entry.getKey();
            int index = ipMask.indexOf("/");
            int mask = Integer.parseInt(ipMask.substring(index + 1));
            String ip = ipMask.substring(0, index);
            Integer parentMask = null;
            // 判断当前mask是否等于最短mask
            if(mask == shorMask){
                // 同为最低等级mask/创建上级
                if (mask > 24) {
                    parentMask = 24;
                } else if (24 >= mask && mask > 16) {
                    parentMask = 16;
                } else if (16 >= mask && mask > 8) {
                    parentMask = 8;
                }
                // 生成上级网段
                String parentIp = this.getParentIp(ip, parentMask);
                parentIp = parentIp + "/" + parentMask;
                // 比较是否已经存在
                if(parentMap.get(parentIp) != null){

                    List<Object> list = map.get(parentIp);

                    List<Object> childs = parentMap.get(ipMask);

                    Map child = new HashMap();

                    child.put(ipMask, childs);

                    list.add(child);

                    map.put(parentIp, list);

                }else{
                    List<Object> list =  new ArrayList<>();

                    List<Object> childs = parentMap.get(ipMask);

                    Map child = new HashMap();
                    child.put(ipMask, childs);

                    list.add(child);

                    map.put(parentIp, list);
                }
            }
        }
        return map;
    }

    /**
     *
     * @param ip
     * @param mask
     * @return
     */
    public String getParentIp(String ip, Integer mask){
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
