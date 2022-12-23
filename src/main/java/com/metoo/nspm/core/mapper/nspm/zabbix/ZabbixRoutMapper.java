package com.metoo.nspm.core.mapper.nspm.zabbix;

import com.metoo.nspm.dto.zabbix.RoutDTO;
import com.metoo.nspm.entity.nspm.Rout;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ZabbixRoutMapper {

    Rout selectObjById(Long id);
    List<Rout> selectConditionQuery(RoutDTO instance);
    List<Rout> selectObjByMap(Map params);
    int save(Rout instance);
    int update(Rout instance);
    int delete(Long id);
    void truncateTable();
    List<Rout> queryDestDevice(Map params);

    Rout selectDestDevice(Map params);
    List<Rout> selectNextHopDevice(Map params);
    void copyRoutTemp();

}
