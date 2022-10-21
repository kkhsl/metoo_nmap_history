package com.metoo.nspm.core.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.metoo.nspm.core.mapper.DeviceConfigMapper;
import com.metoo.nspm.core.service.IDeviceConfigService;
import com.metoo.nspm.dto.DeviceConfigDTO;
import com.metoo.nspm.entity.DeviceConfig;
import com.metoo.nspm.entity.NetworkElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DeviceConfigServiceImpl implements IDeviceConfigService {

    @Autowired
    private DeviceConfigMapper deviceConfigMapper;

    @Override
    public DeviceConfig selectObjById(Long id) {
        DeviceConfig deviceConfig = this.deviceConfigMapper.selectObjById(id);
        return deviceConfig;
    }

    @Override
    public Page<DeviceConfig> selectConditionQuery(DeviceConfigDTO dto) {
        Page<DeviceConfig> page = PageHelper.startPage(dto.getCurrentPage(), dto.getPageSize());
        this.deviceConfigMapper.selectConditionQuery(dto);
        return page;
    }

    @Override
    public List<DeviceConfig> selectObjByMap(Map params) {
        return this.deviceConfigMapper.selectObjByMap(params);
    }

    @Override
    public int save(DeviceConfig instance) {
        return this.deviceConfigMapper.save(instance);
    }

    @Override
    public int update(DeviceConfig instance) {
        return this.deviceConfigMapper.update(instance);
    }

    @Override
    public int delete(Long id) {
        return this.deviceConfigMapper.delete(id);
    }
}
