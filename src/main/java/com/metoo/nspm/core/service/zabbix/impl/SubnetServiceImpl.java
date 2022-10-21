package com.metoo.nspm.core.service.zabbix.impl;

import com.metoo.nspm.core.mapper.zabbix.ZabbixSubnetMapper;
import com.metoo.nspm.core.service.zabbix.ZabbixSubnetService;
import com.metoo.nspm.core.utils.network.IpUtil;
import com.metoo.nspm.entity.zabbix.Subnet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubnetServiceImpl implements ZabbixSubnetService {

    @Autowired
    private ZabbixSubnetMapper zabbixSubnetMapper;

    @Override
    public Subnet selectObjById(Long id) {
        return this.zabbixSubnetMapper.selectObjById(id);
    }

    @Override
    public Subnet selectObjByIp(String ip, Integer mask) {
        return this.zabbixSubnetMapper.selectObjByIp(IpUtil.ipConvertDec(ip), mask);
    }

    @Override
    public List<Subnet> selectSubnetByParentId(Long id) {
        return this.zabbixSubnetMapper.selectSubnetByParentId(id);
    }

    @Override
    public int save(Subnet subnet) {
        if(subnet != null){
           return this.zabbixSubnetMapper.save(subnet);
        }
        return 0;
    }

    @Override
    public int delete(Long id) {
        try {
            return this.zabbixSubnetMapper.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
