package com.metoo.nspm.core.service.zabbix;

import com.metoo.nspm.entity.zabbix.Problem;

import java.util.List;
import java.util.Map;

public interface IProblemService {

    List<Problem> selectObjByMap(Map params);

    int selectCount(Map params);

    void truncateTable();

    void copyProblemTemp(Map params);
}
