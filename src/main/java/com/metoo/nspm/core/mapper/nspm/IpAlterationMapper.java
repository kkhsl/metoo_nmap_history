package com.metoo.nspm.core.mapper.nspm;

import com.metoo.nspm.entity.nspm.IpAlteration;

import java.util.List;
import java.util.Map;

public interface IpAlterationMapper {

    List<IpAlteration> selectObjByMap(Map params);

    List<IpAlteration> selectObjByAddTime(Map params);

    int save(IpAlteration instance);
}
