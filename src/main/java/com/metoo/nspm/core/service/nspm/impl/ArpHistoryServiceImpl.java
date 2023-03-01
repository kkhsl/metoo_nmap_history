package com.metoo.nspm.core.service.nspm.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.metoo.nspm.core.mapper.nspm.zabbix.ArpHistoryMapper;
import com.metoo.nspm.core.service.nspm.IArpHistoryService;
import com.metoo.nspm.dto.ArpDTO;
import com.metoo.nspm.entity.nspm.Arp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ArpHistoryServiceImpl implements IArpHistoryService {

    @Autowired
    private ArpHistoryMapper arpHistoryMapper;


    @Override
    public Arp selectObjByIp(String ip) {
        return this.arpHistoryMapper.selectObjByIp(ip);
    }

    @Override
    public Page<Arp> selectObjConditionQuery(ArpDTO dto) {
        if(dto == null){
            dto = new ArpDTO();
        }
        Page<Arp> page = PageHelper.startPage(dto.getCurrentPage(), dto.getPageSize());
        this.arpHistoryMapper.selectObjConditionQuery(dto);
        return page;
    }

    @Override
    public List<Arp> selectObjByMap(Map params) {
        return this.arpHistoryMapper.selectObjByMap(params);
    }

    @Override
    public List<Arp> selectDistinctObjByMap(Map params) {
        return this.arpHistoryMapper.selectDistinctObjByMap(params);
    }

    @Override
    public int batchDelete(List<Arp> arp) {
        return this.arpHistoryMapper.batchDelete(arp);
    }

    @Override
    public void copyArpTemp(){
        this.arpHistoryMapper.copyArpTemp();
    }
}
