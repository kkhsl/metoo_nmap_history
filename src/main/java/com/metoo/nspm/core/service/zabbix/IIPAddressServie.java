package com.metoo.nspm.core.service.zabbix;

import com.metoo.nspm.entity.zabbix.IpAddress;

import java.util.List;
import java.util.Map;

public interface IIPAddressServie {

    List<IpAddress> selectObjByMap(Map map);

    IpAddress selectObjByMac(String mac);

    IpAddress selectObjByIp(String ip);

    int save(IpAddress instance);

    int update(IpAddress instance);

    void truncateTable();
    IpAddress querySrcDevice(Map params);
}
