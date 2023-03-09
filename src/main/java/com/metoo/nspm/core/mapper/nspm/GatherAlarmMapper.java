package com.metoo.nspm.core.mapper.nspm;

import com.metoo.nspm.dto.GatherAlarmDTO;
import com.metoo.nspm.entity.nspm.GatherAlarm;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GatherAlarmMapper {

    List<GatherAlarm> selectConditionQuery(GatherAlarmDTO dto);

    List<GatherAlarm> selectObjByMap(Map params);

    int save(GatherAlarm instance);

    int update(GatherAlarm instance);
}
