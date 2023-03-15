package com.metoo.nspm.core.mapper.nspm;

import com.metoo.nspm.entity.nspm.TerminalType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TerminalTypeMapper {

    TerminalType selectObjById(Long id);

    TerminalType selectObjByType(Integer type);

    List<TerminalType> selectObjByMap(Map params);

    List<TerminalType> selectObjAll();
}
