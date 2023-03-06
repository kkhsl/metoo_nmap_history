package com.metoo.nspm.core.service.nspm.impl;

import com.metoo.nspm.core.mapper.nspm.SpanningTreeProtocolHistoryMapper;
import com.metoo.nspm.core.service.nspm.ISpanningTreeProtocolHistoryService;
import com.metoo.nspm.core.service.nspm.ISpanningTreeProtocolTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SpanningTreeProtocolHistoryServiceImpl implements ISpanningTreeProtocolHistoryService {

    @Autowired
    private SpanningTreeProtocolHistoryMapper spanningTreeProtocolHistoryMapper;

    @Override
    public void truncateTable() {
        this.spanningTreeProtocolHistoryMapper.truncateTable();
    }

    @Override
    public void copyMacTemp() {
        this.spanningTreeProtocolHistoryMapper.copyMacTemp();
    }
}
