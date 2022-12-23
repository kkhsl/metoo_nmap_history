package com.metoo.nspm.core.service.zabbix;

import com.metoo.nspm.entity.nspm.ProblemTemp;

import java.util.List;

public interface IProblemTempService {

    void truncateTable();

    int batchInsert(List<ProblemTemp> instance);

    ProblemTemp selectObjByObjectId(Integer objectid);

    int update(ProblemTemp instance);

}
