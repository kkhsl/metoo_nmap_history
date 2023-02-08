package com.metoo.nspm.core.service.nspm.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.metoo.nspm.core.mapper.nspm.DeviceTypeMapper;
import com.metoo.nspm.core.service.nspm.IDeviceTypeService;
import com.metoo.nspm.dto.DeviceTypeDTO;
import com.metoo.nspm.entity.nspm.DeviceType;
import com.metoo.nspm.vo.DeviceTypeVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class DeviceTypeServiceImpl implements IDeviceTypeService {

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    @Override
    public DeviceType selectObjById(Long id) {
        return this.deviceTypeMapper.selectObjById(id);
    }

    @Override
    public DeviceType selectObjByName(String name) {
        return this.deviceTypeMapper.selectObjByName(name);
    }

    @Override
    public Page<DeviceType> selectConditionQuery(DeviceTypeDTO dto) {
        Page<DeviceType> page = PageHelper.startPage(dto.getCurrentPage(), dto.getPageSize());
        this.deviceTypeMapper.selectConditionQuery(dto);
        return page;
    }

    @Override
    public List<DeviceType> selectObjByMap(Map params) {
        return this.deviceTypeMapper.selectObjByMap(params);
    }

    @Override
    public List<DeviceType> selectCountByLeftJoin() {
        return this.deviceTypeMapper.selectCountByLeftJoin();
    }

    @Override
    public List<DeviceType> selectCountByJoin() {
        return this.deviceTypeMapper.selectCountByJoin();
    }

    @Override
    public List<DeviceType> selectDeviceTypeAndNeByJoin() {
        return this.deviceTypeMapper.selectDeviceTypeAndNeByJoin();
    }

    @Override
    public List<DeviceTypeVO> statistics() {
        return this.deviceTypeMapper.statistics();
    }

    @Override
    public int save(DeviceType instance) {
        if(instance.getId() == null || instance.getId().equals("")){
            instance.setAddTime(new Date());
        }
        if(instance.getId() == null || instance.getId().equals("")){
            try {
                return this.deviceTypeMapper.save(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }else{
            try {
                return this.deviceTypeMapper.update(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }

    }

    @Override
    public int update(DeviceType instance) {
        try {
            return this.deviceTypeMapper.update(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int delete(Long id) {
        try {
            return this.deviceTypeMapper.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int batcheDel(Long[] ids) {
        try {
            return this.deviceTypeMapper.batcheDel(ids);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
