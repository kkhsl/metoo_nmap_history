package com.metoo.nspm.core.service.nspm.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.metoo.nspm.core.mapper.nspm.zabbix.RoutHistoryMapper;
import com.metoo.nspm.core.service.nspm.IRoutHistoryService;
import com.metoo.nspm.dto.zabbix.RoutDTO;
import com.metoo.nspm.entity.nspm.Rout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RoutHistoryServiceImpl implements IRoutHistoryService {

    @Autowired
    private RoutHistoryMapper routHistoryMapper;

    @Override
    public Rout selectObjById(Long id) {
        return this.routHistoryMapper.selectObjById(id);
    }

    @Override
    public Page<Rout> selectConditionQuery(RoutDTO instance) {
        if(instance == null){
            instance = new RoutDTO();
        }
        Page<Rout> page = PageHelper.startPage(instance.getCurrentPage(), instance.getPageSize());
        this.routHistoryMapper.selectConditionQuery(instance);
        return page;
    }

    @Override
    public Rout selectDestDevice(Map params) {
        return this.routHistoryMapper.selectDestDevice(params);
    }

    @Override
    public int batchDelete(List<Rout> routs) {
        return this.routHistoryMapper.batchDelete(routs);
    }

    @Override
    public List<Rout> selectObjByMap(Map params) {
        return this.routHistoryMapper.selectObjByMap(params);
    }

    @Override
    public void copyRoutTemp() {
        this.routHistoryMapper.copyRoutTemp();
    }


}
