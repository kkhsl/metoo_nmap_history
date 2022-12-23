package com.metoo.nspm.core.mapper.nspm.zabbix;

import com.metoo.nspm.entity.zabbix.Problem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface NspmProoblemMapper {

    List<Problem> selectObjByMap(Map params);

    void truncateTable();

    void copyProblemTemp(Map params);

}
