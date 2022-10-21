package com.metoo.nspm.core.service;

import com.metoo.nspm.dto.GroupDto;
import com.metoo.nspm.entity.Group;

import java.util.List;
import java.util.Map;

public interface IGroupService {

    // 查询所有组
    List<Group> query(Map map);

    Group selectObjById(Long id);

    Group getObjByLevel(String level);

    // 获取子集
    List<Group> queryChild(Long id);


    boolean save(GroupDto instance);

    boolean del(Long id);

}
