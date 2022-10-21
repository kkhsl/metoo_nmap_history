package com.metoo.nspm.core.service;

import com.metoo.nspm.dto.NetworkElementDto;
import com.metoo.nspm.entity.NetworkElement;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface INetworkElementService {

    NetworkElement selectObjById(Long id);

    Page<NetworkElement> selectConditionQuery(NetworkElementDto instance);

    List<NetworkElement> selectObjByMap(Map params);

    int save(NetworkElement instance);

    int update(NetworkElement instance);

    int del(Long id);

}
