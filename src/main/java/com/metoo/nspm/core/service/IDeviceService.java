package com.metoo.nspm.core.service;

import com.metoo.nspm.entity.Device;

import java.util.List;

public interface IDeviceService {

    Device selectObjById(Long id);

    /**
     * 查询所有设备类型
     * @return
     */
    List<Device> selectConditionQuery();
}
