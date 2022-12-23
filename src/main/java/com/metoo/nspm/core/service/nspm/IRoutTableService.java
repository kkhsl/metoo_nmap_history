package com.metoo.nspm.core.service.nspm;

import com.metoo.nspm.entity.nspm.RoutTable;

import java.util.List;
import java.util.Map;

public interface IRoutTableService {

    List<RoutTable> selectObjByMap(Map params);

    RoutTable selectObjByMac(String mac);

    RoutTable selectObjByIp(String ip);

    int save(RoutTable instance);

    int update(RoutTable instance);

    void truncateTable();
}
