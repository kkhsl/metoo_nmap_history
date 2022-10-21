package com.metoo.nspm.core.service.zabbix;

import com.metoo.nspm.entity.zabbix.LocalIpAddress;

import java.util.List;
import java.util.Map;

public interface ILocalIpAddressService {

    List<LocalIpAddress> selectObjByMap(Map map);
    int save(LocalIpAddress instance);
    void truncateTable();
}
