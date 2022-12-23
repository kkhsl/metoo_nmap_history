package com.metoo.nspm.core.service.nspm;

import com.github.pagehelper.Page;
import com.metoo.nspm.dto.zabbix.RoutDTO;
import com.metoo.nspm.entity.nspm.Rout;

import java.util.List;
import java.util.Map;

public interface IRoutService {

    Rout selectObjById(Long id);
    Page<Rout> selectConditionQuery(RoutDTO instance);
    List<Rout> selectObjByMap(Map params);
    int save(Rout instance);
    int update(Rout instance);
    int delete(Long id);
    void truncateTable();
    List<Rout> queryDestDevice(Map params);

    Rout selectDestDevice(Map params);

    List<Rout> selectNextHopDevice(Map params);

    void copyRoutTemp();
}
