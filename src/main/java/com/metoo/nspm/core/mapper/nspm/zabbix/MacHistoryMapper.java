package com.metoo.nspm.core.mapper.nspm.zabbix;

import com.metoo.nspm.dto.MacDTO;
import com.metoo.nspm.entity.nspm.Mac;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MacHistoryMapper {

    List<Mac> selectObjConditionQuery(MacDTO dto);

    List<Mac> selectObjByMap(Map params);

    List<Mac> selectByMap(Map params);

    int update(Mac instance);

    int batchDelete(List<Mac> macs);

    void copyMacTemp();
}
