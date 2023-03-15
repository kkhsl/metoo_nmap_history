package com.metoo.nspm.core.service.nspm.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.metoo.nspm.core.mapper.nspm.zabbix.MacHistoryMapper;
import com.metoo.nspm.core.service.nspm.IMacHistoryService;
import com.metoo.nspm.dto.MacDTO;
import com.metoo.nspm.entity.nspm.Mac;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MacHistoryServiceImpl implements IMacHistoryService {

    @Autowired
    private MacHistoryMapper macHistoryMapper;

    @Override
    public Page<Mac> selectObjConditionQuery(MacDTO dto) {
        if(dto == null){
            dto = new MacDTO();
        }
        Page<Mac> page = PageHelper.startPage(dto.getCurrentPage(), dto.getPageSize());
        this.macHistoryMapper.selectObjConditionQuery(dto);
        return page;
    }

    @Override
    public List<Mac> selectObjByMap(Map map) {
        return this.macHistoryMapper.selectObjByMap(map);
    }

    @Override
    public List<Mac> selectByMap(Map params) {
        return this.macHistoryMapper.selectByMap(params);
    }

    @Override
    public int update(Mac instance) {
        try {
            return this.macHistoryMapper.update(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int batchDelete(List<Mac> macs) {
        return this.macHistoryMapper.batchDelete(macs);
    }

    @Override
    public void copyMacTemp() {
        this.macHistoryMapper.copyMacTemp();
    }

}
