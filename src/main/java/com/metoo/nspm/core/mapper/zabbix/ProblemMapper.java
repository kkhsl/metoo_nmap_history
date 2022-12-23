package com.metoo.nspm.core.mapper.zabbix;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface ProblemMapper {

    int selectCount(Map params);

}
