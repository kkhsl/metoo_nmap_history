package com.metoo.nspm.core.mapper;

import com.metoo.nspm.entity.DeviceType;
import com.metoo.nspm.vo.DeviceTypeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DeviceTypeMapper {

    DeviceType selectObjById(Long id);

    List<DeviceType> selectConditionQuery();

    List<DeviceType> selectObjByMap(Map params);

    List<DeviceType> selectCountByLeftJoin();

    List<DeviceType> selectCountByJoin();

    List<DeviceTypeVO> statistics();

}
