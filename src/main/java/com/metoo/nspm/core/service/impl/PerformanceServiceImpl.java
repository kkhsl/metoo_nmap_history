package com.metoo.nspm.core.service.impl;

import com.metoo.nspm.core.mapper.PerformenceMapper;
import com.metoo.nspm.core.service.IPerformanceService;
import com.metoo.nspm.entity.Performance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PerformanceServiceImpl implements IPerformanceService {

    @Autowired
    private PerformenceMapper performenceMapper;

    @Override
    public Performance getObjBy(Map params) {
        return this.performenceMapper.getObjBy(params);
    }
}
