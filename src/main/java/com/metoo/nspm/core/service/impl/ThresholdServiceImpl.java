package com.metoo.nspm.core.service.impl;

import com.metoo.nspm.core.mapper.ThresholdMapper;
import com.metoo.nspm.core.service.IThresholdService;
import com.metoo.nspm.entity.Threshold;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ThresholdServiceImpl implements IThresholdService {

    @Autowired
    private ThresholdMapper thresholdMapper;

    @Override
    public Threshold query() {
        return this.thresholdMapper.query();
    }

    @Override
    public int update(Threshold instance) {
        return this.thresholdMapper.update(instance);
    }
}
