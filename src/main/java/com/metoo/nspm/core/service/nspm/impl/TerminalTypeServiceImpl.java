package com.metoo.nspm.core.service.nspm.impl;

import com.metoo.nspm.core.mapper.nspm.TerminalTypeMapper;
import com.metoo.nspm.core.service.nspm.ITerminalTypeService;
import com.metoo.nspm.entity.nspm.TerminalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TerminalTypeServiceImpl implements ITerminalTypeService {


    @Autowired
    private TerminalTypeMapper terminalTypeMapper;

    @Override
    public TerminalType selectObjById(Long id) {
        return this.terminalTypeMapper.selectObjById(id);
    }

    @Override
    public TerminalType selectObjByType(Integer type) {
        return this.terminalTypeMapper.selectObjByType(type);
    }

    @Override
    public List<TerminalType> selectObjByMap(Map params) {
        return this.terminalTypeMapper.selectObjByMap(params);
    }

    @Override
    public List<TerminalType> selectObjAll() {
        return this.terminalTypeMapper.selectObjAll();
    }
}
