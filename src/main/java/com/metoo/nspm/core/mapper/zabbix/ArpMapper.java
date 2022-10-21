package com.metoo.nspm.core.mapper.zabbix;

import com.metoo.nspm.entity.zabbix.Arp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArpMapper {

    List<Arp> selectObjByDistinct();

    List<Arp> selectOppositeByMap(Map params);

    List<Arp> selectObjByInterface(Map params);

    List<Arp> selectObjByGroupMap(Map params);

    List<Arp> selectObjByGroupHavingInterfaceName(Map params);

    List<Arp> selectObjByMac(Map params);

    List<Arp> selectES(Map params);

    List<Arp> selectEAndRemote(Map params);

    List<Arp> selectSubquery(Map params);

    List<Arp> selectObjByMap(Map params);

    List<Arp> selectDistinctObjByMap(Map params);

    List<Arp> arpTag(Map params);

    List<Arp> selectGroupByHavingMac(Map params);

    Arp selectObjByIp(String ip);

    int save(Arp arp);

    int update(Arp instance);

    void truncateTable();
}
