package com.metoo.nspm.core.service;

import com.metoo.nspm.entity.DeviceType;
import com.metoo.nspm.vo.DeviceTypeVO;

import java.util.List;
import java.util.Map;

public interface IDeviceTypeService {

    DeviceType selectObjById(Long id);

    List<DeviceType> selectConditionQuery();

    List<DeviceType> selectObjByMap(Map params);

    List<DeviceType> selectCountByLeftJoin();

    List<DeviceType> selectCountByJoin();

    List<DeviceTypeVO> statistics();
}
