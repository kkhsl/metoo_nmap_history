package com.metoo.nspm.core.service.zabbix;

import com.metoo.nspm.entity.zabbix.Mac;

import java.util.List;
import java.util.Map;

public interface IMacService {

    List<Mac> selectByMap(Map params);

    Mac getObjByInterfaceName(String interfaceName);

    List<Mac> groupByObjByMap(Map params);

    List<Mac> groupByObjByMap2(Map params);

    List<Mac> getMacUS(Map params);

    List<Mac> macJoinArp(Map params);

    int save(Mac instance);

    int update(Mac instance);

    void truncateTable();
}
