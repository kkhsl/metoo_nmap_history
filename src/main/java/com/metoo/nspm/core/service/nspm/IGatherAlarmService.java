package com.metoo.nspm.core.service.nspm;

import com.github.pagehelper.Page;
import com.metoo.nspm.dto.GatherAlarmDTO;
import com.metoo.nspm.entity.nspm.GatherAlarm;

import java.util.List;
import java.util.Map;

public interface IGatherAlarmService {

    Page<GatherAlarm> selectConditionQuery(GatherAlarmDTO dto);

    List<GatherAlarm> selectObjByMap(Map params);

    int save(GatherAlarm instance);

    int update(GatherAlarm instance);

    void gatherAlarms();
}
