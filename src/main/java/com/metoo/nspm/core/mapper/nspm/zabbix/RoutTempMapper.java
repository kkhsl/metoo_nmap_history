package com.metoo.nspm.core.mapper.nspm.zabbix;

import com.metoo.nspm.entity.nspm.RoutTemp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoutTempMapper {

    RoutTemp selectObjById(Long id);
    List<RoutTemp> selectObjByMap(Map params);
    int save(RoutTemp instance);
    int update(RoutTemp instance);
    int delete(Long id);
    void truncateTable();
    List<RoutTemp> queryDestDevice(Map params);
    RoutTemp selectDestDevice(Map params);
    List<RoutTemp> selectNextHopDevice(Map params);

}
