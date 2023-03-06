package com.metoo.nspm.core.mapper.nspm;

import com.metoo.nspm.entity.nspm.SpanningTreeProtocol;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SpanningTreeProtocolMapper {

    List<SpanningTreeProtocol> selectObjByMap(Map params);

    public void truncateTable();

    void copyMacTemp();

}
