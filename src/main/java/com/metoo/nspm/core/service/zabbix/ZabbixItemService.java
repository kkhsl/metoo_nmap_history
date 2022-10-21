package com.metoo.nspm.core.service.zabbix;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.metoo.nspm.dto.zabbix.ItemDTO;

import java.util.List;
import java.util.Map;

public interface ZabbixItemService {

    JSONObject getItem(ItemDTO dto);// 这里优化

    // 获取items
    public JSONArray getItemByIpAndTag(String ip, List tags, List output);

    JSONArray getItemRout(String ip);

    JSONArray getItemRoutTag(String ip);

    // 梳理网段
    JSONArray getItemIpAddress(String ip);

    JSONArray getItemIpAddressTag(String ip);

    JSONArray getItemIpAddressTagByIndex(String ip, Integer index);

    JSONArray getItemOperationalTagByIndex(String ip, Integer index);

    JSONArray getItemTags(String ip);

    JSONArray getItemMac(String ip);

    JSONArray getItemMacTag(String ip);

    JSONArray getItemArpTag(String ip);

    JSONArray getItemInterfacesByIndex(String ip, Integer index);

    JSONArray getItemInterfacesTagByIndex(String ip, String index);

    JSONArray getItemIfIndexByIndexTag(String ip);

    JSONArray getItemInterfacesTag(String ip);

    JSONArray getItemInterfaces(String ip);

    Map<String, List<Object>> ipAddressCombing(JSONArray items);

    void arpTag();

    void macTag();
}
