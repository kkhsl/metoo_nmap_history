package com.metoo.nspm.core.service.impl;

import com.metoo.nspm.core.mapper.TopologyMapper;
import com.metoo.nspm.core.service.ITopologyService;
import com.metoo.nspm.dto.TopologyDTO;
import com.metoo.nspm.entity.Topology;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TopologyServiceImpl implements ITopologyService {

    @Autowired
    private TopologyMapper topologyMapper;

    @Override
    public Topology selectObjById(Long id) {
        return this.topologyMapper.selectObjById(id);
    }

    @Override
    public Page<Topology> selectConditionQuery(TopologyDTO instance) {
        if(instance == null){
            instance = new TopologyDTO();
        }
        Page<Topology> page = PageHelper.startPage(instance.getCurrentPage(), instance.getPageSize());

        this.topologyMapper.selectConditionQuery(instance);
        return page;
    }

    @Override
    public List<Topology> selectObjByMap(Map params) {
        return this.topologyMapper.selectObjByMap(params);
    }

    @Override
    public List<Topology> selectTopologyByMap(Map params) {
        return this.topologyMapper.selectTopologyByMap(params);
    }

    @Override
    public int save(Topology instance) {
        if(instance.getId() == null){
            instance.setAddTime(new Date());
        }else{
            instance.setUpdateTime(new Date());
        }
        if(instance.getId() == null){
            try {
                 this.topologyMapper.save(instance);
                return instance.getId().intValue();
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }else{
            try {
                return this.topologyMapper.update(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    @Override
    public int update(Topology instance) {
        try {
            return this.topologyMapper.update(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int delete(Long id) {
        try {
            return this.topologyMapper.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
