package com.metoo.nspm.core.service.zabbix;

import com.metoo.nspm.entity.zabbix.Subnet;

import java.util.List;

public interface ZabbixSubnetService {

    Subnet selectObjById(Long id);

    Subnet selectObjByIp(String ip, Integer mask);

    List<Subnet> selectSubnetByParentId(Long id);

    int save(Subnet instance);

    int delete(Long id);


}
