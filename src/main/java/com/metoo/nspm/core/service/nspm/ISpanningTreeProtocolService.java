package com.metoo.nspm.core.service.nspm;

import com.metoo.nspm.entity.nspm.SpanningTreeProtocol;

import java.util.List;
import java.util.Map;

public interface ISpanningTreeProtocolService {

    List<SpanningTreeProtocol> selectObjByMap(Map params);

    void truncateTable();

    public void copyMacTemp();
}
