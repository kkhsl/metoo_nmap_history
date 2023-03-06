package com.metoo.nspm.core.service.nspm.impl;

import com.metoo.nspm.core.mapper.nspm.SpanningTreeProtocolMapper;
import com.metoo.nspm.core.mapper.nspm.SpanningTreeProtocolTempMapper;
import com.metoo.nspm.core.service.nspm.ISpanningTreeProtocolTempService;
import com.metoo.nspm.entity.nspm.SpanningTreeProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpanningTreeProtocolTempServiceImpl implements ISpanningTreeProtocolTempService {

    @Autowired
    private SpanningTreeProtocolTempMapper spanningTreeProtocolTempMapper;

    @Override
    public SpanningTreeProtocol selectObjById(Long id) {
        return this.spanningTreeProtocolTempMapper.selectObjById(id);
    }

    @Override
    public List<SpanningTreeProtocol> selectObjByInstance(String instance) {
        return this.spanningTreeProtocolTempMapper.selectObjByInstance(instance);
    }

    @Override
    public List<SpanningTreeProtocol> selectObjByMap(Map params) {
        return this.spanningTreeProtocolTempMapper.selectObjByMap(params);
    }

    @Override
    public void batchInsert(List<SpanningTreeProtocol> instances) {
        this.spanningTreeProtocolTempMapper.batchInsert(instances);
    }

    @Override
    public void batchUpdate(List<SpanningTreeProtocol> instances) {
        this.spanningTreeProtocolTempMapper.batchUpdate(instances);
    }

    @Override
    public void truncateTable() {
        this.spanningTreeProtocolTempMapper.truncateTable();
    }
}
