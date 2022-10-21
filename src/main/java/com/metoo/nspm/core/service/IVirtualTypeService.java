package com.metoo.nspm.core.service;


import com.metoo.nspm.entity.VirtualType;

import java.util.List;
import java.util.Map;

public interface IVirtualTypeService {

    VirtualType getObjById(Long id);

    List<VirtualType> selectByMap(Map map);
}
