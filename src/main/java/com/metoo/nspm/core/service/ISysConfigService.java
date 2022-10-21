package com.metoo.nspm.core.service;

import com.metoo.nspm.entity.SysConfig;

public interface ISysConfigService {

    SysConfig findObjById(Long id);

    SysConfig  findSysConfigList();

    int modify(SysConfig instance);

    boolean update(SysConfig instance);
}
