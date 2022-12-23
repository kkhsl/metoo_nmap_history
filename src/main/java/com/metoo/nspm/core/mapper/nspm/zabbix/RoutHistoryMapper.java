package com.metoo.nspm.core.mapper.nspm.zabbix;

import com.metoo.nspm.dto.zabbix.RoutDTO;
import com.metoo.nspm.entity.nspm.Rout;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoutHistoryMapper {

    Rout selectObjById(Long id);

    List<Rout> selectObjByMap(Map params);

    List<Rout> selectConditionQuery(RoutDTO instance);

    Rout selectDestDevice(Map params);

    int batchDelete(List<Rout> macs);

    void copyRoutTemp();

}
