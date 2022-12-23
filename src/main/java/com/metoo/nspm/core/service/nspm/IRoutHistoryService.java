package com.metoo.nspm.core.service.nspm;

import com.github.pagehelper.Page;
import com.metoo.nspm.dto.zabbix.RoutDTO;
import com.metoo.nspm.entity.nspm.Rout;

import java.util.List;
import java.util.Map;

public interface IRoutHistoryService {

    Rout selectObjById(Long id);

    List<Rout> selectObjByMap(Map params);

    Page<Rout> selectConditionQuery(RoutDTO instance);

    Rout selectDestDevice(Map params);

    int batchDelete(List<Rout> routs);

    void copyRoutTemp();
}
