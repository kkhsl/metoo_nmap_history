package com.metoo.nspm.core.service.nspm;

import com.metoo.nspm.entity.nspm.TerminalType;

import java.util.List;
import java.util.Map;

public interface ITerminalTypeService {

    TerminalType selectObjById(Long id);

    TerminalType selectObjByType(Integer type);

    List<TerminalType> selectObjByMap(Map params);

    List<TerminalType> selectObjAll();
}

