package com.metoo.nspm.core.mapper.zabbix;

import com.metoo.nspm.entity.zabbix.IpAddress;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IpAddressMapper {

    List<IpAddress> selectObjByMap(Map map);

    IpAddress selectObjByMac(String mac);

    IpAddress selectObjByIp(String ip);

    int save(IpAddress instance);

    int update(IpAddress instance);

    void truncateTable();

    IpAddress querySrcDevice(Map params);
}
