package com.metoo.nspm.core.service.impl;

import com.metoo.nspm.core.mapper.OperationSystemMapper;
import com.metoo.nspm.core.service.IOperationSystemService;
import com.metoo.nspm.entity.OperationSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OperationServiceImpl implements IOperationSystemService {

    @Autowired
    private OperationSystemMapper operationSystemMapper;

    @Override
    public OperationSystem getObjById(Long id) {
        return this.operationSystemMapper.getObjById(id);
    }

    @Override
    public List<OperationSystem> selectByMap(Map map) {
        return this.operationSystemMapper.selectByMap(map);
    }
}
