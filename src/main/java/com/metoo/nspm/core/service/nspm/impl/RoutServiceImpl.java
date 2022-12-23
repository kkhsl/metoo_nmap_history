package com.metoo.nspm.core.service.nspm.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.metoo.nspm.core.mapper.nspm.zabbix.ZabbixRoutMapper;
import com.metoo.nspm.core.service.nspm.IRoutService;
import com.metoo.nspm.dto.zabbix.RoutDTO;
import com.metoo.nspm.entity.nspm.Rout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RoutServiceImpl implements IRoutService {

    @Autowired
    private ZabbixRoutMapper zabbixRoutMapper;

    @Override
    public Rout selectObjById(Long id) {
        return this.zabbixRoutMapper.selectObjById(id);
    }

    @Override
    public Page<Rout> selectConditionQuery(RoutDTO instance) {
        if(instance == null){
            instance = new RoutDTO();
        }
        Page<Rout> page = PageHelper.startPage(instance.getCurrentPage(), instance.getPageSize());
        this.zabbixRoutMapper.selectConditionQuery(instance);
        return page;
    }

    @Override
    public List<Rout> selectObjByMap(Map params) {
        return this.zabbixRoutMapper.selectObjByMap(params);
    }

    @Override
    public int save(Rout instance) {
        try {
            if(instance.getId() == null){
                instance.setAddTime(new Date());
            }
            return this.zabbixRoutMapper.save(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int update(Rout instance) {
        try {
            return this.zabbixRoutMapper.update(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int delete(Long id) {
        try {
            return this.zabbixRoutMapper.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void truncateTable() {
        this.zabbixRoutMapper.truncateTable();
    }

    @Override
    public List<Rout> queryDestDevice(Map params) {
        return this.zabbixRoutMapper.queryDestDevice(params);
    }

    @Override
    public Rout selectDestDevice(Map params) {
        return this.zabbixRoutMapper.selectDestDevice(params);
    }

    @Override
    public List<Rout> selectNextHopDevice(Map params) {
        return this.zabbixRoutMapper.selectNextHopDevice(params);
    }

    @Override
    public void copyRoutTemp() {
        this.zabbixRoutMapper.copyRoutTemp();
    }


}
