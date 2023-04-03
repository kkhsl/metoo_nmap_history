package com.metoo.nspm.core.service.nspm;

import com.github.pagehelper.Page;
import com.metoo.nspm.dto.MacDTO;
import com.metoo.nspm.entity.nspm.Mac;

import java.util.List;
import java.util.Map;

public interface IMacHistoryService {

    Page<Mac> selectObjConditionQuery(MacDTO dto);

    List<Mac> selectObjByMap(Map params);

    List<Mac> selectByMap(Map params);

    int update(Mac instance);

    int deleteObjByMap(Map params);

    int batchDelete(List<Long> ids);

    void copyMacTemp();
}
