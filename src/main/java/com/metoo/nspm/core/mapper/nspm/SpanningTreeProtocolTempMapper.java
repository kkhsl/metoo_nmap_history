package com.metoo.nspm.core.mapper.nspm;

import com.metoo.nspm.entity.nspm.SpanningTreeProtocol;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SpanningTreeProtocolTempMapper {

    SpanningTreeProtocol selectObjById(Long id);

    List<SpanningTreeProtocol> selectObjByInstance(String instance);

    List<SpanningTreeProtocol> selectObjByMap(Map params);

    public void truncateTable();

    void batchInsert(List<SpanningTreeProtocol> instances);

    void batchUpdate(List<SpanningTreeProtocol> instances);

}
