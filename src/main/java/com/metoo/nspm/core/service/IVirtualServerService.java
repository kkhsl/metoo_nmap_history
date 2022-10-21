package com.metoo.nspm.core.service;

import com.metoo.nspm.dto.VirtualServerDto;
import com.metoo.nspm.entity.VirtualServer;
import com.github.pagehelper.Page;

import java.util.Map;

public interface IVirtualServerService {

    VirtualServer getObjById(Long id);

    Page<VirtualServer> selectList(VirtualServerDto instance);

    int insert(VirtualServer instance);

    int update(VirtualServer instance);

    int deleteById(Long id);

    int deleteByMap(Map map);
}
