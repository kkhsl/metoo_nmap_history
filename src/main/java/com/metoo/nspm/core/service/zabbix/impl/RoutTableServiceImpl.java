package com.metoo.nspm.core.service.zabbix.impl;

import com.metoo.nspm.core.mapper.zabbix.RoutTableMapper;
import com.metoo.nspm.core.service.zabbix.IRoutTableService;
import com.metoo.nspm.entity.zabbix.RoutTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RoutTableServiceImpl implements IRoutTableService {

    @Autowired
    private RoutTableMapper routTableMapper;

    @Override
    public List<RoutTable> selectObjByMap(Map map) {
        return this.routTableMapper.selectObjByMap(map);
    }

    @Override
    public RoutTable selectObjByMac(String mac) {
        return this.routTableMapper.selectObjByMac(mac);
    }

    @Override
    public RoutTable selectObjByIp(String ip) {
        return this.routTableMapper.selectObjByIp(ip);
    }

    @Override
    public int save(RoutTable instance) {
        if(instance.getId() == null){
            instance.setAddTime(new Date());
            try {
                return this.routTableMapper.save(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }else{
            try {
                this.routTableMapper.update(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    @Override
    public int update(RoutTable instance) {
        return this.routTableMapper.update(instance);
    }

    @Override
    public void truncateTable() {
        this.routTableMapper.truncateTable();
    }
}
