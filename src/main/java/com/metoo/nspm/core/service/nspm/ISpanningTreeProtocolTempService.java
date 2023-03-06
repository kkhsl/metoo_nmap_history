package com.metoo.nspm.core.service.nspm;

import com.metoo.nspm.entity.nspm.SpanningTreeProtocol;

import java.util.List;
import java.util.Map;

public interface ISpanningTreeProtocolTempService {

    SpanningTreeProtocol selectObjById(Long id);

    List<SpanningTreeProtocol> selectObjByInstance(String instance);

    List<SpanningTreeProtocol> selectObjByMap(Map params);

    void batchInsert(List<SpanningTreeProtocol> instances);

    void batchUpdate(List<SpanningTreeProtocol> instances);

    void truncateTable();
}
