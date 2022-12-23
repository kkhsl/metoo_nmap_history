package com.metoo.nspm.core.mapper.nspm.zabbix;

import com.metoo.nspm.entity.nspm.RoutTable;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoutTableMapper {


    RoutTable selectObjByMac(String mac);

    RoutTable selectObjByIp(String ip);

    List<RoutTable> selectObjByMap(Map params);

    int save(RoutTable instance);

    int update(RoutTable instance);

    void truncateTable();

}
