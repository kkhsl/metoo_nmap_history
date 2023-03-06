package com.metoo.nspm.core.service.nspm.impl;

import com.metoo.nspm.core.mapper.nspm.SpanningTreeProtocolMapper;
import com.metoo.nspm.core.service.nspm.ISpanningTreeProtocolService;
import com.metoo.nspm.entity.nspm.SpanningTreeProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpanningTreeProtocolServiceImpl implements ISpanningTreeProtocolService {

    @Autowired
    private SpanningTreeProtocolMapper spanningTreeProtocolMapper;

    @Override
    public List<SpanningTreeProtocol> selectObjByMap(Map params) {
        return this.spanningTreeProtocolMapper.selectObjByMap(params);
    }

    @Override
    public void truncateTable() {
        this.spanningTreeProtocolMapper.truncateTable();
    }

    @Override
    public void copyMacTemp() {
        this.spanningTreeProtocolMapper.copyMacTemp();
    }
}
