package com.metoo.nspm.core.service;

import com.metoo.nspm.entity.OperationSystem;

import java.util.List;
import java.util.Map;

public interface IOperationSystemService {

    OperationSystem getObjById(Long id);

    List<OperationSystem> selectByMap(Map map);
}
