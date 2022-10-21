package com.metoo.nspm.core.service.impl;

import com.metoo.nspm.core.mapper.PolicyStatisticalMapper;
import com.metoo.nspm.core.service.IPolicyStatisticalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PolicyStatisticalServiceImpl implements IPolicyStatisticalService {

    @Autowired
    private PolicyStatisticalMapper policyStatisticalMapper;

    @Override
    public Double getObjByCode(String code) {
        return this.policyStatisticalMapper.getObjByCode(code);
    }
}
